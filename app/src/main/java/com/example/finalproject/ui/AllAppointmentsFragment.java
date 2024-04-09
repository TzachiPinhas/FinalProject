package com.example.finalproject.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.Adapters.AppointmentAdapter;
import com.example.finalproject.Interfaces.AppointmentLoadListener;
import com.example.finalproject.Interfaces.CancelCallback;
import com.example.finalproject.Models.Appointment;
import com.example.finalproject.Models.FireBaseManager;
import com.example.finalproject.databinding.FragmentAllAppointmentsBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;


public class AllAppointmentsFragment extends Fragment {
    private RecyclerView main_LST_appointments;
    private ArrayList<Appointment> allAppointments;
    private ArrayList<Appointment> myAppointments;
    private FirebaseAuth auth;
    private FireBaseManager fbm;
    private FragmentAllAppointmentsBinding binding;
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        
        binding = FragmentAllAppointmentsBinding.inflate(inflater, container, false);
        allAppointments = new ArrayList<>();
        myAppointments = new ArrayList<>();
        root = binding.getRoot();
        auth = FirebaseAuth.getInstance();
        fbm = new FireBaseManager();
        getAllAppointmentFromData();
        findViews();

        return root;
    }

    private void getYourAppointments() {
        if (!allAppointments.isEmpty()){
            for (Appointment appointment : allAppointments) {
                if (appointment.getIdCustomer().equals(auth.getCurrentUser().getUid())) {
                    myAppointments.add(appointment);
                }
            }
        }
        else {
            myAppointments = new ArrayList<>();
        }
        initViews(root);
    }

    private void initViews(View root) {
        AppointmentAdapter appointmentAdapter = new AppointmentAdapter(root.getContext(), myAppointments);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(root.getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        main_LST_appointments.setLayoutManager(linearLayoutManager);
        main_LST_appointments.setAdapter(appointmentAdapter);

        appointmentAdapter.setAppointmentCallBack(new CancelCallback() {
            @Override
            public void cancelCallback(Appointment appointment, int position) {
                removeAppointment(appointment);
                appointmentAdapter.notifyDataSetChanged();
            }
        });
    }

    public void removeAppointment(Appointment appointment) {
        fbm.removeAppointment(appointment);
        fbm.removeAppointmentForUser(appointment.getIdCustomer(), appointment.getAppointmentId());
        myAppointments.remove(appointment);
    }

    private void getAllAppointmentFromData() {
        fbm.getAllAppointments(new AppointmentLoadListener() {
            @Override
            public void onAppointmentLoaded(ArrayList<Appointment> appointments) {
                if (appointments != null)
                    getAppointments(appointments);
                else
                    getAppointments(new ArrayList<>());

                getYourAppointments();
            }
        });
    }

    private void getAppointments(ArrayList<Appointment> appointments) {
          allAppointments.addAll(appointments);
      }


    private void findViews() {
        main_LST_appointments = binding.mainLSTAppointments;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}