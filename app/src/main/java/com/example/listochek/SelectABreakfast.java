package com.example.listochek;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.listochek.adapter.MealRecyclerAdapter;
import com.example.listochek.model.MealModel;
import com.example.listochek.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.Query;

import androidx.core.view.GestureDetectorCompat;


import android.text.Editable;
import android.text.TextWatcher;

public class SelectABreakfast extends AppCompatActivity {

    RecyclerView meal_rv;
    MealRecyclerAdapter adapter;
    Button saveBtn, systemBtn, userBtn;
    ImageButton addDish;
    private GestureDetectorCompat gestureDetectorCompat;
    boolean system_view = true;
    private boolean isViewButtonSelected = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_a_breakfast);

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
                Intent intent = new Intent(SelectABreakfast.this, HomePage.class);
                startActivity(intent);
            }
        });
        setupMealRecyclerView();
        setupSearchBar();
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