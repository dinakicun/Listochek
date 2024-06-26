package com.example.listochek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.listochek.model.CharacteristicsModel;
import com.example.listochek.model.PlantModel;
import com.example.listochek.model.UserModel;
import com.example.listochek.utils.FirebaseUtil;
import com.example.listochek.utils.NutritionCalculator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.core.view.GestureDetectorCompat;

import java.util.Date;

public class RegistrationFinish extends AppCompatActivity {
    String age;
    String name;
    String height;
    String weight;
    String sex;
    String email;
    UserModel userModel;
    Button notActive;
    Button active;
    Button save;
    CharacteristicsModel nutrition;
    boolean active_lifestyle;
    private boolean isLifestyleSelected = false;
    private GestureDetectorCompat gestureDetectorCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_finish);

        name = getIntent().getExtras().getString("name");
        age = getIntent().getExtras().getString("age");
        height = getIntent().getExtras().getString("height");
        weight = getIntent().getExtras().getString("weight");
        active_lifestyle = getIntent().getExtras().getBoolean("active_lifestyle");
        sex = getIntent().getExtras().getString("sex");
        email = FirebaseUtil.currentUserEmail();

        notActive = findViewById(R.id.notActiveLS);
        active = findViewById(R.id.activeLS);
        save = findViewById(R.id.saveLifestyle);

        notActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                active_lifestyle = false;
                ((MaterialButton) notActive).setStrokeColor(ColorStateList.valueOf(Color.parseColor("#B1E996")));
                ((MaterialButton) active).setStrokeColor(ColorStateList.valueOf(Color.parseColor("#E5E3E1")));
                isLifestyleSelected = true;
            }
        });

        active.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                active_lifestyle = true;
                ((MaterialButton) active).setStrokeColor(ColorStateList.valueOf(Color.parseColor("#B1E996")));
                ((MaterialButton) notActive).setStrokeColor(ColorStateList.valueOf(Color.parseColor("#E5E3E1")));
                isLifestyleSelected = true;
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLifestyleSelected) {
                    Toast.makeText(RegistrationFinish.this, "Пожалуйста, выберите образ жизни для продолжения", Toast.LENGTH_LONG).show();
                } else {
                    getUser();
                }
            }
        });
    }

    void setUser() {
        Integer intAge = Integer.parseInt(age);
        Integer intHeight = Integer.parseInt(height);
        Integer intWeight = Integer.parseInt(weight);
        String userId = FirebaseUtil.currentUserId();
        if (userModel == null) {
            userModel = new UserModel(email, name, intAge, intHeight, intWeight, sex, active_lifestyle);
            nutrition = NutritionCalculator.calculateNutrition(sex, intAge, intHeight, intWeight, active_lifestyle, userId);
        }

        FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    saveNutritionDetails(nutrition);
                    savePlantDetails(userId);  // Вызов метода сохранения данных растения
                    SharedPreferences sharedPref = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("user_id", userId);
                    editor.apply();
                    Intent intent = new Intent(RegistrationFinish.this, HomePage.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });
    }

    void saveNutritionDetails(CharacteristicsModel nutrition) {
        FirebaseUtil.currentCharacteristicsDetails()
                .set(nutrition)
                .addOnSuccessListener(aVoid -> Toast.makeText(RegistrationFinish.this, "Данные сохранены", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(RegistrationFinish.this, "Ошибка сбора данных", Toast.LENGTH_SHORT).show());
    }

    void savePlantDetails(String userId) {
        Date currentDate = new Date();
        PlantModel plantModel = new PlantModel();
        plantModel.setLastWatered(currentDate);
        plantModel.setLastFertilized(currentDate);
        plantModel.setWaterExperience(1000);
        plantModel.setFertilizerExperience(1000);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("plants").document(userId).set(plantModel)
                .addOnSuccessListener(aVoid -> Log.d("RegistrationFinish", "Plant data saved successfully"))
                .addOnFailureListener(e -> Log.e("RegistrationFinish", "Failed to save plant data", e));
    }

    void getUser() {
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    userModel = task.getResult().toObject(UserModel.class);
                    if (userModel == null) {
                        setUser();
                    }
                }
            }
        });
    }
}
