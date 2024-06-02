package com.example.listochek;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.listochek.utils.FirebaseUtil;
import com.example.listochek.utils.NutritionCalculator;
import com.example.listochek.utils.NutritionViewModel;
import com.example.listochek.utils.UserPointsViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.messaging.FirebaseMessaging;

public class HomePage extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    ImageButton userButton;
    TextView waterPointsText;
    TextView foodPointsText;

    HomeFragment homeFragment;
    WaterFragment waterFragment;
    CaloriesFragment caloriesFragment;
    String userId;
    UserPointsViewModel userPointsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        homeFragment = new HomeFragment();
        waterFragment = new WaterFragment();
        caloriesFragment = new CaloriesFragment();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        userButton = findViewById(R.id.main_user_btn);
        waterPointsText = findViewById(R.id.waterPointsText);
        foodPointsText = findViewById(R.id.foodPointsText);
        userId = FirebaseUtil.currentUserId();

        userPointsViewModel = new ViewModelProvider(this).get(UserPointsViewModel.class);

        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, PrivateAccount.class);
                startActivity(intent);
            }
        });

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.menu_water) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_frame_layout, new WaterFragment())
                            .commit();
                    return true;
                } else if (item.getItemId() == R.id.menu_home) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, homeFragment).commit();
                    return true;
                } else if (item.getItemId() == R.id.menu_calories) {
                    NutritionViewModel nutritionViewModel = new ViewModelProvider(HomePage.this).get(NutritionViewModel.class);

                    nutritionViewModel.loadCharacteristics(userId);

                    NutritionCalculator.sumDailyIntake(userId, () -> {
                        int totalCalories = NutritionCalculator.getTotalCalories();
                        int totalFats = NutritionCalculator.getTotalFats();
                        int totalProtein = NutritionCalculator.getTotalProtein();
                        int totalCarbohydrates = NutritionCalculator.getTotalCarbohydrates();

                        nutritionViewModel.setCalculatedValues(totalCalories, totalFats, totalProtein, totalCarbohydrates);

                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.main_frame_layout, new CaloriesFragment())
                                .commit();
                    });

                    return true;
                } else {
                    return false;
                }
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.menu_calories);

        userPointsViewModel.getWaterPoints().observe(this, waterPoints -> {
            if (waterPoints != null) {
                waterPointsText.setText(String.valueOf(waterPoints));
            }
        });

        userPointsViewModel.getFoodPoints().observe(this, foodPoints -> {
            if (foodPoints != null) {
                foodPointsText.setText(String.valueOf(foodPoints));
            }
        });

        userPointsViewModel.loadUserPoints(userId);
        getFCMToken();
    }

    void getFCMToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult();
                Log.i("My token", token);
            }
        });
    }
}
