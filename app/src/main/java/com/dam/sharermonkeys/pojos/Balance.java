package com.dam.sharermonkeys.pojos;

public class Balance {

    private String idUser; //user.getName
    private String idFareshare; //fareshare.getIdFareshare
    private double expense;
    private double payment;

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

    public double getExpense() {
        return expense;
    }

    public void setExpense(double expense) {
        this.expense = expense;
    }

    public double getPayment() {
        return payment;
    }

    public void setPayment(double payment) {
        this.payment = payment;
    }


    // Get total owed or to pay (+ or -)
    public double calculateTotal() {

        return payment - expense;

    }

}
