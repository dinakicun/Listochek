package com.example.listochek;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.listochek.model.CharacteristicsModel;
import com.example.listochek.model.UserModel;
import com.example.listochek.utils.FirebaseUtil;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import androidx.core.view.GestureDetectorCompat;

public class PrivateAccount extends AppCompatActivity {
    private TextView nameText, ageText, weightText, heightText, caloriesText;
    private LinearLayout changeData, exit, myDishes, reminders;
    private GestureDetectorCompat gestureDetectorCompat;

    private static final String PREFS_NAME = "WaterPrefs";
    private static final String LAST_UPDATE_KEY = "lastWaterPointsUpdate";
    private static final String FOOD_PREFS_NAME = "FoodPrefs";
    private static final String LAST_FOOD_UPDATE_KEY = "lastFoodPointsUpdate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_account);

        nameText = findViewById(R.id.nameText);
        ageText = findViewById(R.id.ageText);
        weightText = findViewById(R.id.weightText);
        heightText = findViewById(R.id.heightText);
        caloriesText = findViewById(R.id.caloriesText);
        changeData = findViewById(R.id.changeDataLL);
        exit = findViewById(R.id.exitLL);
        myDishes = findViewById(R.id.myDishesLL);
        reminders = findViewById(R.id.remindersLL);
        loadUserData();

        changeData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrivateAccount.this, ChangePersonalData.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

        });
        reminders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrivateAccount.this, ReminderList.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

        });
        myDishes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrivateAccount.this, UsersDishes.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.remove("user_id");
                editor.apply();

                // Очистка LAST_FOOD_UPDATE_KEY
                SharedPreferences foodPrefs = getSharedPreferences(FOOD_PREFS_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor foodEditor = foodPrefs.edit();
                foodEditor.remove(LAST_FOOD_UPDATE_KEY);
                foodEditor.apply();

                // Очистка LAST_UPDATE_KEY
                SharedPreferences waterPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor waterEditor = waterPrefs.edit();
                waterEditor.remove(LAST_UPDATE_KEY);
                waterEditor.apply();

                Intent intent = new Intent(PrivateAccount.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

        });
        gestureDetectorCompat = new GestureDetectorCompat(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float diffX = e2.getX() - e1.getX();
                float diffY = e2.getY() - e1.getY();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    // Горизонтальный свайп
                    if (diffX > 0) {
                        // Свайп вправо
                        Intent intent = new Intent(PrivateAccount.this, HomePage.class);
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

    private void loadUserData() {
        String userId = FirebaseUtil.currentUserId();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    UserModel userModel = document.toObject(UserModel.class);
                    if (userModel != null) {
                        nameText.setText(userModel.getName());
                        ageText.setText("Возраст: " + userModel.getAge() + " лет");
                        weightText.setText("Вес: " + userModel.getWeight() + " кг");
                        heightText.setText("Рост: " + userModel.getHeight() + " см");

                        loadUserCharacteristics(userId);
                    }
                }
            } else {
            }
        });
    }
    private void loadUserCharacteristics(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("characteristics").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    CharacteristicsModel characteristicsModel = document.toObject(CharacteristicsModel.class);
                    if (characteristicsModel != null) {
                        caloriesText.setText("Калории: " + characteristicsModel.getCalories() + " калл");
                    }
                }
            } else {
            }
        });
    }
}
