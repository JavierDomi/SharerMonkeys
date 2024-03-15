package com.dam.sharermonkeys.pojos;

public class Balance {

    private String idUser; //user.getName
    private String idFareshare; //fareshare.getIdFareshare
    private double expenses;
    private double payments;

    public Balance(){};

    public Balance(String idUser, String idFareshare, double expense, double payment) {
        this.idUser = idUser;
        this.idFareshare = idFareshare;
        this.expenses = expense;
        this.payments = payment;
    }

    public Balance(String idUser) {
        this.idUser = idUser;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdFareshare() {
        return idFareshare;
    }

    public void setIdFareshare(String idFareshare) {
        this.idFareshare = idFareshare;
    }

    public double getExpenses() {
        return expenses;
    }

    public void setExpenses(double expenses) {
        this.expenses = expenses;
    }

    public double getPayments() {
        return payments;
    }

    public void setPayments(double payments) {
        this.payments = payments;
    }


    // Get total owed or to pay (+ or -)
    public double calculateTotal() {

        return payments - expenses;

    }

}
