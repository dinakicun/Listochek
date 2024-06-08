package com.example.listochek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GestureDetectorCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.listochek.model.ReminderModel;

import java.util.Calendar;

public class AddReminder extends AppCompatActivity {

    private EditText nameText;
    private TimePicker timePicker;
    private Button saveBtn;
    private GestureDetectorCompat gestureDetectorCompat;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private static final int PERMISSION_REQUEST_CODE = 1;

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
        }
    }

    private boolean checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
        }
        return true; // Для версий ниже API 33 разрешение не требуется
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        nameText = findViewById(R.id.nameText);
        timePicker = findViewById(R.id.timePicker);
        saveBtn = findViewById(R.id.saveBtn);

        saveBtn.setOnClickListener(view -> {
            if (checkNotificationPermission()) {
                if (isInputValid()) {
                    saveReminder();
                }
            } else {
                requestNotificationPermission();
            }
        });

        // Запрос разрешения на уведомления при запуске
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!checkNotificationPermission()) {
                requestNotificationPermission();
            }
        }
        gestureDetectorCompat = new GestureDetectorCompat(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float diffX = e2.getX() - e1.getX();
                float diffY = e2.getY() - e1.getY();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    // Горизонтальный свайп
                    if (diffX > 0) {
                        // Свайп вправо
                        Intent intent = new Intent(AddReminder.this, ReminderList.class);
                        startActivity(intent);
                        return true;
                    }
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetectorCompat.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private boolean isInputValid() {
        String title = nameText.getText().toString().trim();
        if (title.isEmpty()) {
            Toast.makeText(this, "Название напоминания не может быть пустым", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void saveReminder() {
        String title = nameText.getText().toString().trim();
        int hour = timePicker.getCurrentHour();
        int minute = timePicker.getCurrentMinute();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            ReminderModel reminder = new ReminderModel(title, hour, minute);

            db.collection("reminders")
                    .document(userId)
                    .collection("userReminders")
                    .add(reminder)
                    .addOnSuccessListener(documentReference -> {
                        reminder.setId(documentReference.getId());
                        db.collection("reminders")
                                .document(userId)
                                .collection("userReminders")
                                .document(reminder.getId())
                                .set(reminder)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(AddReminder.this, "Напоминание сохранено", Toast.LENGTH_SHORT).show();
                                    setDailyReminder(reminder);
                                    Intent intent = new Intent(AddReminder.this, PrivateAccount.class);
                                    startActivity(intent);
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(AddReminder.this, "Ошибка сохранения ID напоминания", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AddReminder.this, "Ошибка сохранения напоминания", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "Пользователь не авторизован", Toast.LENGTH_SHORT).show();
        }
    }

    private void setDailyReminder(ReminderModel reminder) {
        if (!checkNotificationPermission()) {
            Toast.makeText(this, "Нет разрешения на уведомления", Toast.LENGTH_SHORT).show();
            requestNotificationPermission();
            return;
        }

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("title", reminder.getTitle());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, reminder.getId().hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, reminder.getHour());
        calendar.set(Calendar.MINUTE, reminder.getMinute());
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        try {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);
        } catch (SecurityException e) {
            Toast.makeText(this, "Ошибка установки будильника: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Разрешение предоставлено, можно установить напоминание
                if (isInputValid()) {
                    saveReminder();
                }
            } else {
                Toast.makeText(this, "Разрешение отклонено", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
