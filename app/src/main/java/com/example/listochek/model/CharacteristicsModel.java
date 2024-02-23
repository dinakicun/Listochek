package com.example.listochek.model;

public class CharacteristicsModel {
    private Integer calories;
    private Integer fats;
    private Integer protein;
    private Integer carbohydrates;
    private String userId;
    private double water;
    public CharacteristicsModel() {
    }

    public CharacteristicsModel(Integer calories, Integer fats, Integer protein, Integer carbohydrates, String userId, Double water) {
        this.calories = calories;
        this.fats = fats;
        this.protein = protein;
        this.carbohydrates = carbohydrates;
        this.userId = userId;
        this.water = water;
    }

    public Integer getCalories() {
        return calories;
    }

    public void setCalories(Integer calories) {
        this.calories = calories;
    }

    public Integer getFats() {
        return fats;
    }

    public void setFats(Integer fats) {
        this.fats = fats;
    }

    public Integer getProtein() {
        return protein;
    }

    public void setProtein(Integer protein) {
        this.protein = protein;
    }
    public Integer getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(Integer carbohydrates) {
        this.protein = carbohydrates;
    }

    public Double getWater() {
        return water;
    }

    public void setWater(Double water) {
        this.water = water;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
