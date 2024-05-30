package com.example.listochek;

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

import androidx.core.view.GestureDetectorCompat;


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
    private GestureDetectorCompat gestureDetectorCompat;
    boolean system_view = true;
    private boolean isViewButtonSelected = true;
    List<MealModel> selectedDishes = new ArrayList<>();
    //поменять название и текст "Завтрак"
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
        if (system_view == true){
            ((MaterialButton) systemBtn).setStrokeColor(ColorStateList.valueOf(Color.parseColor("#B1E996")));
            ((MaterialButton) userBtn).setStrokeColor(ColorStateList.valueOf(Color.parseColor("#E5E3E1")));
        }
        userBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                system_view = false;
                ((MaterialButton) userBtn).setStrokeColor(ColorStateList.valueOf(Color.parseColor("#B1E996")));
                ((MaterialButton) systemBtn).setStrokeColor(ColorStateList.valueOf(Color.parseColor("#E5E3E1")));
                isViewButtonSelected = true;
                setupMealRecyclerView();
                adapter.setOnMealListener(new MealRecyclerAdapter.OnMealListener() {
                    @Override
                    public void onMealClick(MealModel meal) {
                        updateSelectedDishes(meal);
                    }
                });
            }
        });

        systemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                system_view = true;
                ((MaterialButton) systemBtn).setStrokeColor(ColorStateList.valueOf(Color.parseColor("#B1E996")));
                ((MaterialButton) userBtn).setStrokeColor(ColorStateList.valueOf(Color.parseColor("#E5E3E1")));
                isViewButtonSelected = true;
                setupMealRecyclerView();
                adapter.setOnMealListener(new MealRecyclerAdapter.OnMealListener() {
                    @Override
                    public void onMealClick(MealModel meal) {
                        updateSelectedDishes(meal);
                    }
                });
            }
        });
        addDish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectABreakfast.this, AddDish.class);
                startActivity(intent);
            }
        });
        saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMeal(userId, type, selectedDishes);
                Intent intent = new Intent(SelectABreakfast.this, HomePage.class);
                startActivity(intent);
            }
        });
        setupMealRecyclerView();
        setupSearchBar();
        adapter.setOnMealListener(new MealRecyclerAdapter.OnMealListener() {
            @Override
            public void onMealClick(MealModel meal) {
                updateSelectedDishes(meal);
            }
        });
    }

    public void saveMeal(String userId, String mealType, List<MealModel> selectedDishes) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        List<Map<String, Object>> dishesToSave = new ArrayList<>();
        for (MealModel dish : selectedDishes) {
            Map<String, Object> dishMap = new HashMap<>();
            dishMap.put("id", UUID.randomUUID().toString()); // Добавляем уникальный идентификатор
            dishMap.put("mealId", dish.getId()); // Добавляем идентификатор блюда

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

    private void updateSelectedDishes(MealModel meal) {
        LinearLayout selectedDishesLayout = findViewById(R.id.selectedDishesLayout);
        LayoutInflater inflater = LayoutInflater.from(this);
        ConstraintLayout dishLayout = (ConstraintLayout) inflater.inflate(R.layout.selected_dish_layout, selectedDishesLayout, false);

        TextView nameTextView = dishLayout.findViewById(R.id.dish_name);
        TextView weightTextView = dishLayout.findViewById(R.id.dish_weight);
        TextView caloriesTextView = dishLayout.findViewById(R.id.dish_calories);
        ImageButton deleteButton = dishLayout.findViewById(R.id.delete_button);

        nameTextView.setText(meal.getName());
        weightTextView.setText(meal.getWeight() + " г");
        caloriesTextView.setText(meal.getCalories() + " ккал");

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectedDishes.remove(meal);
                selectedDishesLayout.removeView(dishLayout);
            }
        });

        selectedDishes.add(meal);
        selectedDishesLayout.addView(dishLayout);
    }

    private void setupSearchBar() {
        EditText searchBar = findViewById(R.id.searchBarEditText);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterMeals(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    // Функция поиска блюд по полю с названием блюда маленького регистра
    private void filterMeals(String text) {
//        Query query;
//        if (text.isEmpty()) {
//            query = FirebaseUtil.allMealsCollectionReference();
//        } else {
//            String searchText = text.toLowerCase();  // Приведение текста к нижнему регистру
//            query = FirebaseUtil.allMealsCollectionReference()
//                    .orderBy("nameToLower")
//                    .startAt(searchText)
//                    .endAt(searchText + "\uf8ff");
//        }
//        FirestoreRecyclerOptions<MealModel> options = new FirestoreRecyclerOptions.Builder<MealModel>()
//                .setQuery(query, MealModel.class).build();
//
//        if (adapter != null) {
//            adapter.stopListening();
//        }
//        adapter = new MealRecyclerAdapter(options, getApplicationContext());
//        meal_rv.setAdapter(adapter);
//        adapter.startListening();
    }



    void setupMealRecyclerView(){
        if (system_view){
            Query query = FirebaseUtil.allMealsCollectionReference();

            FirestoreRecyclerOptions<MealModel> options = new FirestoreRecyclerOptions.Builder<MealModel>()
                    .setQuery(query, MealModel.class).build();

            adapter = new MealRecyclerAdapter(options, getApplicationContext());
            meal_rv.setLayoutManager(new LinearLayoutManager(this));
            meal_rv.setAdapter(adapter);
            adapter.startListening();
        }
        else {

            Query query = FirebaseUtil.userMealsCollectionReference();

            FirestoreRecyclerOptions<MealModel> options = new FirestoreRecyclerOptions.Builder<MealModel>()
                    .setQuery(query, MealModel.class).build();

            adapter = new MealRecyclerAdapter(options, getApplicationContext());
            meal_rv.setLayoutManager(new LinearLayoutManager(this));
            meal_rv.setAdapter(adapter);
            adapter.startListening();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(adapter != null){
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(adapter != null){
            adapter.stopListening();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter != null){
            adapter.startListening();
        }
    }
}
