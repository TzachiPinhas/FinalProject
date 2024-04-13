package com.example.finalproject.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.Interfaces.CancelCallback;
import com.example.finalproject.Models.Appointment;
import com.example.finalproject.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {

    private Context context;
    private ArrayList<Appointment> futureAppointments;
    private ArrayList<Appointment> pastAppointments;
    private boolean showPastAppointments;  // Use this to toggle which list to display
    private CancelCallback cancelCallback;

    public AppointmentAdapter(Context context, ArrayList<Appointment> appointments, boolean showPastAppointments) {
        this.context = context;
        this.futureAppointments = new ArrayList<>();
        this.pastAppointments = new ArrayList<>();
        this.showPastAppointments = showPastAppointments;
        splitAppointments(appointments);
    }

    public AppointmentAdapter setAppointmentCallBack(CancelCallback cancelCallback) {
        this.cancelCallback = cancelCallback;
        return this;
    }
    private void splitAppointments(ArrayList<Appointment> appointments) {
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        for (Appointment appointment : appointments) {
            try {
                Date appointmentDate = dateFormat.parse(appointment.getDate() + " " + appointment.getTime());
                if (appointmentDate != null && appointmentDate.before(currentDate)) {
                    pastAppointments.add(appointment);
                } else {
                    futureAppointments.add(appointment);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    @NonNull
    @Override
    public AppointmentAdapter.AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horisontal_appointment_item, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointment appointment = getItem(position);
        holder.service_price.setText(appointment.getPrice());
        holder.type_Service.setText(appointment.getService());
        holder.service_details.setText("On " + appointment.getDate() + ", at " + appointment.getTime());

        // Here we check if we should show the cancel button
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            Date appointmentDate = dateFormat.parse(appointment.getDate() + " " + appointment.getTime());
            if (appointmentDate != null && appointmentDate.before(new Date())) {
                holder.cancel_BTN.setVisibility(View.GONE);
                holder.service_details.setText("Done on " + appointment.getDate() + ", at " + appointment.getTime());
            } else {
                holder.cancel_BTN.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private Appointment getItem(int position) {
        return showPastAppointments ? pastAppointments.get(position) : futureAppointments.get(position);
    }

    @Override
    public int getItemCount() {
        return showPastAppointments ? pastAppointments.size() : futureAppointments.size();
    }


    public class AppointmentViewHolder extends RecyclerView.ViewHolder {
        private MaterialTextView type_Service;
        private MaterialTextView service_details;
        private MaterialButton cancel_BTN;
        private MaterialTextView service_price;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            type_Service = itemView.findViewById(R.id.type_Service);
            service_details = itemView.findViewById(R.id.service_details);
            service_price = itemView.findViewById(R.id.service_price);
            cancel_BTN = itemView.findViewById(R.id.cancel_BTN);

            // Set up the OnClickListener for the cancel button
            cancel_BTN.setOnClickListener(v -> {
                // Create an AlertDialog to confirm cancellation
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Confirm Cancellation");
                builder.setMessage("Are you sure you want to cancel this appointment?");

                // Positive Button confirms cancellation
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (cancelCallback != null) {
                            cancelCallback.cancelCallback(getItem(getAdapterPosition()), getAdapterPosition());
                        }
                    }
                });

                // Negative Button dismisses the dialog and does nothing
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

                // Create and show the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();
            });
        }
    }

}
