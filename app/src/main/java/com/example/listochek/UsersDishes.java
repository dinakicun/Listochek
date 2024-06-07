package com.example.listochek;

import static com.example.listochek.utils.FirebaseUtil.currentUserId;

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

import com.example.listochek.adapter.UserMealRecyclerAdapter;
import com.example.listochek.model.MealModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class UsersDishes extends AppCompatActivity {
    private RecyclerView recyclerView;
    private UserMealRecyclerAdapter adapter;
    private FirebaseFirestore db;
    ImageButton addDishBtn, backButton;
    private static final String TAG = "UsersDishes";
    private GestureDetectorCompat gestureDetectorCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_dishes);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        addDishBtn = findViewById(R.id.addDishBtn);
        backButton = findViewById(R.id.backBtn);
        addDishBtn.setOnClickListener(v -> {
            Intent intent = new Intent(UsersDishes.this, AddDish.class);
            intent.putExtra("returnPage", "UsersDishes");
            startActivity(intent);
        });

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(UsersDishes.this, PrivateAccount.class);
            startActivity(intent);
        });

        db = FirebaseFirestore.getInstance();
        Query query = db.collection("meal").document("usersMeal").collection(currentUserId()).orderBy("name");

        FirestoreRecyclerOptions<MealModel> options = new FirestoreRecyclerOptions.Builder<MealModel>()
                .setQuery(query, MealModel.class)
                .build();

        adapter = new UserMealRecyclerAdapter(options, this);
        adapter.setOnMealListener(meal -> {
            Intent intent = new Intent(UsersDishes.this, EditUsersDish.class);
            intent.putExtra("mealId", meal.getId());
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
        gestureDetectorCompat = new GestureDetectorCompat(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float diffX = e2.getX() - e1.getX();
                float diffY = e2.getY() - e1.getY();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (diffX > 0) {
                        Intent intent = new Intent(UsersDishes.this, PrivateAccount.class);
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
