package com.example.damxat.Model;

public class User {
    String id;
    String username;
    String status;
    String token;

    public User(String id, String username, String status, String tokken) {
        this.id = id;
        this.username = username;
        this.status = status;
        this.token = token;
    }

    public User(String id, String username, String status) {
        this.id = id;
        this.username = username;
    }

    public User() {
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getStatus(){
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String tokken) {
        this.token = tokken;
    }
}
