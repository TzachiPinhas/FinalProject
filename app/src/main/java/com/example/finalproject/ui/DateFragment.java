package com.example.finalproject.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.finalproject.Listeners.AppointmentLoadListener;
import com.example.finalproject.Listeners.DisabledDaysLoadListener;
import com.example.finalproject.Listeners.UserLoadListener;
import com.example.finalproject.MainActivity;
import com.example.finalproject.Models.Appointment;
import com.example.finalproject.Models.BlockDay;
import com.example.finalproject.Models.FireBaseManager;
import com.example.finalproject.Models.TimeSlot;
import com.example.finalproject.Models.User;
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
import java.util.Locale;
import java.util.UUID;

public class DateFragment extends Fragment {
    private FragmentDateBinding binding;
    private FirebaseAuth auth;
    private FireBaseManager fbm;
    private String service;
    private String price;
    private String phone;
    private String name;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private ArrayList<BlockDay> allBlockDays;
    private HashMap<String, ArrayList<Timepoint>> disabledTimesByDate;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentDateBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Bundle bundle = getArguments();
        if (bundle != null) {
            service = bundle.getString("service"); // Get the service name from the bundle arguments passed from the previous fragment (ServiceFragment)
            price = bundle.getString("price"); // Get the service price from the bundle arguments passed from the previous fragment (ServiceFragment)
        }
        fbm = new FireBaseManager();
        auth = FirebaseAuth.getInstance();
        allBlockDays = new ArrayList<>();
        disabledTimesByDate = new HashMap<>();
        getDisableDaysByBarber(); // Get the disabled days from the database
        getDetails(); // Get the user details from the database
        setupTimePicker();
        binding.pickBTNDate.setOnClickListener(v -> {
            confirmAppointment();
        });

