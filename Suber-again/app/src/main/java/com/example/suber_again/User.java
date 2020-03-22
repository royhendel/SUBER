package com.example.suber_again;

public class User {
    private static String Username;
    private static String Password;
    private static String Role;

    public User(){}

    public User(String name, String password, String role) {
        this.Username = name;
        this.Password = password;
        this.Role = role;
    }


    public String getName() {
        return Username;
    }

    public void setName(String name) {
        this.Username = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        this.Password = password;
    }

    public String getRole() {
        return Role;
    }

    public void setRole(String Role) {
        this.Role = Role;
    }

}
