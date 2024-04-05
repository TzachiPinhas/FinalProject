package com.example.finalproject.Models;

import java.util.List;

public class Customer {
    private String customerId;
    private String name;
    private String email;

    private String phone;

    private List<Appointment> appointments;

    public Customer() {
    }

    public String getCustomerId() {
        return customerId;
    }

    public Customer setCustomerId(String customerId) {
        this.customerId = customerId;
        return this;
    }

    public String getName() {
        return name;
    }

    public Customer setName(String name) {
        this.name = name;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public Customer setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public Customer setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public Customer setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
        return this;
    }
}
