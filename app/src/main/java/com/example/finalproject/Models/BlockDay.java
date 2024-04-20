package com.example.finalproject.Models;

public class BlockDay {

    private String blockDayId = "";
    private String date = "";
    private String note = "";


    public BlockDay() {
    }

    public String getDate() {
        return date;
    }

    public String getBlockDayId() {
        return blockDayId;
    }

    public BlockDay setBlockDayId(String blockDayId) {
        this.blockDayId = blockDayId;
        return this;
    }

    public BlockDay setDate(String date) {
        this.date = date;
        return this;
    }

    public String getNote() {
        return note;
    }

    public BlockDay setNote(String note) {
        this.note = note;
        return this;
    }


}
