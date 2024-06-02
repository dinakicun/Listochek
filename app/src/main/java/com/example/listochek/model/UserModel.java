package com.example.listochek.model;

public class UserModel {

    private String email;
    private String name;
    private Integer age;
    private Integer height;
    private Integer weight;
    private String sex;
    private Boolean activeLS;
    private Integer foodPoints;
    private Integer waterPoints;

    public UserModel() {
    }

    public UserModel(String email, String name, Integer age, Integer height, Integer weight, String sex, Boolean activeLS) {
        this.email = email;
        this.name = name;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.sex = sex;
        this.activeLS = activeLS;
        this.foodPoints = 1;
        this.waterPoints = 1;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Boolean getActiveLS() {
        return activeLS;
    }

    public void setActiveLS(Boolean activeLS) {
        this.activeLS = activeLS;
    }

    public Integer getFoodPoints() {
        return foodPoints;
    }

    public void setFoodPoints(Integer foodPoints) {
        this.foodPoints = foodPoints;
    }

    public Integer getWaterPoints() {
        return waterPoints;
    }

    public void setWaterPoints(Integer waterPoints) {
        this.waterPoints = waterPoints;
    }
}
