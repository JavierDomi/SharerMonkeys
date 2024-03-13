package com.dam.sharermonkeys.pojos;

import java.io.Serializable;
import java.util.ArrayList;

public class Expense implements Serializable {

    private String name;
    private String idUserPayer; // payer
    private String idFairshare;
    private String idExpense;
    private String date;
    private double amount;
    private ArrayList<User> notPayerList; // email ,username

    public Expense(String name, String idUser, String idFairshaer, String idExpense, String date, double amount, ArrayList<User> notPayerList) {
        this.name = name;
        this.idUserPayer = idUser;
        this.idFairshare = idFairshaer;
        this.idExpense = idExpense;
        this.date = date;
        this.amount = amount;
        this.notPayerList = notPayerList;
    }

    public Expense(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdUserPayer() {
        return idUserPayer;
    }

    public void setIdUserPayer(String idUserPayer) {
        this.idUserPayer = idUserPayer;
    }

    public String getIdFairshare() {
        return idFairshare;
    }

    public void setIdFairshare(String idFairshare) {
        this.idFairshare = idFairshare;
    }

    public String getIdExpense() {
        return idExpense;
    }

    public void setIdExpense(String idExpense) {
        this.idExpense = idExpense;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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
