package com.example.finalproject.Models;

public class TimeSlot {

    private String time;
    private String date;

    public TimeSlot() {
    }

    public String getTime() {
        return time;
    }

    public TimeSlot setTime(String time) {
        this.time = time;
        return this;
    }

    public String getDate() {
        return date;
    }

    public TimeSlot setDate(String date) {
        this.date = date;
        return this;
    }
}
