package com.umashankar.kiratbroadbanduser.ModelClass;

public class Authentication {
    int id;
    String customerName, customerMobNumber,customerEmail, customerLandline,ConnType;

    public Authentication() {
    }

    public Authentication(int id, String customerName, String customerMobNumber, String customerEmail, String customerLandline, String ConnType) {
        this.id = id;
        this.customerName = customerName;
        this.customerMobNumber = customerMobNumber;
        this.customerEmail = customerEmail;
        this.customerLandline = customerLandline;
        this.ConnType = ConnType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerMobNumber() {
        return customerMobNumber;
    }

    public void setCustomerMobNumber(String customerMobNumber) {
        this.customerMobNumber = customerMobNumber;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerLandline() {
        return customerLandline;
    }

    public void setCustomerLandline(String customerLandline) {
        this.customerLandline = customerLandline;
    }

    public String getConnType() {
        return ConnType;
    }

    public void setConnType(String connType) {
        ConnType = connType;
    }
}
