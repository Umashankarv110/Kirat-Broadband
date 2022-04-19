package com.umashankar.kiratbroadbanduser.ModelClass;

public class Admins {

    int id, role;
    String username, password, timestamp;

    public Admins() {
    }

    public Admins(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public Admins(int id, int role, String username, String password, String timestamp) {
        this.id = id;
        this.role = role;
        this.username = username;
        this.password = password;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }


}