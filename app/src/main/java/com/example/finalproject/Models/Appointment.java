package com.example.finalproject.Models;

public class Appointment {
    private String appointmentId = "";
    private String idCustomer = "";
    private String customerName = "";
    private String date = "";
    private String time = "";
    private String service = "";
    private String price = "";
    private String costumerPhone = "";

    public Appointment() {
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public Appointment setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
        return this;
    }

    public String getIdCustomer() {
        return idCustomer;
    }

    public Appointment setIdCustomer(String idCustomer) {
        this.idCustomer = idCustomer;
        return this;
    }

    public String getCustomerName() {
        return customerName;
    }

    public Appointment setCustomerName(String customerName) {
        this.customerName = customerName;
        return this;
    }

    public String getDate() {
        return date;
    }

    public Appointment setDate(String date) {
        this.date = date;
        return this;
    }

    public String getTime() {
        return time;
    }

    public Appointment setTime(String time) {
        this.time = time;
        return this;
    }

    public String getService() {
        return service;
    }

    public Appointment setService(String service) {
        this.service = service;
        return this;
    }

    public String getPrice() {
        return price;
    }

    public Appointment setPrice(String price) {
        this.price = price;
        return this;
    }

    public String getCostumerPhone() {
        return costumerPhone;
    }

    public Appointment setCostumerPhone(String costumerPhone) {
        this.costumerPhone = costumerPhone;
        return this;
    }
}
