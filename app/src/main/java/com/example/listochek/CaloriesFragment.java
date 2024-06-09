package com.example.listochek;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.listochek.utils.FirebaseUtil;
import com.example.listochek.utils.NutritionViewModel;
import com.example.listochek.utils.UserPointsViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CaloriesFragment extends Fragment {

    private static final String PREFS_NAME = "FoodPrefs";
    private static final String LAST_FOOD_UPDATE_KEY = "lastFoodPointsUpdate";

    public CaloriesFragment() {
    }

    ImageButton breakfast_btn;
    ImageButton lunch_btn;
    ImageButton dinner_btn;
    ImageButton snack_btn;
    UserPointsViewModel userPointsViewModel;
    private ConstraintLayout addBreakfastLayout;
    private ConstraintLayout addLunchLayout;
    private ConstraintLayout addDinnerLayout;
    private ConstraintLayout addSnackLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calories, container, false);

        NutritionViewModel nutritionViewModel = new ViewModelProvider(requireActivity()).get(NutritionViewModel.class);
        userPointsViewModel = new ViewModelProvider(requireActivity()).get(UserPointsViewModel.class);

        nutritionViewModel.getCalories().observe(getViewLifecycleOwner(), calories -> {
            TextView goalView = view.findViewById(R.id.goalView);
            goalView.setText("Ваша цель: " + calories + " калорий");

            nutritionViewModel.getTotalCalories().observe(getViewLifecycleOwner(), totalCalories -> {
                TextView finishedNumberOfCaloriesText = view.findViewById(R.id.finishedNumberOfCaloriesText);
                finishedNumberOfCaloriesText.setText(String.valueOf(totalCalories));

                TextView leftNumberOfCaloriesText = view.findViewById(R.id.leftNumberOfCaloriesText);
                int leftCalories = calories - totalCalories;
                leftNumberOfCaloriesText.setText(String.valueOf(Math.max(leftCalories, 0)));

                if (leftCalories <= 0) {
                    updateFoodPointsIfNotAlreadyUpdated();
                }
            });
        });

        nutritionViewModel.getTotalCarbohydrates().observe(getViewLifecycleOwner(), totalCarbs -> {
            nutritionViewModel.getCarbohydrates().observe(getViewLifecycleOwner(), carbs -> {
                TextView leftCarbohydratesText = view.findViewById(R.id.leftCarbohydratesText);
                leftCarbohydratesText.setText(totalCarbs + "/" + carbs);
            });
        });

        nutritionViewModel.getTotalProtein().observe(getViewLifecycleOwner(), totalProtein -> {
            nutritionViewModel.getProtein().observe(getViewLifecycleOwner(), protein -> {
                TextView leftProteinsText = view.findViewById(R.id.leftProteinsText);
                leftProteinsText.setText(totalProtein + "/" + protein);
            });
        });

        nutritionViewModel.getTotalFats().observe(getViewLifecycleOwner(), totalFats -> {
            nutritionViewModel.getFats().observe(getViewLifecycleOwner(), fats -> {
                TextView leftFatsText = view.findViewById(R.id.leftFatsText);
                leftFatsText.setText(totalFats + "/" + fats);
            });
        });

        breakfast_btn = view.findViewById(R.id.addBreakfastBtn);
        lunch_btn = view.findViewById(R.id.addLunchBtn);
        dinner_btn = view.findViewById(R.id.addDinnerBtn);
        snack_btn = view.findViewById(R.id.addSnackBtn);

        breakfast_btn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SelectABreakfast.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("type", "Breakfast");
            startActivity(intent);
        });

        lunch_btn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SelectABreakfast.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("type", "Lunch");
            startActivity(intent);
        });

        dinner_btn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SelectABreakfast.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("type", "Dinner");
            startActivity(intent);
        });

        snack_btn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SelectABreakfast.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("type", "Snack");
            startActivity(intent);
        });

        addBreakfastLayout = view.findViewById(R.id.addBreakfastLayout);
        addLunchLayout = view.findViewById(R.id.addLunchLayout);
        addDinnerLayout = view.findViewById(R.id.addDinnerLayout);
        addSnackLayout = view.findViewById(R.id.addSnackLayout);

        addBreakfastLayout.setOnClickListener(v -> openMealDetails("Breakfast"));
        addLunchLayout.setOnClickListener(v -> openMealDetails("Lunch"));
        addDinnerLayout.setOnClickListener(v -> openMealDetails("Dinner"));
        addSnackLayout.setOnClickListener(v -> openMealDetails("Snack"));


        return view;
    }
    private void openMealDetails(String mealType) {
        Intent intent = new Intent(getActivity(), MealDetailsActivity.class);
        intent.putExtra("mealType", mealType);
        startActivity(intent);
    }
    private void updateFoodPointsIfNotAlreadyUpdated() {
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String lastUpdateDate = prefs.getString(LAST_FOOD_UPDATE_KEY, "");

        if (!todayDate.equals(lastUpdateDate)) {
            String userId = FirebaseUtil.currentUserId();
            userPointsViewModel.incrementFoodPoints(userId);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(LAST_FOOD_UPDATE_KEY, todayDate);
            editor.apply();
        }
    }
}
