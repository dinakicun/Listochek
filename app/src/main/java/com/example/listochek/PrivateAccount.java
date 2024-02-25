package com.example.listochek;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.listochek.model.CharacteristicsModel;
import com.example.listochek.model.UserModel;
import com.example.listochek.utils.FirebaseUtil;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class PrivateAccount extends AppCompatActivity {
    private TextView nameText, ageText, weightText, heightText, caloriesText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_account);

        nameText = findViewById(R.id.nameText);
        ageText = findViewById(R.id.ageText);
        weightText = findViewById(R.id.weightText);
        heightText = findViewById(R.id.heightText);
        caloriesText = findViewById(R.id.caloriesText);
        loadUserData();
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