package com.example.finalproject.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.Interfaces.ServiceCallback;
import com.example.finalproject.Models.Service;
import com.example.finalproject.R;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {
    private Context context;
    private ArrayList<Service> services;
    private ServiceCallback serviceCallback;

    public ServiceAdapter(Context context, ArrayList<Service> services) {
        this.context = context;
        this.services = services;
    }

    public ServiceAdapter setServiceCallBack(ServiceCallback serviceCallback) {
        this.serviceCallback = serviceCallback;
        return this;
    }

    @NonNull
    @Override
    public ServiceAdapter.ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horisontal_service_item, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = getItem(position);
        holder.type_Service.setText(service.getName());
        holder.service_description.setText(service.getDescription());
        holder.service_price.setText(service.getPrice());
    }

    @Override
    public int getItemCount() {
        if (services == null)
            return 0;
        return services.size();
    }

    private Service getItem(int position) {
        return services.get(position);
    }

    public class ServiceViewHolder extends RecyclerView.ViewHolder {
        private MaterialTextView type_Service;
        private MaterialTextView service_description;
        private MaterialTextView service_price;


        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            type_Service = itemView.findViewById(R.id.type_Service);
            service_description = itemView.findViewById(R.id.service_description);
            service_price = itemView.findViewById(R.id.service_price);


            type_Service.setOnClickListener(v -> {
                        if (serviceCallback != null) {
                            serviceCallback.ServiceClicked(getItem(getAdapterPosition()));
                        }
                    }
            );
        }
    }


}
