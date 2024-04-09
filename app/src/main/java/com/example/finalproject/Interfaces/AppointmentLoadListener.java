package com.example.finalproject.Interfaces;

import com.example.finalproject.Models.Appointment;

import java.util.ArrayList;

public interface AppointmentLoadListener {
    void onAppointmentLoaded(ArrayList<Appointment> appointments);
}
