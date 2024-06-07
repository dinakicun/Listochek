package com.example.listochek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.listochek.model.MealModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditMealActivity extends AppCompatActivity {

    private EditText editTextName, editTextWeight, editTextCalories, editTextProtein, editTextFats, editTextCarbs;
    private Button buttonSave;
    private FirebaseFirestore db;
    private String mealId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_meal);

        editTextName = findViewById(R.id.editTextName);
        editTextWeight = findViewById(R.id.editTextWeight);
        editTextCalories = findViewById(R.id.editTextCalories);
        editTextProtein = findViewById(R.id.editTextProtein);
        editTextFats = findViewById(R.id.editTextFats);
        editTextCarbs = findViewById(R.id.editTextCarbs);
        buttonSave = findViewById(R.id.buttonSave);

        mealId = getIntent().getStringExtra("mealId");
        db = FirebaseFirestore.getInstance();

        loadMealData(mealId);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMealData();
            }
        });
    }

    private void loadMealData(String mealId) {
        DocumentReference docRef = db.collection("meal").document(mealId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        MealModel meal = document.toObject(MealModel.class);
                        if (meal != null) {
                            editTextName.setText(meal.getName());
                            editTextWeight.setText(String.valueOf(meal.getWeight()));
                            editTextCalories.setText(String.valueOf(meal.getCalories()));
                            editTextProtein.setText(String.valueOf(meal.getProtein()));
                            editTextFats.setText(String.valueOf(meal.getFats()));
                            editTextCarbs.setText(String.valueOf(meal.getCarbohydrates()));
                        }
                    } else {
                        Toast.makeText(EditMealActivity.this, "Failed to load meal data", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EditMealActivity.this, "Failed to load meal data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveMealData() {
        String name = editTextName.getText().toString().trim();
        String weightStr = editTextWeight.getText().toString().trim();
        String caloriesStr = editTextCalories.getText().toString().trim();
        String proteinStr = editTextProtein.getText().toString().trim();
        String fatsStr = editTextFats.getText().toString().trim();
        String carbsStr = editTextCarbs.getText().toString().trim();

        if (name.isEmpty() || weightStr.isEmpty() || caloriesStr.isEmpty() || proteinStr.isEmpty() || fatsStr.isEmpty() || carbsStr.isEmpty()) {
            Toast.makeText(this, "Все поля должны быть заполнены", Toast.LENGTH_SHORT).show();
            return;
        }

        Integer weight, calories, protein, fats, carbs;
        try {
            weight = Integer.parseInt(weightStr);
            calories = Integer.parseInt(caloriesStr);
            protein = Integer.parseInt(proteinStr);
            fats = Integer.parseInt(fatsStr);
            carbs = Integer.parseInt(carbsStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Некорректные данные. Проверьте введенные значения.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (weight <= 0 || calories <= 0 || protein < 0 || fats < 0 || carbs < 0) {
            Toast.makeText(this, "Введенные значения должны быть положительными", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference docRef = db.collection("meal").document(mealId);
        docRef.update("name", name, "weight", weight, "calories", calories, "protein", protein, "fats", fats, "carbohydrates", carbs)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(EditMealActivity.this, "Блюдо успешно обновлено", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(EditMealActivity.this, AllDishes.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(EditMealActivity.this, "Ошибка при обновлении блюда", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
