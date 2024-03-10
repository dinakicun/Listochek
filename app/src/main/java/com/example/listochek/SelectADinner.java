package com.example.listochek;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.listochek.adapter.MealRecyclerAdapter;
import com.example.listochek.model.MealModel;
import com.example.listochek.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class SelectADinner extends AppCompatActivity {
    RecyclerView meal_rv;
    MealRecyclerAdapter adapter;

    ImageButton addDish;
    Button saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_a_dinner);
        meal_rv = findViewById(R.id.meal_recycler_view);
        addDish = findViewById(R.id.addDishBtn);

        addDish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectADinner.this, AddDish.class);
                startActivity(intent);
            }
        });
        saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectADinner.this, CaloriesFragment.class);
                startActivity(intent);
            }
        });
        setupMealRecyclerView();
    }
    void setupMealRecyclerView(){

        Query query = FirebaseUtil.allMealsCollectionReference();

        FirestoreRecyclerOptions<MealModel> options = new FirestoreRecyclerOptions.Builder<MealModel>()
                .setQuery(query, MealModel.class).build();

        adapter = new MealRecyclerAdapter(options, getApplicationContext());
        meal_rv.setLayoutManager(new LinearLayoutManager(this));
        meal_rv.setAdapter(adapter);
        adapter.startListening();
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