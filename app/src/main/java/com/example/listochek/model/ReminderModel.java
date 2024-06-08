package com.example.listochek.model;

public class ReminderModel {

    private String id;
    private String title;
    private int hour;
    private int minute;

    public ReminderModel() {
        // Пустой конструктор необходим для Firebase Firestore
    }

    public ReminderModel(String title, int hour, int minute) {
        this.title = title;
        this.hour = hour;
        this.minute = minute;
    }

    // Геттеры и сеттеры

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }
}
