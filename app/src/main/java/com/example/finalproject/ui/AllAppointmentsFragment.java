package com.example.finalproject.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.Adapters.AppointmentAdapter;
import com.example.finalproject.Interfaces.AppointmentLoadListener;
import com.example.finalproject.Interfaces.CancelCallback;
import com.example.finalproject.Models.Appointment;
import com.example.finalproject.Models.FireBaseManager;
import com.example.finalproject.R;
import com.example.finalproject.databinding.FragmentAllAppointmentsBinding;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;


public class AllAppointmentsFragment extends Fragment {
    private RecyclerView main_LST_appointments;
    private MaterialButton show_BTN_history;
    private ArrayList<Appointment> allAppointments;
    private ArrayList<Appointment> myAppointments;
    private FirebaseAuth auth;
    private FireBaseManager fbm;
    private FragmentAllAppointmentsBinding binding;
    private View root;
    private int count;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAllAppointmentsBinding.inflate(inflater, container, false);
        count = 0;
        allAppointments = new ArrayList<>();
        myAppointments = new ArrayList<>();
        root = binding.getRoot();
        auth = FirebaseAuth.getInstance();
        fbm = new FireBaseManager();
        findViews();
        getAllAppointmentFromData();

        return root;
    }

    private void getYourAppointments() {
        if (!allAppointments.isEmpty()) {
            for (Appointment appointment : allAppointments) {
                if (appointment.getIdCustomer().equals(auth.getCurrentUser().getUid())) {
                    myAppointments.add(appointment);
                }
            }
            // Sort appointments by date and time
            Collections.sort(myAppointments, new Comparator<Appointment>() {
                @Override
                public int compare(Appointment o1, Appointment o2) {
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    try {
                        Date date1 = format.parse(o1.getDate() + " " + o1.getTime());
                        Date date2 = format.parse(o2.getDate() + " " + o2.getTime());
                        return date1.compareTo(date2);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return 0;
                    }
                }
            });
        }
        initViews(root);
    }

    private void initViews(View root) {
        AppointmentAdapter appointmentAdapter = new AppointmentAdapter(root.getContext(), myAppointments, false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(root.getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        main_LST_appointments.setLayoutManager(linearLayoutManager);
        main_LST_appointments.setAdapter(appointmentAdapter);
        count = appointmentAdapter.getItemCount();
        if (count == 0) {
            updateNoAppointmentsView();
        }
        show_BTN_history.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_nav_myAppointments);
        });


        appointmentAdapter.setAppointmentCallBack(new CancelCallback() {
            @Override
            public void cancelCallback(Appointment appointment, int position) {
                removeAppointment(appointment);
                getActivity().recreate();
            }
        });
    }

    public void removeAppointment(Appointment appointment) {
        fbm.removeAppointment(appointment);
        fbm.removeAppointmentForUser(appointment.getIdCustomer(), appointment.getAppointmentId());
        processRemove();
    }

    private void processRemove() {
        myAppointments.size();
        myAppointments.clear();  // Clear existing data
        myAppointments.size();
        allAppointments.clear();
        getAllAppointmentFromData();
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
        allAppointments.clear();
        allAppointments.addAll(appointments);
    }

    private void updateNoAppointmentsView() {
        TextView noAppointmentsText = root.findViewById(R.id.no_appointments_text);
        noAppointmentsText.setText("No future appointments at the moment.");
        noAppointmentsText.setVisibility(View.VISIBLE);  // Display the message
        main_LST_appointments.setVisibility(View.GONE);  // Hide the RecyclerView
    }


    private void findViews() {
        main_LST_appointments = binding.mainLSTAppointments;
        show_BTN_history = binding.showBTNHistory;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}