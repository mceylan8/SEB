package main.mtcg.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.mtcg.entity.PushUpRecord;
import main.server.http.HttpContentType;
import main.server.http.HttpStatus;
import main.server.http.Request;
import main.server.http.Response;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class PushupController implements Controller {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/SEB";
    private static final String USER = "postgres";
    private static final String PASS= "12345678";
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final AuthenticationService authenticationService;


    private final ObjectMapper objectMapper = new ObjectMapper();
    public PushupController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public boolean supports(String route) {
        return route.equals("/pushup") || route.equals("/getAllPushup");
    }

    public Response handle(Request request) {
        //System.out.println(request);
        try {
            PushupController.PushUpReordDto pushUpReordDto = objectMapper.readValue(request.getBody(), PushupController.PushUpReordDto.class);
            if (pushUpReordDto.getCount() == 0 || pushUpReordDto.getDuration() == 0) {
                return new Response(HttpStatus.BAD_REQUEST, HttpContentType.TEXT_PLAIN, "Invalid data");
            }

            if (request.getRoute().equals("/pushup")) {
                return handleUserPushupRecords(pushUpReordDto);
            } else if (request.getRoute().equals("/getAllPushup")) {
                return getAllPushRecords();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new Response(HttpStatus.BAD_REQUEST, HttpContentType.TEXT_PLAIN, "Invalid request format");
        }

        return new Response(HttpStatus.BAD_REQUEST, HttpContentType.TEXT_PLAIN, "Invalid route");
    }
    private Response handleUserPushupRecords(PushupController.PushUpReordDto pushUpReordDto) {
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
            try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO pushup (p_count, duration) VALUES (?, ?)")) {
                preparedStatement.setInt(1, pushUpReordDto.getCount());
                preparedStatement.setInt(2, pushUpReordDto.getDuration());
                preparedStatement.executeUpdate();
            }

            return new Response(HttpStatus.CREATED, HttpContentType.APPLICATION_JSON, "{\"message\": \"Record created\"}");
        } catch (SQLException e) {
            e.printStackTrace();
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, HttpContentType.TEXT_PLAIN, HttpStatus.INTERNAL_SERVER_ERROR.getMessage());
        }
    }
    private Response getAllPushRecords()
    {
        String responseBody=null;
        List<PushUpRecord> pushUpRecords = new ArrayList<>();
        String sql = "SELECT * FROM pushup";

        try {
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            //System.out.println("----->before while " + resultSet.getFetchSize());

            while (resultSet.next()) {
                PushUpRecord pushUpRecord = new PushUpRecord(resultSet.getInt("p_count"), resultSet.getInt("duration"), resultSet.getInt("userId"));
                // System.out.println(card.getName());
                pushUpRecords.add(pushUpRecord);
                // System.out.println("----->after addcard");
            }

            ObjectMapper objectMapper = new ObjectMapper();
            responseBody = objectMapper.writeValueAsString(pushUpRecords);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Response(HttpStatus.OK, HttpContentType.APPLICATION_JSON, responseBody);
    }

    private static class PushUpReordDto {
        @JsonProperty("count")
        private int count;
        @JsonProperty("duration")
        private int duration;

        public int getCount() {
            return count;
        }

        public int getDuration() {
            return duration;
        }
    }
}
