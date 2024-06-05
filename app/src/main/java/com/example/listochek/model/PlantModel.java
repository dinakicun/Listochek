package com.example.listochek.model;

import java.util.Date;

public class PlantModel {
    private int waterExperience;
    private int fertilizerExperience;
    private Date lastWatered;
    private Date lastFertilized;

    public PlantModel() {
        this.waterExperience = 0;
        this.fertilizerExperience = 0;
        this.lastWatered = new Date();
        this.lastFertilized = new Date();
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

    public int getWaterLevel() {
        return waterExperience / 1000;
    }

    public int getFertilizerLevel() {
        return fertilizerExperience / 1000;
    }

    public int getPlantLevel() {
        int waterLevel = getWaterLevel();
        int fertilizerLevel = getFertilizerLevel();
        return Math.min(waterLevel, fertilizerLevel);
    }
}
