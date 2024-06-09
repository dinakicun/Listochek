package com.example.listochek.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.listochek.R;
import com.example.listochek.model.ReminderModel;

import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {

    private List<ReminderModel> reminderList;
    private OnReminderClickListener listener;

    public ReminderAdapter(List<ReminderModel> reminderList, OnReminderClickListener listener) {
        this.reminderList = reminderList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reminder, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        ReminderModel reminder = reminderList.get(position);
        holder.titleTextView.setText(reminder.getTitle());
        holder.timeTextView.setText(String.format("%02d:%02d", reminder.getHour(), reminder.getMinute()));
        holder.deleteReminderBtn.setOnClickListener(v -> listener.onReminderClick(reminder));
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    public interface OnReminderClickListener {
        void onReminderClick(ReminderModel reminder);
    }

    public static class ReminderViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView;
        TextView timeTextView;
        ImageButton deleteReminderBtn;

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.reminderTitleTextView);
            timeTextView = itemView.findViewById(R.id.reminderTimeTextView);
            deleteReminderBtn = itemView.findViewById(R.id.deleteReminderBtn);
        }
    }
}
