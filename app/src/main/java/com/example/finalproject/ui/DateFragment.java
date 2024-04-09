package com.example.finalproject.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.finalproject.Interfaces.AppointmentLoadListener;
import com.example.finalproject.MainActivity;
import com.example.finalproject.Models.Appointment;
import com.example.finalproject.Models.FireBaseManager;
import com.example.finalproject.Models.TimeSlot;
import com.example.finalproject.databinding.FragmentDateBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.wdullaer.materialdatetimepicker.time.Timepoint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class DateFragment extends Fragment {
    private FragmentDateBinding binding;
    private FirebaseAuth auth;
    private FireBaseManager fbm;
    private String service;
    private String price;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private HashMap<String, ArrayList<Timepoint>> disabledTimesByDate;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentDateBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Bundle bundle = getArguments();
        if (bundle != null) {
            service = bundle.getString("service");
            price = bundle.getString("price");
        }
        fbm = new FireBaseManager();
        auth = FirebaseAuth.getInstance();
        disabledTimesByDate = new HashMap<>();
        setupDatePicker();
        setupTimePicker();
        binding.pickBTNDate.setOnClickListener(v -> {
            confirmAppointment();
        });

        return root;
    }

    private void confirmAppointment() {
        // Get the selected date and time
        String date = binding.editTxtDate.getText().toString();
        String time = binding.editTxtTime.getText().toString();

        // Check if the user has selected a date and time
        if (date.isEmpty() || time.isEmpty()) {
            Toast.makeText(this.getContext(), "No full details", Toast.LENGTH_SHORT).show();
        } else {
            Appointment appointment = new Appointment()
                    .setAppointmentId(UUID.randomUUID().toString())
                    .setIdCustomer(auth.getCurrentUser().getUid())
                    .setCustomerName(auth.getCurrentUser().getDisplayName())
                    .setDate(date)
                    .setTime(time)
                    .setService(service)
                    .setPrice(price);

            fbm.saveAppointmentForUser(auth, appointment.getAppointmentId());
            fbm.saveAppointment(appointment); // Save the appointment to the database
            approveAppointment(); // Show a success message
        }
    }

    private void approveAppointment() {
        Toast.makeText(this.getContext(), "The appointment was successfully set", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this.getContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void setupDatePicker() {
        datePickerDialog = DatePickerDialog.newInstance(this::onDateSet,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 4);
        datePickerDialog.setMinDate(startDate);
        datePickerDialog.setMaxDate(endDate);
        datePickerDialog.setDisabledDays(getDisabledDays());
        binding.editTxtDate.setOnClickListener(v -> datePickerDialog.show(getParentFragmentManager(), "Datepickerdialog"));
    }


    private void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String selectedDateKey = String.format("%d/%d/%d", dayOfMonth, monthOfYear + 1, year);
        binding.editTxtDate.setText(selectedDateKey);
        binding.editTxtTime.setText(""); // Clear the time


        this.timePickerDialog= new TimePickerDialog();
        this.timePickerDialog=initializeTimePickerDialog();


        // Update TimePickerDialog with disabled times for the selected date
        ArrayList<Timepoint> disabledTimes = disabledTimesByDate.get(selectedDateKey);
        if (disabledTimes != null && !disabledTimes.isEmpty()) {
            updateTimePickerDialogWithDisabledTimes(disabledTimes);
        }
    }

    private TimePickerDialog initializeTimePickerDialog() {
        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(this::onTimeSet,
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE), true);

        timePickerDialog.setMinTime(9, 0, 0);
        timePickerDialog.setMaxTime(17, 0, 0);
        timePickerDialog.setTimeInterval(1, 30);
        getDisabledHours();
        return timePickerDialog;
    }


    private void setupTimePicker() {
        timePickerDialog = TimePickerDialog.newInstance(this::onTimeSet,
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE), true);

        timePickerDialog.setMinTime(9, 0, 0);
        timePickerDialog.setMaxTime(17, 0, 0);
        timePickerDialog.setTimeInterval(1, 30);
        getDisabledHours();
        binding.editTxtTime.setOnClickListener(v -> timePickerDialog.show(getParentFragmentManager(), "Timepickerdialog"));
    }

    private void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {

        binding.editTxtTime.setText(String.format("%d:%02d", hourOfDay, minute));
    }


    private void getDisabledHours() {
        fbm.getAllAppointments(new AppointmentLoadListener() {
            @Override
            public void onAppointmentLoaded(ArrayList<Appointment> appointments) {
                if (appointments != null)
                    processAppointments(appointments);
            }
        });
    }

    private void processAppointments(ArrayList<Appointment> appointments) {
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        ArrayList<TimeSlot> dateAndHour = new ArrayList<>();
        for (Appointment appointment : appointments) {
            dateAndHour.add(new TimeSlot().setDate(appointment.getDate()).setTime(appointment.getTime()));
        }
        for (int i = 0; i < dateAndHour.size(); i++) {
            try {
                Date date = dateTimeFormat.parse(dateAndHour.get(i).getDate() + " " + dateAndHour.get(i).getTime());
                if (date != null) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    String dateKey = dateAndHour.get(i).getDate().toString();
                    Timepoint time = new Timepoint(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));

                    if (disabledTimesByDate.containsKey(dateKey)) {
                        disabledTimesByDate.get(dateKey).add(time);
                    } else {
                        ArrayList<Timepoint> timesForDate = new ArrayList<>();
                        timesForDate.add(time);
                        disabledTimesByDate.put(dateKey, timesForDate);
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        int x = 1;
    }

    private void updateTimePickerDialogWithDisabledTimes(ArrayList<Timepoint> disabledTimes) {
        if (timePickerDialog != null) {
            timePickerDialog.setDisabledTimes(disabledTimes.toArray(new Timepoint[0]));
        }
    }


    private Calendar[] getDisabledDays() {
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 4);

        ArrayList<Calendar> disabledDays = new ArrayList<>();

        while (startDate.before(endDate)) {
            int dayOfWeek = startDate.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek == Calendar.FRIDAY || dayOfWeek == Calendar.SATURDAY) {
                Calendar disabledDay = Calendar.getInstance();
                disabledDay.setTime(startDate.getTime());
                disabledDays.add(disabledDay);
            }
            startDate.add(Calendar.DAY_OF_YEAR, 1); // Increment day by day
        }
        // Convert ArrayList to Array
        Calendar[] disabledDaysArray = new Calendar[disabledDays.size()];
        disabledDaysArray = disabledDays.toArray(disabledDaysArray);
        return disabledDaysArray;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}


