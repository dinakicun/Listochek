package com.example.listochek.model;

import java.util.Date;

public class PlantModel {
    private int level;
    private Date lastWatered;
    private Date lastFertilized;

    public PlantModel() {
    }

    public PlantModel(int level, Date lastWatered, Date lastFertilized) {
        this.level = level;
        this.lastWatered = lastWatered;
        this.lastFertilized = lastFertilized;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
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
}
