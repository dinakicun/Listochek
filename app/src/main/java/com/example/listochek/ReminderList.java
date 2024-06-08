package com.example.listochek;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.listochek.model.ReminderModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ReminderList extends AppCompatActivity {

    private RecyclerView reminderRecyclerView;
    private com.example.listochek.adapter.ReminderAdapter reminderAdapter;
    private List<ReminderModel> reminderList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ImageButton addReminder, backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_list);

        reminderRecyclerView = findViewById(R.id.reminderRecyclerView);
        reminderRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        reminderList = new ArrayList<>();
        reminderAdapter = new com.example.listochek.adapter.ReminderAdapter(reminderList, this::deleteReminder);
        reminderRecyclerView.setAdapter(reminderAdapter);

        addReminder = findViewById(R.id.addReminderBtn);
        addReminder.setOnClickListener(v -> {
            Intent intentAdd = new Intent(ReminderList.this, AddReminder.class);
            startActivity(intentAdd);
        });

        backButton = findViewById(R.id.backBtn);
        backButton.setOnClickListener(v -> {
            Intent intentBack = new Intent(ReminderList.this, PrivateAccount.class);
            startActivity(intentBack);
        });

        loadReminders();
    }

    private void loadReminders() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            db.collection("reminders")
                    .document(userId)
                    .collection("userReminders")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            reminderList.clear();
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null) {
                                for (QueryDocumentSnapshot document : querySnapshot) {
                                    ReminderModel reminder = document.toObject(ReminderModel.class);
                                    reminder.setId(document.getId());
                                    reminderList.add(reminder);
                                }
                                reminderAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(ReminderList.this, "Ошибка загрузки напоминаний", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void deleteReminder(ReminderModel reminder) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            db.collection("reminders")
                    .document(userId)
                    .collection("userReminders")
                    .document(reminder.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        reminderList.remove(reminder);
                        reminderAdapter.notifyDataSetChanged();
                        cancelAlarm(reminder);
                        Toast.makeText(ReminderList.this, "Напоминание удалено", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(ReminderList.this, "Ошибка удаления напоминания", Toast.LENGTH_SHORT).show());
        }
    }


    private void cancelAlarm(ReminderModel reminder) {
        Intent intent = new Intent(this, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, reminder.getId().hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}
