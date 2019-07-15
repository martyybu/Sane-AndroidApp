package com.example.tp.sane.Models;

public class User {

    private int ID;
    private String email, username;

    public User(int ID, String username, String email) {
        this.ID = ID;
        this.username = username;
        this.email = email;
    }

    public int getID() {
        return ID;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
}
