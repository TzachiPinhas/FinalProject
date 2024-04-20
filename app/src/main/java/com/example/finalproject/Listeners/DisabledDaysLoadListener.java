package com.example.finalproject.Listeners;

import com.example.finalproject.Models.BlockDay;

import java.util.ArrayList;

public interface DisabledDaysLoadListener { // This interface is used to load disabled days
    void onDaysLoad(ArrayList<BlockDay> blockDays);
}
