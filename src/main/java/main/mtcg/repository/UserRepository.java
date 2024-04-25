package main.mtcg.repository;

import main.mtcg.entity.PushUpRecord;
import main.mtcg.entity.User;

import java.sql.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserRepository {

    private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/SEB";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "12345678";
    private Connection currentTransactionConnection;


    public Optional<User> findByToken(String token) {
        String sql = "SELECT * FROM players WHERE token = ?";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, token);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapToUser(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public boolean deductCoins(int playerId, int amount) {
        String sql = "UPDATE players SET coins = coins - ? WHERE player_id = ?";
        try (Connection connection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, amount);
            statement.setObject(2, playerId);
            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Helfermethode zum Mappen des ResultSets auf ein User-Objekt
    private User mapToUser(ResultSet resultSet) throws SQLException {
        //System.out.println(resultSet.getObject("player_id").toString());
        int playerId = resultSet.getInt("player_id");
        String username = resultSet.getString("username");
        String password = resultSet.getString("password"); // Gehashtes Passwort
        String email = resultSet.getString("email");
        List<PushUpRecord> pushUpRecordList= (List<PushUpRecord>) resultSet.getBlob("pushup");
        String token = resultSet.getString("token");
        return new User(playerId, username, password, email, pushUpRecordList, token);
    }

    public void startTransaction() throws SQLException {
        if (currentTransactionConnection == null || currentTransactionConnection.isClosed()) {
            currentTransactionConnection = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
            currentTransactionConnection.setAutoCommit(false);
        } else {
            throw new SQLException("Transaction already started or connection is not closed");
        }
    }

    public void commitTransaction() throws SQLException {
        if (currentTransactionConnection != null && !currentTransactionConnection.isClosed()) {
            currentTransactionConnection.commit();
            currentTransactionConnection.close();
            currentTransactionConnection = null;
        } else {
            throw new SQLException("No transaction to commit or connection is already closed");
        }
    }

    public void rollbackTransaction() {
        try {
            if (currentTransactionConnection != null && !currentTransactionConnection.isClosed()) {
                currentTransactionConnection.rollback();
                currentTransactionConnection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            currentTransactionConnection = null;
        }
    }
}
