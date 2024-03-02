package com.example.listochek.model;

import java.util.Date;

public class WaterIntakeModel {
    private Date date;
    private Double volume;
    public WaterIntakeModel() {

    }

    public WaterIntakeModel(Date date, Double volume) {
        this.date = date;
        this.volume = volume;
    }

    public Date getDate() {
        return date;
    }

    public Double getVolume() {
        return volume;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }
}
