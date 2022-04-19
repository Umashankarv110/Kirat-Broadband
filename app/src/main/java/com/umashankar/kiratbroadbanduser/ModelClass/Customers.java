package com.umashankar.kiratbroadbanduser.ModelClass;

public class Customers {

    int customerId,locationId,isRequired,numberChoice,isTrash,isActive,isJoining;
    String name,mobile,email,landline,password,address,timestamp,oltIp;
    String bbPlan, fixedMonthlyCharges, finalAmountRound, wkgStatus, llInstallDate;

    public Customers() {
    }

    public Customers(String bbPlan, String fixedMonthlyCharges, String finalAmountRound, String wkgStatus, String llInstallDate) {
        this.bbPlan = bbPlan;
        this.fixedMonthlyCharges = fixedMonthlyCharges;
        this.finalAmountRound = finalAmountRound;
        this.wkgStatus = wkgStatus;
        this.llInstallDate = llInstallDate;
    }

    public Customers(int customerId, String name, String mobile, String landline) {
        this.customerId = customerId;
        this.name = name;
        this.mobile = mobile;
        this.landline = landline;
    }

    public Customers(int customerId, String name, String password) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.password = password;
    }



    public Customers(int customerId, String name) {
        this.customerId = customerId;
        this.name = name;
    }

    public Customers(String name, String mobile, String email, int locationId, String address, int isRequired, int numberChoice) {
        this.name = name;
        this.mobile = mobile;
        this.email = email;
        this.locationId = locationId;
        this.address = address;
        this.isRequired = isRequired;
        this.numberChoice = numberChoice;
    }

    public String getBbPlan() {
        return bbPlan;
    }

    public void setBbPlan(String bbPlan) {
        this.bbPlan = bbPlan;
    }

    public String getFixedMonthlyCharges() {
        return fixedMonthlyCharges;
    }

    public void setFixedMonthlyCharges(String fixedMonthlyCharges) {
        this.fixedMonthlyCharges = fixedMonthlyCharges;
    }

    public String getFinalAmountRound() {
        return finalAmountRound;
    }

    public void setFinalAmountRound(String finalAmountRound) {
        this.finalAmountRound = finalAmountRound;
    }

    public String getWkgStatus() {
        return wkgStatus;
    }

    public void setWkgStatus(String wkgStatus) {
        this.wkgStatus = wkgStatus;
    }

    public String getLlInstallDate() {
        return llInstallDate;
    }

    public void setLlInstallDate(String llInstallDate) {
        this.llInstallDate = llInstallDate;
    }

    public String getLandline() {
        return landline;
    }

    public void setLandline(String landline) {
        this.landline = landline;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public int getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(int isRequired) {
        this.isRequired = isRequired;
    }

    public int getNumberChoice() {
        return numberChoice;
    }

    public void setNumberChoice(int numberChoice) {
        this.numberChoice = numberChoice;
    }

    public int getIsTrash() {
        return isTrash;
    }

    public void setIsTrash(int isTrash) {
        this.isTrash = isTrash;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public int getIsJoining() {
        return isJoining;
    }

    public void setIsJoining(int isJoining) {
        this.isJoining = isJoining;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return this.name; // What to display in the Spinner list.
    }
}
