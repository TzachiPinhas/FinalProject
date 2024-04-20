package com.example.finalproject.Listeners;

import com.example.finalproject.Models.Appointment;

import java.util.ArrayList;

public interface AppointmentLoadListener {// This interface is used to load appointments
    void onAppointmentLoaded(ArrayList<Appointment> appointments);
}
