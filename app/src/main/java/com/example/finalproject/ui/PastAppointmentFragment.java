package com.example.finalproject.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.Adapters.AppointmentAdapter;
import com.example.finalproject.Listeners.AppointmentLoadListener;
import com.example.finalproject.Listeners.BarberListener;
import com.example.finalproject.Models.Appointment;
import com.example.finalproject.Models.FireBaseManager;
import com.example.finalproject.R;
import com.example.finalproject.databinding.FragmentPastAppointmentBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;


public class PastAppointmentFragment extends Fragment {
    private RecyclerView main_LST_pastAppointments;
    private FloatingActionButton show_BTN_allHistory;
    private ArrayList<Appointment> appointments;
    private ArrayList<Appointment> myAppointments;
    private FirebaseAuth auth;
    private FireBaseManager fbm;
    private FragmentPastAppointmentBinding binding;
    private View root;
    private int count;
    private boolean isBarber;
    private EditText edit_Date_history;
    private DatePickerDialog datePickerDialog;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPastAppointmentBinding.inflate(inflater, container, false);
        count = 0;
        appointments = new ArrayList<>();
        myAppointments = new ArrayList<>();
        root = binding.getRoot();
        auth = FirebaseAuth.getInstance();
        fbm = new FireBaseManager();
        findViews();
        checkIfBarber();
        getAllAppointmentFromData();
        showDatePickerDialog();
        initViews();

        return root;
    }

    private void checkIfBarber() { // Check if the user is a barber
        fbm.checkIsBarber(auth.getCurrentUser().getUid(), new BarberListener() {
            @Override
            public void isBarberLoaded(boolean isBarberLoaded) {
                isBarber = isBarberLoaded;
                setEditText(isBarber);
            }
        });
    }

    private void setEditText(boolean isBarber) {
        if (isBarber) {
            edit_Date_history.setVisibility(View.VISIBLE);
            show_BTN_allHistory.setVisibility(View.VISIBLE);
        } else {
            edit_Date_history.setVisibility(View.GONE);
            show_BTN_allHistory.setVisibility(View.GONE);

        }
    }


    private void initViews() {
        show_BTN_allHistory.setOnClickListener(v -> {
            updateAppointmentView(appointments); // Showing all appointments if the user is a barber
        });
    }

    private void getAllAppointmentFromData() { // Get all appointments from the database
        fbm.getAllAppointments(new AppointmentLoadListener() {
            @Override
            public void onAppointmentLoaded(ArrayList<Appointment> appointments) {
                if (appointments != null)
                    getYourAppointments(appointments);
                else {
                    getYourAppointments(new ArrayList<>());
                }
            }
        });
    }

    private void getYourAppointments(ArrayList<Appointment> allAppointments) { // Get the user's appointments from the database
        appointments.clear();
        myAppointments.clear();
        appointments.addAll(allAppointments);
        if (!allAppointments.isEmpty()) {
            for (Appointment appointment : allAppointments) {
                if (isBarber || appointment.getIdCustomer().equals(auth.getCurrentUser().getUid())) {
                    myAppointments.add(appointment);
                }
            }
            // Sort appointments by date and time
           sortAppointments(myAppointments);
        }
        updateAdapter();
    }

    private void sortAppointments(ArrayList<Appointment> Appointments) { // Sort the appointments by date and time
        Collections.sort(Appointments, new Comparator<Appointment>() {
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

    private void updateAdapter() {
        AppointmentAdapter appointmentAdapter = new AppointmentAdapter(root.getContext(), myAppointments, true, isBarber);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(root.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        main_LST_pastAppointments.setLayoutManager(linearLayoutManager);
        main_LST_pastAppointments.setAdapter(appointmentAdapter);
        myAppointments.size();
        count = appointmentAdapter.getItemCount();
        if (count == 0) {
            updateNoAppointmentsView();
        }
    }


    private void updateNoAppointmentsView() { // Update the view if there are no appointments
        TextView noAppointmentsText = root.findViewById(R.id.no_pastAppointments_text);
        noAppointmentsText.setText("No appointments have been held yet.");
        noAppointmentsText.setVisibility(View.VISIBLE);
        main_LST_pastAppointments.setVisibility(View.GONE);
    }

    public void showDatePickerDialog() {
        datePickerDialog = DatePickerDialog.newInstance(this::onDateSet,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        Calendar endDate = Calendar.getInstance();
        datePickerDialog.setMaxDate(endDate);
        edit_Date_history.setOnClickListener(v -> datePickerDialog.show(getParentFragmentManager(), "Datepickerdialog"));
    }

    private void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String selectedDateKey = String.format("%d/%d/%d", dayOfMonth, monthOfYear + 1, year);
        edit_Date_history.setText(selectedDateKey);
        filterAppointmentsByDate(selectedDateKey);
        edit_Date_history.setText("");
        edit_Date_history.setHint("Display appointments by date");
    }

    private void filterAppointmentsByDate(String date) { // Filter the appointments by date for the barber
        ArrayList<Appointment> filteredAppointments = new ArrayList<>();
        for (Appointment appointment : appointments) {
            if (appointment.getDate().equals(date)) {
                filteredAppointments.add(appointment);
            }
        }
        updateAppointmentView(filteredAppointments);
    }

    private void updateAppointmentView(ArrayList<Appointment> appointments) { // Update the view with the appointments
        sortAppointments(appointments);
        AppointmentAdapter adapter = new AppointmentAdapter(getContext(), appointments, true, isBarber);
        main_LST_pastAppointments.setAdapter(adapter);
    }


    private void findViews() {
        main_LST_pastAppointments = binding.mainLSTPastAppointments;
        edit_Date_history = binding.editDateHistory;
        show_BTN_allHistory = binding.showBTNAllHistory;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

    }

}