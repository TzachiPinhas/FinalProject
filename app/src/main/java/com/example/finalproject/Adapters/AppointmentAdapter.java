package com.example.finalproject.Adapters;

import android.content.Context;
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

import java.util.ArrayList;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {

    private Context context;
    private ArrayList<Appointment> appointmentslist;
    private CancelCallback cancelCallback;

    public AppointmentAdapter(Context context, ArrayList<Appointment> appointmentslist) {
        this.context = context;
        this.appointmentslist = appointmentslist;
    }

    public AppointmentAdapter setAppointmentCallBack(CancelCallback cancelCallback) {
        this.cancelCallback = cancelCallback;
        return this;
    }


    @NonNull
    @Override
    public AppointmentAdapter.AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horisontal_appointment_item, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentAdapter.AppointmentViewHolder holder, int position) {
        Appointment appointment = getItem(position);
        holder.service_price.setText(appointment.getPrice());
        holder.type_Service.setText(appointment.getService());
        holder.service_details.setText("On " + appointment.getDate() + ", at " + appointment.getTime());

    }

    private Appointment getItem(int position) {
        return appointmentslist.get(position);
    }

    @Override
    public int getItemCount() {
        if (appointmentslist == null)
            return 0;
        return appointmentslist.size();
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
            cancel_BTN.setOnClickListener(v -> {
                        if (cancelCallback != null) {
                            cancelCallback.cancelCallback(getItem(getAdapterPosition()), getAdapterPosition());
                        }
                    }
            );
        }
    }
}
