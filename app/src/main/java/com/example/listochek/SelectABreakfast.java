package com.example.listochek;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.listochek.adapter.MealRecyclerAdapter;
import com.example.listochek.model.MealModel;
import com.example.listochek.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SelectABreakfast extends AppCompatActivity {

    RecyclerView meal_rv;
    MealRecyclerAdapter adapter;
    Button saveBtn, systemBtn, userBtn;
    ImageButton addDish;
    TextView typeOfDishText;
    boolean system_view = true;
    List<MealModel> selectedDishes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_a_breakfast);

        String userId = FirebaseUtil.currentUserId();
        String type = getIntent().getExtras().getString("type");
        typeOfDishText = findViewById(R.id.typeOfDishText);

        if (type.equals("Breakfast")) {
            typeOfDishText.setText("Завтрак");
        } else if (type.equals("Dinner")) {
            typeOfDishText.setText("Ужин");
        } else if (type.equals("Lunch")) {
            typeOfDishText.setText("Обед");
        } else if (type.equals("Snack")) {
            typeOfDishText.setText("Перекус");
        }

        meal_rv = findViewById(R.id.meal_recycler_view);
        addDish = findViewById(R.id.addDishBtn);
        systemBtn = findViewById(R.id.systemButton);
        userBtn = findViewById(R.id.usersButton);
        saveBtn = findViewById(R.id.saveBtn);

        setUpButtons();
        setupMealRecyclerView();
        setupSearchBar();
    }

    private void setUpButtons() {
        if (system_view) {
            ((MaterialButton) systemBtn).setStrokeColor(ColorStateList.valueOf(Color.parseColor("#B1E996")));
            ((MaterialButton) userBtn).setStrokeColor(ColorStateList.valueOf(Color.parseColor("#E5E3E1")));
        }

        userBtn.setOnClickListener(v -> {
            system_view = false;
            updateButtonStyles();
            setupMealRecyclerView();
        });

        systemBtn.setOnClickListener(v -> {
            system_view = true;
            updateButtonStyles();
            setupMealRecyclerView();
        });

        addDish.setOnClickListener(v -> {
            Intent intent = new Intent(SelectABreakfast.this, AddDish.class);
            intent.putExtra("returnPage", "SelectABreakfast");
            startActivity(intent);
        });

        saveBtn.setOnClickListener(v -> {
            if (selectedDishes.isEmpty()) {
                Intent intent = new Intent(SelectABreakfast.this, HomePage.class);
                startActivity(intent);
            } else {
                String userId = FirebaseUtil.currentUserId();
                String type = getIntent().getExtras().getString("type");
                saveMeal(userId, type, selectedDishes);
                Intent intent = new Intent(SelectABreakfast.this, HomePage.class);
                startActivity(intent);
            }
        });
    }

    private void updateButtonStyles() {
        if (system_view) {
            ((MaterialButton) systemBtn).setStrokeColor(ColorStateList.valueOf(Color.parseColor("#B1E996")));
            ((MaterialButton) userBtn).setStrokeColor(ColorStateList.valueOf(Color.parseColor("#E5E3E1")));
        } else {
            ((MaterialButton) userBtn).setStrokeColor(ColorStateList.valueOf(Color.parseColor("#B1E996")));
            ((MaterialButton) systemBtn).setStrokeColor(ColorStateList.valueOf(Color.parseColor("#E5E3E1")));
        }
    }

    public void saveMeal(String userId, String mealType, List<MealModel> selectedDishes) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        List<Map<String, Object>> dishesToSave = new ArrayList<>();
        for (MealModel dish : selectedDishes) {
            Map<String, Object> dishMap = new HashMap<>();
            dishMap.put("id", UUID.randomUUID().toString());
            dishMap.put("mealId", dish.getId());
            dishMap.put("factor", dish.getFactor()); // сохраняем factor

            dishesToSave.add(dishMap);
        }

        Map<String, Object> meal = new HashMap<>();
        meal.put(mealType, FieldValue.arrayUnion(dishesToSave.toArray(new Map[0])));

        db.collection("mealConsumption")
                .document(userId)
                .collection("meals")
                .document(new SimpleDateFormat("yyyy-MM-dd").format(new Date()))
                .set(meal, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Meal successfully saved!"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error saving meal", e));
    }

    private void showChangeWeightDialog(MealModel meal) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_change_weight, null);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(dialogView);

        EditText newWeightEditText = dialogView.findViewById(R.id.newWeightEditText);
        newWeightEditText.setText(String.valueOf(meal.getWeight()));
        Button changeWeightButton = dialogView.findViewById(R.id.changeWeightButton);

        AlertDialog alertDialog = dialogBuilder.create();

        changeWeightButton.setOnClickListener(v -> {
            String newWeightStr = newWeightEditText.getText().toString();
            int newWeight;
            if (newWeightStr.isEmpty()) {
                newWeight = meal.getWeight();
            } else {
                newWeight = Integer.parseInt(newWeightStr);
            }
            saveNewMealWithNewWeight(meal, newWeight);
            alertDialog.dismiss();
        });

        alertDialog.show();
    }

    private void saveNewMealWithNewWeight(MealModel meal, int newWeight) {
        double factor = (double) newWeight / meal.getWeight();
        meal.setFactor(factor); // сохраняем factor в объекте MealModel
        meal.setWeight(newWeight);
        meal.setCalories((int) Math.round(meal.getCalories() * factor));
        meal.setProtein((int) Math.round(meal.getProtein() * factor));
        meal.setFats((int) Math.round(meal.getFats() * factor));
        meal.setCarbohydrates((int) Math.round(meal.getCarbohydrates() * factor));

        updateSelectedDishes(meal);
    }

    private void updateSelectedDishes(MealModel meal) {
        LinearLayout selectedDishesLayout = findViewById(R.id.selectedDishesLayout);
        LayoutInflater inflater = LayoutInflater.from(this);
        boolean dishUpdated = false;

        for (int i = 0; i < selectedDishesLayout.getChildCount(); i++) {
            View view = selectedDishesLayout.getChildAt(i);
            TextView nameTextView = view.findViewById(R.id.dish_name);
            if (nameTextView != null && nameTextView.getText().toString().equals(meal.getName())) {
                TextView weightTextView = view.findViewById(R.id.dish_weight);
                TextView caloriesTextView = view.findViewById(R.id.dish_calories);

                weightTextView.setText(meal.getWeight() + " г");
                caloriesTextView.setText(meal.getCalories() + " ккал");

                for (MealModel selectedMeal : selectedDishes) {
                    if (selectedMeal.getName().equals(meal.getName())) {
                        selectedMeal.setWeight(meal.getWeight());
                        selectedMeal.setCalories(meal.getCalories());
                        selectedMeal.setProtein(meal.getProtein());
                        selectedMeal.setFats(meal.getFats());
                        selectedMeal.setCarbohydrates(meal.getCarbohydrates());
                        dishUpdated = true;
                        break;
                    }
                }
                if (dishUpdated) break;
            }
        }

        if (!dishUpdated) {
            ConstraintLayout dishLayout = (ConstraintLayout) inflater.inflate(R.layout.selected_dish_change_layout, selectedDishesLayout, false);

            TextView nameTextView = dishLayout.findViewById(R.id.dish_name);
            TextView weightTextView = dishLayout.findViewById(R.id.dish_weight);
            TextView caloriesTextView = dishLayout.findViewById(R.id.dish_calories);
            ImageButton deleteButton = dishLayout.findViewById(R.id.delete_button);

            nameTextView.setText(meal.getName());
            weightTextView.setText(meal.getWeight() + " г");
            caloriesTextView.setText(meal.getCalories() + " ккал");

            deleteButton.setOnClickListener(v -> {
                selectedDishes.remove(meal);
                selectedDishesLayout.removeView(dishLayout);
            });

            selectedDishes.add(meal);
            selectedDishesLayout.addView(dishLayout);

            dishLayout.setOnClickListener(v -> showChangeWeightDialog(meal));
        }
    }

    private void setupSearchBar() {
        EditText searchBar = findViewById(R.id.searchBarEditText);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterMeals(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void filterMeals(String text) {
//        Query query;
//        if (system_view) {
//            query = FirebaseUtil.allMealsCollectionReference()
//                    .whereGreaterThanOrEqualTo("nameToLower", text)
//                    .whereLessThanOrEqualTo("nameToLower", text + '\uf8ff');
//        } else {
//            query = FirebaseUtil.userMealsCollectionReference()
//                    .whereGreaterThanOrEqualTo("nameToLower", text)
//                    .whereLessThanOrEqualTo("nameToLower", text + '\uf8ff');
//        }
//
//        FirestoreRecyclerOptions<MealModel> options = new FirestoreRecyclerOptions.Builder<MealModel>()
//                .setQuery(query, MealModel.class).build();
//
//        if (adapter != null) {
//            adapter.updateOptions(options);
//        }
    }

    void setupMealRecyclerView() {
        Query query;
        if (system_view) {
            query = FirebaseUtil.allMealsCollectionReference();
        } else {
            query = FirebaseUtil.userMealsCollectionReference();
        }

        FirestoreRecyclerOptions<MealModel> options = new FirestoreRecyclerOptions.Builder<MealModel>()
                .setQuery(query, MealModel.class).build();

        if (adapter != null) {
            adapter.stopListening();
        }

        adapter = new MealRecyclerAdapter(options, getApplicationContext());
        meal_rv.setLayoutManager(new LinearLayoutManager(this));
        meal_rv.setAdapter(adapter);
        adapter.startListening();
        adapter.setOnMealListener(meal -> updateSelectedDishes(meal));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.startListening();
        }
    }
}
