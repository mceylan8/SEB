package main.mtcg.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import main.server.http.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.sql.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserController implements Controller {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/SEB";
    private static final String DB_USER = "postgres";
    private static final String DB_PASS = "12345678";
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final AuthenticationService authenticationService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public UserController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public boolean supports(String route) {
        return route.equals("/users") || route.equals("/sessions");
    }

    @Override
    public Response handle(Request request) {
        try {
            UserDto userDto = objectMapper.readValue(request.getBody(), UserDto.class);

            switch (request.getRoute()) {
                case "/users":
                    if ("PUT".equals(request.getMethod())) {
                        return handleUserUpdate(request); // Pass userDto for updates
                    } else if ("POST".equals(request.getMethod())) {
                        return handleUserRegistration(userDto);
                    }
                    break;
                case "/sessions":
                    if ("POST".equals(request.getMethod())) {
                        return handleUserLogin(userDto);
                    }
                    break;
                default:
                    return new Response(HttpStatus.BAD_REQUEST, HttpContentType.TEXT_PLAIN, "Invalid route");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new Response(HttpStatus.BAD_REQUEST, HttpContentType.TEXT_PLAIN, "Invalid request format");
        }
        return new Response(HttpStatus.METHOD_NOT_ALLOWED, HttpContentType.TEXT_PLAIN, "Method Not Allowed");
    }
    private Response handleUserUpdate(Request request) {
        // Authentifizierung und Benutzernamen Extraktion
        System.out.println("request"+ request);
        String authToken = request.getAuthorization().substring(6); // "Basic " überspringen
        String username = authenticationService.getUsernameFromToken(authToken);

        // Autorisierungsüberprüfung
        if (username == null || !request.getRoute().endsWith(username)) {
            return new Response(HttpStatus.UNAUTHORIZED, HttpContentType.TEXT_PLAIN, "Unauthorized");
        }

        // Extraktion der zu aktualisierenden Daten aus der Anfrage
        UserDto userDto;
        try {
            userDto = objectMapper.readValue(request.getBody(), UserDto.class);
        } catch (IOException e) {
            e.printStackTrace();
            return new Response(HttpStatus.BAD_REQUEST, HttpContentType.TEXT_PLAIN, "Bad request");
        }

        // Aktualisieren des Benutzers in der Datenbank
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            // Stellen Sie sicher, dass die zu aktualisierenden Spalten und ihre Reihenfolge mit der Struktur der Datenbanktabelle "Player" übereinstimmen
            String updateQuery = "UPDATE \"Player\" SET email = ?, token = ? WHERE username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                // Die Parameter setzen
                preparedStatement.setString(1, userDto.getEmail());
                preparedStatement.setString(2, userDto.getToken()); // Token wird normalerweise serverseitig generiert und sollte nicht vom Client kommen
                preparedStatement.setString(3, username);

                // Aktualisierung durchführen und Ergebnis überprüfen
                int updatedRows = preparedStatement.executeUpdate();
                if (updatedRows == 0) {
                    return new Response(HttpStatus.NOT_FOUND, HttpContentType.TEXT_PLAIN, "User not found");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, HttpContentType.TEXT_PLAIN, "Internal Server Error");
        }

        // Erfolgreiche Antwort zurückgeben
        return new Response(HttpStatus.OK, HttpContentType.APPLICATION_JSON, "{\"message\": \"User updated\"}");
    }


    private boolean userExists(String username, Connection connection) throws SQLException {
        String query = "SELECT count(*) FROM \"Player\" WHERE username = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    private Response handleUserRegistration(UserDto userDto) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            if (userExists(userDto.getUsername(), connection)) {
                return new Response(HttpStatus.CONFLICT, HttpContentType.TEXT_PLAIN, "User already exists");
            }

            // Generiere die nächste playerId
            long nextId = getNextPlayerId(connection);

            String hashedPassword = passwordEncoder.encode(userDto.getPassword());
            String insertQuery = "INSERT INTO \"Player\" (playerid, username, password, email) VALUES (?, ?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                preparedStatement.setLong(1, nextId);
                preparedStatement.setString(2, userDto.getUsername());
                preparedStatement.setString(3, hashedPassword);
                preparedStatement.setString(4, userDto.getEmail());
                preparedStatement.executeUpdate();
            }

            return new Response(HttpStatus.CREATED, HttpContentType.APPLICATION_JSON, "{\"message\": \"User created\", \"playerId\": \"" + nextId + "\"}");

        } catch (SQLException e) {
            e.printStackTrace();
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, HttpContentType.TEXT_PLAIN, "Internal Server Error");
        }
    }

    private long getNextPlayerId(Connection connection) throws SQLException {
        String query = "SELECT MAX(playerid) FROM \"Player\"";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            if (resultSet.next()) {
                return resultSet.getLong(1) + 1; // Nächste ID ist die aktuelle maximale ID plus 1
            } else {
                return 1; // Wenn es keine Benutzer gibt, starten wir mit der ID 1
            }
        }
    }


    private Response handleUserLogin(UserDto userDto) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String query = "SELECT password FROM \"Player\" WHERE username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, userDto.getUsername());
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (!resultSet.next()) {
                        return new Response(HttpStatus.UNAUTHORIZED, HttpContentType.TEXT_PLAIN, "Invalid username or password");
                    }
                    String storedHash = resultSet.getString("password");
                    if (passwordEncoder.matches(userDto.getPassword(), storedHash)) {
                        String token = authenticationService.generateToken(userDto.getUsername());
                        return new Response(HttpStatus.OK, HttpContentType.APPLICATION_JSON, "{\"token\": \"" + token + "\"}");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new Response(HttpStatus.UNAUTHORIZED, HttpContentType.TEXT_PLAIN, "Invalid username or password");
    }

    private static class UserDto {
        @JsonProperty("Username")
        private String username;
        @JsonProperty("Password")
        private String password;
        @JsonProperty("email")
        private String email;
        @JsonProperty
        private String token;

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String getEmail() {
            return email;
        }
        public String getToken() {
            return token;
        }

    }
}
