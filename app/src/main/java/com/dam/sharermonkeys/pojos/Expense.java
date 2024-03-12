package com.dam.sharermonkeys.pojos;

import java.io.Serializable;
import java.util.ArrayList;

public class Expense implements Serializable {

    private String name;
    private String idUser; // payer
    private int idFairshaer;
    private int edExpense;
    private double amount;
    private ArrayList<User> notPayerList;


    public Expense(String name, String idUser, int idFairshaer, int edExpense, double amount, ArrayList<User> notPayerList) {
        this.name = name;
        this.idUser = idUser;
        this.idFairshaer = idFairshaer;
        this.edExpense = edExpense;
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

    public int getIdFairshaer() {
        return idFairshaer;
    }

    public void setIdFairshaer(int idFairshaer) {
        this.idFairshaer = idFairshaer;
    }

    public int getEdExpense() {
        return edExpense;
    }

    public void setEdExpense(int edExpense) {
        this.edExpense = edExpense;
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
