package com.umashankar.kiratbroadbanduser.ModelClass;

public class PlanDetails {
    int planId, isActive;
    String planName, bandwithDownloadSpeed, fupSpeed, fmc, service, modTimestamp;
    String fixedMonthlyCharges, gst, discount, finalAmount, finalAmountRound, securityDeposit;

    public PlanDetails() {
    }

    public PlanDetails(int planId, String planName, String fixedMonthlyCharges, String gst, String discount, String finalAmount, String securityDeposit, String finalAmountRound, String bandwithDownloadSpeed) {
        this.planId = planId;
        this.planName = planName;
        this.fixedMonthlyCharges = fixedMonthlyCharges;
        this.gst = gst;
        this.discount = discount;
        this.finalAmount = finalAmount;
        this.securityDeposit = securityDeposit;
        this.finalAmountRound = finalAmountRound;
        this.bandwithDownloadSpeed = bandwithDownloadSpeed;
    }

    public int getPlanId() {
        return planId;
    }

    public void setPlanId(int planId) {
        this.planId = planId;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getBandwithDownloadSpeed() {
        return bandwithDownloadSpeed;
    }

    public void setBandwithDownloadSpeed(String bandwithDownloadSpeed) {
        this.bandwithDownloadSpeed = bandwithDownloadSpeed;
    }

    public String getFupSpeed() {
        return fupSpeed;
    }

    public void setFupSpeed(String fupSpeed) {
        this.fupSpeed = fupSpeed;
    }

    public String getFmc() {
        return fmc;
    }

    public void setFmc(String fmc) {
        this.fmc = fmc;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getModTimestamp() {
        return modTimestamp;
    }

    public void setModTimestamp(String modTimestamp) {
        this.modTimestamp = modTimestamp;
    }

    public String getFixedMonthlyCharges() {
        return fixedMonthlyCharges;
    }

    public void setFixedMonthlyCharges(String fixedMonthlyCharges) {
        this.fixedMonthlyCharges = fixedMonthlyCharges;
    }

    public String getGst() {
        return gst;
    }

    public void setGst(String gst) {
        this.gst = gst;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(String finalAmount) {
        this.finalAmount = finalAmount;
    }

    public String getFinalAmountRound() {
        return finalAmountRound;
    }

    public void setFinalAmountRound(String finalAmountRound) {
        this.finalAmountRound = finalAmountRound;
    }

    public String getSecurityDeposit() {
        return securityDeposit;
    }

    public void setSecurityDeposit(String securityDeposit) {
        this.securityDeposit = securityDeposit;
    }
}
