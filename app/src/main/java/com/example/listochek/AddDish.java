package com.example.listochek;

import static java.lang.Integer.parseInt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.listochek.model.MealModel;
import com.example.listochek.utils.FirebaseUtil;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddDish extends AppCompatActivity {
    private EditText nameText, weightText, caloriesText, proteinText, fatsText, carbohydratesText;
    private FirebaseFirestore db;
    private String returnPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dish);

        db = FirebaseFirestore.getInstance();

        nameText = findViewById(R.id.nameText);
        weightText = findViewById(R.id.weightText);
        caloriesText = findViewById(R.id.caloriesText);
        proteinText = findViewById(R.id.proteinText);
        fatsText = findViewById(R.id.fatsText);
        carbohydratesText = findViewById(R.id.carbohydratesText);

        returnPage = getIntent().getStringExtra("returnPage");

        findViewById(R.id.save).setOnClickListener(v -> saveDish());
    }

    private void saveDish() {
        String name = nameText.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Введите название блюда", Toast.LENGTH_SHORT).show();
            return;
        }

        String weightStr = weightText.getText().toString().trim();
        String caloriesStr = caloriesText.getText().toString().trim();
        String proteinStr = proteinText.getText().toString().trim();
        String fatsStr = fatsText.getText().toString().trim();
        String carbohydratesStr = carbohydratesText.getText().toString().trim();

        if (weightStr.isEmpty() || caloriesStr.isEmpty() || proteinStr.isEmpty() || fatsStr.isEmpty() || carbohydratesStr.isEmpty()) {
            Toast.makeText(this, "Все поля должны быть заполнены", Toast.LENGTH_SHORT).show();
            return;
        }

        int weight, calories, protein, fats, carbohydrates;

        try {
            weight = Integer.parseInt(weightStr);
            calories = Integer.parseInt(caloriesStr);
            protein = Integer.parseInt(proteinStr);
            fats = Integer.parseInt(fatsStr);
            carbohydrates = Integer.parseInt(carbohydratesStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Некорректные данные. Проверьте введенные значения.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = FirebaseUtil.currentUserId();
        String documentId = db.collection("meal").document("usersMeal").collection(userId).document().getId();

        MealModel meal = new MealModel(calories, fats, protein, carbohydrates, name, weight, name.toLowerCase(), documentId);
        db.collection("meal").document("usersMeal").collection(userId).document(documentId).set(meal)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Блюдо успешно добавлено!", Toast.LENGTH_SHORT).show();
                    Intent intent;
                    if ("SelectABreakfast".equals(returnPage)) {
                        intent = new Intent(AddDish.this, SelectABreakfast.class);
                    } else {
                        intent = new Intent(AddDish.this, UsersDishes.class);
                    }
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Ошибка при добавлении блюда: " + e.toString(), Toast.LENGTH_SHORT).show();
                });
    }
}