        return root;
    }


    private void confirmAppointment() { // if the user has selected a date and time, save the appointment to the database
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
                    .setCustomerName(name)
                    .setDate(date)
                    .setTime(time)
                    .setService(service)
                    .setPrice(price)
                    .setCostumerPhone(phone);
            // Save the appointment to the database



            fbm.saveAppointmentForUser(auth, appointment.getAppointmentId()); // Save the appointment to the user's appointments
            fbm.saveAppointment(appointment); // Save the appointment to the database
            approveAppointment(); // Show a success message
        }
    }

    private void getDetails() {
        fbm.getUserByUID(auth.getCurrentUser().getUid(), new UserLoadListener() {
            @Override
            public void onUserLoaded(User userDetails) {
                phone = userDetails.getPhone();
                name = userDetails.getName();
            }
            @Override
            public void onUserLoadFailed(String message) {
                System.err.println("Failed to load user: " + message);
            }
        });
    }

    private void approveAppointment() {
        Toast.makeText(this.getContext(), "The appointment was successfully set", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this.getContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void setupDatePicker() {
        if (binding == null) {
            return; // Skip setting up the date picker if binding is null
        }
        datePickerDialog = DatePickerDialog.newInstance(this::onDateSet, //onDateSet is called when the user selects a date
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
        binding.editTxtTime.setText(""); // Clear the time when the user selects a new date
        this.timePickerDialog = new TimePickerDialog();
        this.timePickerDialog = initializeTimePickerDialog();

        // Update TimePickerDialog with disabled times for the selected date
        ArrayList<Timepoint> disabledTimes = disabledTimesByDate.get(selectedDateKey); // Get the disabled times for the selected date
        if (disabledTimes != null && !disabledTimes.isEmpty()) { // If there are disabled times for the selected date, don't allow the user to select them
            updateTimePickerDialogWithDisabledTimes(disabledTimes);
        }
    }

    private void updateTimePickerDialogWithDisabledTimes(ArrayList<Timepoint> disabledTimes) { // Update the TimePickerDialog with the disabled times
        if (timePickerDialog != null) {
            timePickerDialog.setDisabledTimes(disabledTimes.toArray(new Timepoint[0]));
        }
    }

    private Calendar[] getDisabledDays() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());  // Assuming BlockDay uses this format
        Calendar now = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 4);

        ArrayList<Calendar> disabledDays = new ArrayList<>();

        int currentHour = now.get(Calendar.HOUR_OF_DAY);
        int currentMinute = now.get(Calendar.MINUTE);
        if (currentHour > 17 || (currentHour == 17 && currentMinute > 0)) {
            Calendar disabledToday = Calendar.getInstance();
            disabledDays.add(disabledToday);
        }

        while (startDate.before(endDate)) { // Iterate through the next 4 months
            int dayOfWeek = startDate.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek == Calendar.FRIDAY || dayOfWeek == Calendar.SATURDAY) {
                Calendar disabledDay = Calendar.getInstance();
                disabledDay.setTime(startDate.getTime());
                disabledDays.add(disabledDay);
            }
            startDate.add(Calendar.DAY_OF_YEAR, 1); // Increment day by day
        }

        for (BlockDay blockDay : allBlockDays) { // Add the disabled days from the database by the barber
            try {
                Date blockDate = sdf.parse(blockDay.getDate());
                Calendar blockCalendar = Calendar.getInstance();
                blockCalendar.setTime(blockDate);
                disabledDays.add(blockCalendar);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        // Convert ArrayList to Array
        Calendar[] disabledDaysArray = new Calendar[disabledDays.size()];
        disabledDaysArray = disabledDays.toArray(disabledDaysArray);
        return disabledDaysArray;
    }

    private void getDisableDaysByBarber() {
        fbm.getAllDisableDate(new DisabledDaysLoadListener() {
            @Override
            public void onDaysLoad(ArrayList<BlockDay> blockDays) {
                if (blockDays != null) {
                    processDisableDays(blockDays);
                }
            }
        });
    }

    private void processDisableDays(ArrayList<BlockDay> blockDays) {
        allBlockDays.clear();
        allBlockDays.addAll(blockDays);
        setupDatePicker();
    }

    private TimePickerDialog initializeTimePickerDialog() { // Initialize the TimePickerDialog with the selected date every time the user selects a date
        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(this::onTimeSet,
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE), true);

        timePickerDialog.setMinTime(9, 0, 0);
        timePickerDialog.setMaxTime(17, 0, 0);
        timePickerDialog.setTimeInterval(1, 30);
        getDisabledHours();
        return timePickerDialog;
    }


    private void setupTimePicker() { // Set up the TimePickerDialog for the first time when the fragment is created (before the user selects a date)
        timePickerDialog = TimePickerDialog.newInstance(this::onTimeSet, //onTimeSet is called when the user selects a time
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE), true);

        timePickerDialog.setMinTime(9, 0, 0);
        timePickerDialog.setMaxTime(17, 0, 0);
        timePickerDialog.setTimeInterval(1, 30);
        getDisabledHours();
        binding.editTxtTime.setOnClickListener(v -> timePickerDialog.show(getParentFragmentManager(), "Timepickerdialog"));
    }

    private void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {

        binding.editTxtTime.setText(String.format("%d:%02d", hourOfDay, minute)); // Set the selected time in the EditText
    }


    private void getDisabledHours() { // Get the disabled hours from the database
        fbm.getAllAppointments(new AppointmentLoadListener() { // Get all appointments for
            @Override
            public void onAppointmentLoaded(ArrayList<Appointment> appointments) {
                if (appointments != null)
                    processAppointments(appointments);
            }
        });
    }

    private void processAppointments(ArrayList<Appointment> appointments) { // Process the appointments and save for each date the disabled times in a HashMap
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");// Format for the date and time
        ArrayList<TimeSlot> dateAndHour = new ArrayList<>();// List of dates and times for the appointments type TimeSlot
        for (Appointment appointment : appointments) {
            dateAndHour.add(new TimeSlot().setDate(appointment.getDate()).setTime(appointment.getTime())); // Add the date and time to the list of TimeSlots
        }
        for (int i = 0; i < dateAndHour.size(); i++) {
            try {
                Date date = dateTimeFormat.parse(dateAndHour.get(i).getDate() + " " + dateAndHour.get(i).getTime()); // Parse the date and time to a Date object
                if (date != null) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date); // Set the Calendar object to the date and time
                    String dateKey = dateAndHour.get(i).getDate().toString();
                    Timepoint time = new Timepoint(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE)); // Create a Timepoint object with the hour and minute

                    if (disabledTimesByDate.containsKey(dateKey)) {  // If the date is already in the HashMap, add the time to the list of disabled times
                        disabledTimesByDate.get(dateKey).add(time);
                    } else { // If the date is not in the HashMap, create a new list of disabled times for the date
                        ArrayList<Timepoint> timesForDate = new ArrayList<>();
                        timesForDate.add(time);
                        disabledTimesByDate.put(dateKey, timesForDate);
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}