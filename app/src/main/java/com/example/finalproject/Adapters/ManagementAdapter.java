package com.example.finalproject.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.Interfaces.BlockDayCallback;
import com.example.finalproject.Models.BlockDay;
import com.example.finalproject.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ManagementAdapter extends RecyclerView.Adapter<ManagementAdapter.ManagementViewHolder> {

    private Context context;

    private ArrayList<BlockDay> futureDays;

    private BlockDayCallback blockDayCallback;

    public ManagementAdapter(Context context, ArrayList<BlockDay> blockDays) {
        this.context = context;
        futureDays = new ArrayList<>();
        splitDays(blockDays);
    }

    public ManagementAdapter setBlockDayCallback(BlockDayCallback blockDayCallback) {
        this.blockDayCallback = blockDayCallback;
        return this;
    }

    private void splitDays(ArrayList<BlockDay> blockDays) {
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            currentDate = dateFormat.parse(dateFormat.format(currentDate)); // Normalize currentDate to start of the day
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (BlockDay blockDay : blockDays) {
            try {
                Date appointmentDate = dateFormat.parse(blockDay.getDate());
                if (appointmentDate != null && !appointmentDate.before(currentDate)) { // Only show future days in the list of block days
                    futureDays.add(blockDay);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @NonNull
    @Override
    public ManagementAdapter.ManagementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horisontal_block_day_item, parent, false);
        return new ManagementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ManagementAdapter.ManagementViewHolder holder, int position) {
        BlockDay blockDay = getItem(position);
        holder.date_block.setText(blockDay.getDate());
        holder.reason_details.setText(blockDay.getNote());
    }

    @Override
    public int getItemCount() {
        if (futureDays == null)
            return 0;
        return futureDays.size();

    }

    private BlockDay getItem(int position) {
        return futureDays.get(position);
    }

    public class ManagementViewHolder extends RecyclerView.ViewHolder {
        private MaterialTextView date_block;
        private MaterialTextView reason_details;
        private FloatingActionButton cancel_block;

        public ManagementViewHolder(@NonNull View itemView) {
            super(itemView);
            date_block = itemView.findViewById(R.id.date_block);
            reason_details = itemView.findViewById(R.id.reason_details);
            cancel_block = itemView.findViewById(R.id.cancel_BTN);
            cancel_block.setOnClickListener(v -> { // Show confirmation dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Confirm Cancellation");
                builder.setMessage("Are you sure you want to perform the following action?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (blockDayCallback != null) {
                            blockDayCallback.cancelDayCallback(getItem(getAdapterPosition()), getAdapterPosition());
                        }
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            });
        }
    }
}
