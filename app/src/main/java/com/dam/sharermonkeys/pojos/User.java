package com.dam.sharermonkeys.pojos;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {

    private String username;
    private String email;
    private ArrayList<FairShare> fairSharesList;

    public User(String username, String email, ArrayList<FairShare> fairSharesList) {
        this.username = username;
        this.email = email;
        this.fairSharesList = fairSharesList;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<FairShare> getFairSharesList() {
        return fairSharesList;
    }

    public void setFairSharesList(ArrayList<FairShare> fairSharesList) {
        this.fairSharesList = fairSharesList;
    }
}
