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
import com.example.listochek.utils.PlantViewModel;
import com.example.listochek.utils.UserPointsViewModel;
import com.example.listochek.utils.CombinedWaterViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.messaging.FirebaseMessaging;

public class HomePage extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    ImageButton userButton;
    TextView waterPointsText;
    TextView foodPointsText;
    TextView levelText;
    HomeFragment homeFragment;
    WaterFragment waterFragment;
    CaloriesFragment caloriesFragment;
    String userId;
    UserPointsViewModel userPointsViewModel;
    PlantViewModel plantViewModel;
    CombinedWaterViewModel combinedWaterViewModel;

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
        levelText = findViewById(R.id.levelText);
        userId = FirebaseUtil.currentUserId();

        userPointsViewModel = new ViewModelProvider(this).get(UserPointsViewModel.class);
        plantViewModel = new ViewModelProvider(this).get(PlantViewModel.class);
        combinedWaterViewModel = new ViewModelProvider(this).get(CombinedWaterViewModel.class);

        userButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomePage.this, PrivateAccount.class);
            startActivity(intent);
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.menu_water) {
                combinedWaterViewModel.loadData(userId).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("HomePage", "Data for WaterFragment loaded successfully");
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.main_frame_layout, waterFragment)
                                .commit();
                    } else {
                        Log.e("HomePage", "Failed to load data for WaterFragment", task.getException());
                    }
                });
                return true;
            } else if (item.getItemId() == R.id.menu_home) {
                plantViewModel.getPlant().observe(HomePage.this, plantModel -> {
                    homeFragment.setPlantLevel(plantModel.getPlantLevel());
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_frame_layout, homeFragment)
                            .commit();
                });
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
                            .replace(R.id.main_frame_layout, caloriesFragment)
                            .commit();
                });

                return true;
            } else {
                return false;
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.menu_home);

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

        plantViewModel.getPlant().observe(this, plantModel -> {
            if (plantModel != null) {
                levelText.setText(String.valueOf(plantModel.getPlantLevel()));
            }
        });

        userPointsViewModel.loadUserPoints(userId);
        getFCMToken();

        // Обновление состояния растения при входе в приложение
        plantViewModel.loadPlantData(userId).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                plantViewModel.updatePlantState();
            }
        });
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
