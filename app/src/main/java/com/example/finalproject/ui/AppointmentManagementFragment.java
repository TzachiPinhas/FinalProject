package com.example.finalproject.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.Adapters.ManagementAdapter;
import com.example.finalproject.Interfaces.BlockDayCallback;
import com.example.finalproject.Listeners.AppointmentLoadListener;
import com.example.finalproject.Listeners.DisabledDaysLoadListener;
import com.example.finalproject.Models.Appointment;
import com.example.finalproject.Models.BlockDay;
import com.example.finalproject.Models.FireBaseManager;
import com.example.finalproject.databinding.FragmentAppointmentManagementBinding;
import com.google.android.material.button.MaterialButton;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.UUID;

public class AppointmentManagementFragment extends Fragment {

    private FragmentAppointmentManagementBinding binding;
    private RecyclerView blockDayRecyclerView;
    private FireBaseManager fbm;
    private ArrayList<BlockDay> allBlockDays;
    private HashSet<String> uniqueAppointmentDates;
    private EditText editBlockDate;
    private EditText noteText;
    private MaterialButton blockDayButton;
    private DatePickerDialog datePickerDialog;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentAppointmentManagementBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        allBlockDays = new ArrayList<>();
        uniqueAppointmentDates = new HashSet<>(); //for unique dates only
        fbm = new FireBaseManager();
        findViews();
        getAllAppointmentFromData(); // Get all appointments from the database
        getDisableDaysByBarber(); // Get all disabled days from the database
        initViews();

        return root;
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
        editBlockDate.setOnClickListener(v -> datePickerDialog.show(getParentFragmentManager(), "Datepickerdialog"));
    }

    private void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String selectedDateKey = String.format("%d/%d/%d", dayOfMonth, monthOfYear + 1, year);
        editBlockDate.setText(selectedDateKey);
    }

    private Calendar[] getDisabledDays() { // This method will return an array of disabled days to disable in the date picker
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());  // Assuming BlockDay uses this format
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

        for (String blockDateStr : uniqueAppointmentDates) {
            try {
                Date blockDate = sdf.parse(blockDateStr);
                Calendar blockCalendar = Calendar.getInstance();
                blockCalendar.setTime(blockDate);
                disabledDays.add(blockCalendar);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        for (BlockDay blockDay : allBlockDays) {
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


    private void getAllAppointmentFromData() { // This method will get all appointments from the database
        fbm.getAllAppointments(new AppointmentLoadListener() {
            @Override
            public void onAppointmentLoaded(ArrayList<Appointment> appointments) {
                if (appointments != null) {
                    for (Appointment appointment : appointments) {
                        uniqueAppointmentDates.add(appointment.getDate());  // Add only unique dates
                    }
                }
            }
        });
    }


    private void initViews() {

        blockDayButton.setOnClickListener(new View.OnClickListener() { // This method will save a disabled day to the database
            @Override
            public void onClick(View v) {
                String date = editBlockDate.getText().toString();
                String reasonBlock = noteText.getText().toString();

                if (date.isEmpty() || reasonBlock.isEmpty()) {
                    Toast.makeText(getContext(), "Please fill all fields.", Toast.LENGTH_LONG).show();
                    return; // Stop the submission if validation fails
                }
                BlockDay blockDay = new BlockDay()
                        .setBlockDayId(UUID.randomUUID().toString())
                        .setDate(date)
                        .setNote(reasonBlock);

                fbm.saveDisableDate(blockDay); // Save the block day to the database

                getDisableDaysByBarber(); // Update the UI with the new disabled day added to the database

                editBlockDate.setText("");
                editBlockDate.setHint("Choose date: ");
                noteText.setText("");
            }
        });
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
        // after getting all disabled days from the database, this method will process them and update the UI accordingly by sorting them
        allBlockDays.clear();
        allBlockDays.addAll(blockDays);
        Collections.sort(allBlockDays, new Comparator<BlockDay>() {
            @Override
            public int compare(BlockDay o1, BlockDay o2) {
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    Date date1 = format.parse(o1.getDate());
                    Date date2 = format.parse(o2.getDate());
                    return date1.compareTo(date2);
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });
        setupDatePicker(); //
        // call this method here to update the disabled days in the date picker
        updateAdapter();
    }

    private void updateAdapter() {
        ManagementAdapter adapter = new ManagementAdapter(getContext(), allBlockDays);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        blockDayRecyclerView.setLayoutManager(linearLayoutManager);
        blockDayRecyclerView.setAdapter(adapter);

        adapter.setBlockDayCallback(new BlockDayCallback() {
            @Override
            public void cancelDayCallback(BlockDay blockDay, int position) { // This method will remove a disabled day from the database and update the UI
                fbm.removeDisableDate(blockDay);
                getDisableDaysByBarber();
            }
        });
    }

    private void findViews() {
        editBlockDate = binding.editBlockDateTxt;
        noteText = binding.noteTxt;
        blockDayButton = binding.blockBtnDay;
        blockDayRecyclerView = binding.blockDaysRecyclerView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}