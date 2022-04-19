package com.umashankar.kiratbroadbanduser.ModelClass;

public class Notifications {
    int id, uniquenumber, isRead;
    String title, message, date, time;

    public Notifications() {
    }

    public Notifications(int id, String title, String message, String date, String time) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.date = date;
        this.time = time;
    }

    public Notifications(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public Notifications(int id, int uniquenumber, int isRead, String title, String message, String date, String time) {
        this.id = id;
        this.uniquenumber = uniquenumber;
        this.isRead = isRead;
        this.title = title;
        this.message = message;
        this.date = date;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUniquenumber() {
        return uniquenumber;
    }

    public void setUniquenumber(int uniquenumber) {
        this.uniquenumber = uniquenumber;
    }

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
