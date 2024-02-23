package com.dam.sharermonkeys.pojos;

import java.util.ArrayList;

public class Fairshare {

    private String idFairshare;
    private String name;
    private String description;
    private ArrayList<User> userList;

    public Fairshare(String idFairshare, String name, String description, ArrayList<User> userList) {
        this.idFairshare = idFairshare;
        this.name = name;
        this.description = description;
        this.userList = userList;
    }

    public String getIdFairshare() {return idFairshare;}

    public void setIdFairshare(String idFairshare) {
        this.idFairshare = idFairshare;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<User> getUserList() {
        return userList;
    }

    public void setUserList(ArrayList<User> userList) {
        this.userList = userList;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
