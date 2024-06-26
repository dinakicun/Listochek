package com.example.listochek.model;

public class MealModel {
    private Integer Calories;
    private Integer Fats;
    private Integer Protein;
    private Integer Carbohydrates;
    private String Name;
    private Integer Weight;
    private String NameToLower;
    private String id;
    private double factor = 1.0; // инициализация по умолчанию

    public MealModel() {
    }

    public MealModel(Integer Calories, Integer Fats, Integer Protein, Integer Carbohydrates, String Name, Integer Weight, String NameToLower, String id) {
        this.Calories = Calories;
        this.Fats = Fats;
        this.Protein = Protein;
        this.Carbohydrates = Carbohydrates;
        this.Name = Name;
        this.Weight = Weight;
        this.NameToLower = NameToLower;
        this.id = id;
        this.factor = 1.0; // инициализация по умолчанию
    }

    public double getFactor() {
        return factor;
    }

    public void setFactor(double factor) {
        this.factor = factor;
    }
    public Integer getCalories() {
        return Calories;
    }

    public Integer getFats() {
        return Fats;
    }

    public Integer getProtein() {
        return Protein;
    }

    public Integer getCarbohydrates() {
        return Carbohydrates;
    }

    public String getName() {
        return Name;
    }

    public String getNameToLower() {
        return NameToLower;
    }

    public Integer getWeight() {
        return Weight;
    }

    public String getId() {
        return id;
    }

    public void setCalories(Integer Calories) {
        this.Calories = Calories;
    }

    public void setFats(Integer Fats) {
        this.Fats = Fats;
    }

    public void setProtein(Integer Protein) {
        this.Protein = Protein;
    }

    public void setCarbohydrates(Integer Carbohydrates) {
        this.Carbohydrates = Carbohydrates;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public void setNameToLower(String NameToLower) {
        this.NameToLower = NameToLower;
    }

    public void setWeight(Integer Weight) {
        this.Weight = Weight;
    }

    public void setId(String id) {
        this.id = id;
    }
}
