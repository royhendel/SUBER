package com.example.suber_again;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    private static String Username;
    private static String Password;
    private static String Role;
/*    private static ArrayList<Request> Requests;
    private static ArrayList<Request> Past_Requests;*/

    public User(){}

    public User(String name, String password, String role) {
        this.Username = name;
        this.Password = password;
        this.Role = role;
    }

    /*public ArrayList<Request> getPast_Requests() {
        return Past_Requests;
    }

    public void setPast_Requests(ArrayList<Request> Past_Requests) {
        this.Past_Requests = Past_Requests;
    }

    public ArrayList<Request> getRequests() {
        return Requests;
    }

    public void setRequests(ArrayList<Request> Requests) {
        this.Requests = Requests;
    }*/

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
