package main.mtcg.entity;


import java.util.List;

public class User {
    private int playerId;
    private String username;
    private String password;
    private String email;
    private List<PushUpRecord> pushUpRecords;
    private String token;


    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<PushUpRecord> getPushUpRecords() {
        return pushUpRecords;
    }

    public void setPushUpRecords(List<PushUpRecord> pushUpRecords) {
        this.pushUpRecords = pushUpRecords;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    public User(int playerId, String username, String password, String email, List<PushUpRecord> pushUpRecords, String token) {
        this.playerId = playerId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.pushUpRecords=pushUpRecords;
        this.token = token;
    }

}




