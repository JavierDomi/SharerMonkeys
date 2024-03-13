package com.dam.sharermonkeys.pojos;

import java.io.Serializable;
import java.util.ArrayList;

public class Expense implements Serializable {

    private String name;
    private String idUser; // payer
    private String idFairshaer;
    private String idExpense;
    private double amount;
    private ArrayList<User> notPayerList; // email ,username


    public Expense(String name, String idUser, String idFairshaer, String idExpense, double amount, ArrayList<User> notPayerList) {
        this.name = name;
        this.idUser = idUser;
        this.idFairshaer = idFairshaer;
        this.idExpense = idExpense;
        this.amount = amount;
        this.notPayerList = notPayerList;
    }

    public Expense() {
    }

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdFairshaer() {
        return idFairshaer;
    }

    public void setIdFairshaer(String idFairshaer) {
        this.idFairshaer = idFairshaer;
    }

    public String getEdExpense() {
        return idExpense;
    }

    public void setEdExpense(String edExpense) {
        this.idExpense = edExpense;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public ArrayList<User> getNotPayerList() {
        return notPayerList;
    }

    public void setNotPayerList(ArrayList<User> notPayerList) {
        this.notPayerList = notPayerList;
    }
}
