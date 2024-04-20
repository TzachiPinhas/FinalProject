package com.example.finalproject.Models;

import java.util.List;

public class User {
    private String userId;
    private String email;
    private String name;
    private String phone;
    private String Barber;
    private List<String> appointments;

    public User() {
    }

    public String getUserId() {
        return userId;
    }

    public User setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }



    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public User setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public List<String> getAppointments() {
        return appointments;
    }

    public User setAppointments(List<String> appointments) {
        this.appointments = appointments;
        return this;
    }

    public String getBarber() {
        return Barber;
    }

    public User setBarber(String value) {
        Barber = value;
        return this;
    }
}


