package com.example.listochek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.listochek.model.CharacteristicsModel;
import com.example.listochek.model.UserModel;
import com.example.listochek.utils.FirebaseUtil;
import com.example.listochek.utils.NutritionCalculator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;

import androidx.core.view.GestureDetectorCompat;

public class AuthorizationFinish extends AppCompatActivity {
    UserModel userModel;
    Button notActive;
    Button active;
    Button save;
    boolean active_lifestyle;
    private boolean isLifestyleSelected = false;
    CharacteristicsModel newNutrition;
    private GestureDetectorCompat gestureDetectorCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization_finish);

        notActive = findViewById(R.id.notActiveLS);
        active = findViewById(R.id.activeLS);
        save = findViewById(R.id.saveLifestyle);

        getUserActivity();

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
                    Toast.makeText(AuthorizationFinish.this, "Пожалуйста, выберите образ жизни для продолжения", Toast.LENGTH_LONG).show();
                } else {
                    setUserActivity();
                }
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
                        Intent intent = new Intent(AuthorizationFinish.this, Authorization3.class);
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
    void setUserActivity(){
        String userId = FirebaseUtil.currentUserId();

        if(userModel!=null){
            userModel.setActiveLS(active_lifestyle);

            int age = userModel.getAge();
            int height = userModel.getHeight();
            int weight = userModel.getWeight();
            String sex = userModel.getSex();
            newNutrition = NutritionCalculator.calculateNutrition(sex, age, height, weight, active_lifestyle, userId);
        }
        FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    updateNutritionDetails(newNutrition);
                    Intent intent = new Intent(AuthorizationFinish.this, HomePage.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    SharedPreferences sharedPref = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("user_id", userId);
                    editor.apply();
                    startActivity(intent);

                }
                else{
                    Toast.makeText(AuthorizationFinish.this, "Ошибка авторизации: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    void updateNutritionDetails(CharacteristicsModel nutrition) {
        FirebaseUtil.currentCharacteristicsDetails()
                .set(nutrition)
                .addOnSuccessListener(aVoid -> Toast.makeText(AuthorizationFinish.this, "Nutrition details updated successfully.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(AuthorizationFinish.this, "Error updating nutrition details.", Toast.LENGTH_SHORT).show());
    }
    void getUserActivity() {
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    userModel = task.getResult().toObject(UserModel.class);
                }
            }
        });
    }
    }
