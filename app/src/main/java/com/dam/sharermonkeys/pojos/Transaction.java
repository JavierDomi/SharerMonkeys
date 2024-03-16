package com.dam.sharermonkeys.pojos;

public class Transaction {

    String deudor;
    String acreedor;
    Double cantidad;

    public Transaction(){};

    public Transaction(String deudor, String acreedor, Double cantidad) {
        this.deudor = deudor;
        this.acreedor = acreedor;
        this.cantidad = cantidad;
    }

    public String getDeudor() {
        return deudor;
    }

    public void setDeudor(String deudor) {
        this.deudor = deudor;
    }

    public String getAcreedor() {
        return acreedor;
    }

    public void setAcreedor(String acreedor) {
        this.acreedor = acreedor;
    }

    public Double getCantidad() {
        return cantidad;
    }

    public void setCantidad(Double cantidad) {
        this.cantidad = cantidad;
    }
}
