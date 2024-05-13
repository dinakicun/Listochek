package com.example.listochek.model;

public class MealModel {

    private Integer Calories;
    private Integer Fats;
    private Integer Protein;
    private Integer Carbohydrates;
    private String Name;
    private Integer Weight;
    private String NameToLower;

    public MealModel() {

    }
    public MealModel(Integer Calories, Integer Fats, Integer Protein, Integer Carbohydrates, String Name, Integer Weight, String NameToLower) {
        this.Calories = Calories;
        this.Fats = Fats;
        this.Protein = Protein;
        this.Carbohydrates = Carbohydrates;
        this.Name = Name;
        this.Weight = Weight;
        this.NameToLower = NameToLower;
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
    public void setNameToLower() { this.NameToLower = NameToLower;}
    public void setWeight(Integer Weight) {
        this.Weight = Weight;
    }
}

