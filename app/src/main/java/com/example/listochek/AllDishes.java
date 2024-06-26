package com.example.listochek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import com.example.listochek.adapter.AdminMealRecyclerAdapter;
import com.example.listochek.model.MealModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class AllDishes extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdminMealRecyclerAdapter adapter;
    private FirebaseFirestore db;
    ImageButton addDishBtn, backButton;
    private static final String TAG = "AllDishes";
    private GestureDetectorCompat gestureDetectorCompat;

    private String userName;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_dishes);

        Intent intent = getIntent();
        userName = intent.getStringExtra("name");
        userEmail = intent.getStringExtra("email");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        addDishBtn = findViewById(R.id.addDishBtn);
        backButton = findViewById(R.id.backBtn);
        addDishBtn.setOnClickListener(v -> {
            Intent intentAdd = new Intent(AllDishes.this, AdminAddDish.class);
            startActivity(intentAdd);
        });

        backButton.setOnClickListener(v -> {
            Intent intentBack = new Intent(AllDishes.this, AdminMainActivity.class);
            intentBack.putExtra("name", userName);
            intentBack.putExtra("email", userEmail);
            startActivity(intentBack);
        });


        db = FirebaseFirestore.getInstance();
        Query query = db.collection("meal").orderBy("name");

        FirestoreRecyclerOptions<MealModel> options = new FirestoreRecyclerOptions.Builder<MealModel>()
                .setQuery(query, MealModel.class)
                .build();

        adapter = new AdminMealRecyclerAdapter(options, this);
        adapter.setOnMealListener(meal -> {
            Intent intentEdit = new Intent(AllDishes.this, EditMealActivity.class);
            intentEdit.putExtra("mealId", meal.getId());
            startActivity(intentEdit);
        });

        recyclerView.setAdapter(adapter);
        gestureDetectorCompat = new GestureDetectorCompat(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float diffX = e2.getX() - e1.getX();
                float diffY = e2.getY() - e1.getY();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (diffX > 0) {
                        Intent intentFling = new Intent(AllDishes.this, AdminMainActivity.class);
                        intentFling.putExtra("name", userName);
                        intentFling.putExtra("email", userEmail);
                        startActivity(intentFling);
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

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
        Log.d(TAG, "Adapter started listening");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
        Log.d(TAG, "Adapter stopped listening");
    }
}
