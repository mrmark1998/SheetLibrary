package com.example.bugtracker;

public class Users {
    private int id;

    private String name;
    private String email;
    private String username;
    private String password;
    private String type;

    public Users(int id, String name, String email, String username, String password, String type) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
        this.type = type;
    }

    //getters

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getType() {
        return type;
    }
}
