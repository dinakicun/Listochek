package com.example.listochek.model;

public class UserModel {

    private String email;
    private String name;
    private Integer age;
    private Integer height;
    private Integer weight;
    private String sex;

    public UserModel() {
    }

    public UserModel(String email, String name, Integer age, Integer height, Integer weight, String sex) {
        this.email = email;
        this.name = name;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.sex = sex;
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
}
