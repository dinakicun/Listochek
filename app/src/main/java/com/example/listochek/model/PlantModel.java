package com.example.listochek.model;

import java.util.Date;

public class PlantModel {
    private int waterLevel;
    private int fertilizerLevel;
    private Date lastWatered;
    private Date lastFertilized;
    private int waterExperience;
    private int fertilizerExperience;

    // Конструктор, геттеры и сеттеры
    public PlantModel() {
        this.waterLevel = 0;
        this.fertilizerLevel = 0;
        this.lastWatered = new Date();
        this.lastFertilized = new Date();
        this.waterExperience = 0;
        this.fertilizerExperience = 0;
    }

    public int getWaterLevel() {
        return waterLevel;
    }

    public void setWaterLevel(int waterLevel) {
        this.waterLevel = waterLevel;
    }

    public int getFertilizerLevel() {
        return fertilizerLevel;
    }

    public void setFertilizerLevel(int fertilizerLevel) {
        this.fertilizerLevel = fertilizerLevel;
    }

    public Date getLastWatered() {
        return lastWatered;
    }

    public void setLastWatered(Date lastWatered) {
        this.lastWatered = lastWatered;
    }

    public Date getLastFertilized() {
        return lastFertilized;
    }

    public void setLastFertilized(Date lastFertilized) {
        this.lastFertilized = lastFertilized;
    }

    public int getWaterExperience() {
        return waterExperience;
    }

    public void setWaterExperience(int waterExperience) {
        this.waterExperience = waterExperience;
    }

    public int getFertilizerExperience() {
        return fertilizerExperience;
    }

    public void setFertilizerExperience(int fertilizerExperience) {
        this.fertilizerExperience = fertilizerExperience;
    }
}
