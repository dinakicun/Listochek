package com.example.listochek;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.text.TextRunShaper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.listochek.model.CharacteristicsModel;
import com.example.listochek.model.UserModel;
import com.example.listochek.utils.FirebaseUtil;
import com.example.listochek.utils.NutritionCalculator;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChangePersonalData extends AppCompatActivity {
    TextView name, height, weight, age;
    Button sex, save;
    String userId;
    Boolean activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_personal_data);

        name = findViewById(R.id.nameText);
        height = findViewById(R.id.heightText);
        weight = findViewById(R.id.weightText);
        age = findViewById(R.id.ageText);
        sex = findViewById(R.id.sexButton);
        save = findViewById(R.id.save);

        userId = FirebaseUtil.currentUserId();
        getUserData(userId);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUserData(userId);
            }
        });

        sex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sex.getText().equals("Мужской")) {
                    sex.setText("Женский");
                } else {
                    sex.setText("Мужской");
                }
            }
        });
    }

    private void getUserData(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    UserModel userModel = document.toObject(UserModel.class);
                    if (userModel != null) {
                        name.setText(String.valueOf(userModel.getName()));
                        age.setText(String.valueOf(userModel.getAge()));
                        weight.setText(String.valueOf(userModel.getWeight()));
                        height.setText(String.valueOf(userModel.getHeight()));
                        activity = userModel.getActiveLS();
                        if ((String.valueOf(userModel.getSex()).equals("М"))) {
                            sex.setText("Мужской");
                        } else if ((String.valueOf(userModel.getSex()).equals("Ж"))) {
                            sex.setText("Женский");

                        }

                    }
                }
            } else {
            }
        });

    }

    private void setUserData(String userId) {
        String updatedName = name.getText().toString();
        String updatedAge = age.getText().toString();
        String updatedHeight = height.getText().toString();
        String updatedWeight = weight.getText().toString();
        String updatedSex = sex.getText().toString().equals("Мужской") ? "М" : "Ж";
        Boolean activityType = activity;
        CharacteristicsModel newNutrition = NutritionCalculator.calculateNutrition(updatedSex, Integer.parseInt(updatedAge), Integer.parseInt(updatedHeight), Integer.parseInt(updatedWeight), activityType, userId);



        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    UserModel userModel = document.toObject(UserModel.class);
                    if (userModel != null) {
                        userModel.setName(updatedName);
                        userModel.setAge(Integer.parseInt(updatedAge));
                        userModel.setHeight(Integer.parseInt(updatedHeight));
                        userModel.setWeight(Integer.parseInt(updatedWeight));
                        userModel.setSex(updatedSex);
                        db.collection("users").document(userId).set(userModel)
                                .addOnSuccessListener(aVoid -> {
                                    updateNutritionDetails(newNutrition);
                                    Intent intent = new Intent(ChangePersonalData.this, PrivateAccount.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                })
                                .addOnFailureListener(e -> {
                                });
                    }
                }
            }
        });
    }
    void updateNutritionDetails(CharacteristicsModel nutrition) {
        FirebaseUtil.currentCharacteristicsDetails()
                .set(nutrition)
                .addOnSuccessListener(aVoid -> Toast.makeText(ChangePersonalData.this, "Данные успешно обновлены", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(ChangePersonalData.this, "Ошибка обновления данных", Toast.LENGTH_SHORT).show());
    }
}