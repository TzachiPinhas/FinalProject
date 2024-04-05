package com.example.finalproject.Models;

public class Service {
    private String name = "";

    private String description = "";

    private String price = "";

    public Service() {
    }

    public String getName() {
        return name;
    }

    public Service setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Service setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getPrice() {
        return price;
    }

    public Service setPrice(String price) {
        this.price = price;
        return this;
    }
}
