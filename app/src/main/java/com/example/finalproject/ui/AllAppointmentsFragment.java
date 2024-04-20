package com.example.finalproject.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.Adapters.AppointmentAdapter;
import com.example.finalproject.Listeners.AppointmentLoadListener;
import com.example.finalproject.Listeners.BarberListener;
import com.example.finalproject.Interfaces.CancelCallback;
import com.example.finalproject.Models.Appointment;
import com.example.finalproject.Models.FireBaseManager;
import com.example.finalproject.R;
import com.example.finalproject.databinding.FragmentAllAppointmentsBinding;
import com.google.android.material.button.MaterialButton;
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


public class AllAppointmentsFragment extends Fragment {
    private RecyclerView main_LST_appointments;
    private MaterialButton show_BTN_history;
    private FloatingActionButton show_BTN_all;
    private ArrayList<Appointment> appointments;
    private ArrayList<Appointment> myAppointments;
    private FirebaseAuth auth;
    private FireBaseManager fbm;
    private FragmentAllAppointmentsBinding binding;
    private View root;
    private int count;
    private boolean isBarber;
    private EditText edit_Date;
    private DatePickerDialog datePickerDialog;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentAllAppointmentsBinding.inflate(inflater, container, false);
        count = 0;
        appointments = new ArrayList<>();
        myAppointments = new ArrayList<>();
        root = binding.getRoot();
        auth = FirebaseAuth.getInstance();
        fbm = new FireBaseManager();
        checkIfBarber();
        findViews();
        getAllAppointmentFromData();
        showDatePickerDialog();
        initViews();

        return root;
    }

    private void checkIfBarber() {
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
            edit_Date.setVisibility(View.VISIBLE);
            show_BTN_all.setVisibility(View.VISIBLE);
        } else {
            edit_Date.setVisibility(View.GONE);
            show_BTN_all.setVisibility(View.GONE);

        }
    }

    private void initViews() {
        show_BTN_history.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.nav_myPastAppointments); // Navigate to the past appointments fragment
        });

        show_BTN_all.setOnClickListener(v -> {
            updateAppointmentView(appointments); // Reset all appointments in the list for the barber
        });
    }

    private synchronized void getAllAppointmentFromData() { // Get all appointments from the database
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

    private void getYourAppointments(ArrayList<Appointment> allAppointments) {
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

    private void sortAppointments(ArrayList<Appointment> Appointments) {
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
        AppointmentAdapter appointmentAdapter = new AppointmentAdapter(root.getContext(), myAppointments, false, isBarber);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(root.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        main_LST_appointments.setLayoutManager(linearLayoutManager);
        main_LST_appointments.setAdapter(appointmentAdapter);

        count = appointmentAdapter.getItemCount();
        if (count == 0) {
            updateNoAppointmentsView();
        }
        appointmentAdapter.setAppointmentCallBack(new CancelCallback() { //if the user wants to cancel an appointment
            @Override
            public void cancelCallback(Appointment appointment, int position) {
                fbm.removeAppointment(appointment);
                fbm.removeAppointmentForUser(appointment.getIdCustomer(), appointment.getAppointmentId());
                getAllAppointmentFromData();
            }
        });
    }


    private void updateNoAppointmentsView() { // Update the UI if there are no appointments
        TextView noAppointmentsText = root.findViewById(R.id.no_appointments_text);
        noAppointmentsText.setText("No future appointments at the moment.");
        noAppointmentsText.setVisibility(View.VISIBLE);
        main_LST_appointments.setVisibility(View.GONE);
    }

    public void showDatePickerDialog() {
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
        edit_Date.setOnClickListener(v -> datePickerDialog.show(getParentFragmentManager(), "Datepickerdialog"));
    }


    private void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String selectedDateKey = String.format("%d/%d/%d", dayOfMonth, monthOfYear + 1, year);
        edit_Date.setText(selectedDateKey);
        filterAppointmentsByDate(selectedDateKey);
        edit_Date.setText("");
        edit_Date.setHint("Display appointments by date");
    }

    private void filterAppointmentsByDate(String date) { // Filter appointments by date
        ArrayList<Appointment> filteredAppointments = new ArrayList<>();
        for (Appointment appointment : appointments) {
            if (appointment.getDate().equals(date)) {
                filteredAppointments.add(appointment);
            }
        }
        updateAppointmentView(filteredAppointments);
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


    private void updateAppointmentView(ArrayList<Appointment> appointments) {
        sortAppointments(appointments);
        AppointmentAdapter adapter = new AppointmentAdapter(getContext(), appointments, false, isBarber);
        main_LST_appointments.setAdapter(adapter);
    }


    private void findViews() {
        main_LST_appointments = binding.mainLSTAppointments;
        show_BTN_history = binding.showBTNHistory;
        edit_Date = binding.editDate;
        show_BTN_all = binding.showBTNAll;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}