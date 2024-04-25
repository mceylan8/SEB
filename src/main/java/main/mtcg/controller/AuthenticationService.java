package main.mtcg.controller;

import java.sql.*;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

public class AuthenticationService {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/SEB";
    private static final String DB_USER = "postgres";
    private static final String DB_PASS = "12345678";

    public String generateToken(String username) {
        // Generiere einen einfachen Base64-kodierten "Token" für den Benutzernamen
        return Base64.getEncoder().encodeToString(username.getBytes(StandardCharsets.UTF_8));
    }

    public boolean isTokenValid(String token) {
        // Dekodiere das Token und prüfe, ob der Benutzername existiert
        String username = new String(Base64.getDecoder().decode(token), StandardCharsets.UTF_8);
        return userExists(username);
    }

    public String getUsernameFromToken(String token) {
        try {
            return new String(Base64.getDecoder().decode(token), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            return null;  // ungültiges Token Handling
        }
    }

    private boolean userExists(String username) {
        String sql = "SELECT count(*) FROM \"Player\" WHERE username = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
