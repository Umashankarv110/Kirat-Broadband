package com.umashankar.kiratbroadbanduser.ModelClass;

public class Report {
    int reportId;
    String reportStatus, reportName, reportDate, reportTime, reportReason, assignUserId, reportAssignTo, assignUserContact, resolveDate, resolveTime;
    String resolveReason, inProgressReason;

    public Report() {
    }

    public Report(int reportId, String reportStatus, String reportName, String reportDate, String reportTime, String reportReason) {
        this.reportId = reportId;
        this.reportStatus = reportStatus;
        this.reportName = reportName;
        this.reportDate = reportDate;
        this.reportTime = reportTime;
        this.reportReason = reportReason;
    }

    public Report(int reportId, String reportStatus, String reportName, String reportDate, String reportTime, String reportReason, String assignUserId, String reportAssignTo, String assignUserContact, String resolveDate, String resolveTime, String resolveReason, String inProgressReason) {
        this.reportId = reportId;
        this.reportStatus = reportStatus;
        this.reportName = reportName;
        this.reportDate = reportDate;
        this.reportTime = reportTime;
        this.reportReason = reportReason;
        this.reportAssignTo = reportAssignTo;
        this.assignUserContact = assignUserContact;
        this.resolveDate = resolveDate;
        this.resolveTime = resolveTime;
        this.assignUserId = assignUserId;
        this.resolveReason = resolveReason;
        this.inProgressReason = inProgressReason;
    }

    public String getAssignUserContact() {
        return assignUserContact;
    }

    public void setAssignUserContact(String assignUserContact) {
        this.assignUserContact = assignUserContact;
    }

    public String getResolveReason() {
        return resolveReason;
    }

    public String getInProgressReason() {
        return inProgressReason;
    }

    public void setInProgressReason(String inProgressReason) {
        this.inProgressReason = inProgressReason;
    }

    public void setResolveReason(String resolveReason) {
        this.resolveReason = resolveReason;
    }

    public String getAssignUserId() {
        return assignUserId;
    }

    public void setAssignUserId(String assignUserId) {
        this.assignUserId = assignUserId;
    }

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public String getReportStatus() {
        return reportStatus;
    }

    public void setReportStatus(String reportStatus) {
        this.reportStatus = reportStatus;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getReportDate() {
        return reportDate;
    }

    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
    }

    public String getReportTime() {
        return reportTime;
    }

    public void setReportTime(String reportTime) {
        this.reportTime = reportTime;
    }

    public String getReportReason() {
        return reportReason;
    }

    public void setReportReason(String reportReason) {
        this.reportReason = reportReason;
    }

    public String getReportAssignTo() {
        return reportAssignTo;
    }

    public void setReportAssignTo(String reportAssignTo) {
        this.reportAssignTo = reportAssignTo;
    }

    public String getResolveDate() {
        return resolveDate;
    }

    public void setResolveDate(String resolveDate) {
        this.resolveDate = resolveDate;
    }

    public String getResolveTime() {
        return resolveTime;
    }

    public void setResolveTime(String resolveTime) {
        this.resolveTime = resolveTime;
    }
}
