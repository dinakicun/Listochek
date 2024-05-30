package com.example.listochek;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.listochek.utils.NutritionViewModel;

public class CaloriesFragment extends Fragment {

    public CaloriesFragment() {
    }

    ImageButton breakfast_btn;
    ImageButton lunch_btn;
    ImageButton dinner_btn;
    ImageButton snack_btn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calories, container, false);

        NutritionViewModel viewModel = new ViewModelProvider(requireActivity()).get(NutritionViewModel.class);

        viewModel.getCalories().observe(getViewLifecycleOwner(), calories -> {
            TextView goalView = view.findViewById(R.id.goalView);
            goalView.setText("Ваша цель: " + calories + " калорий");

            viewModel.getTotalCalories().observe(getViewLifecycleOwner(), totalCalories -> {
                TextView finishedNumberOfCaloriesText = view.findViewById(R.id.finishedNumberOfCaloriesText);
                finishedNumberOfCaloriesText.setText(String.valueOf(totalCalories));

                TextView leftNumberOfCaloriesText = view.findViewById(R.id.leftNumberOfCaloriesText);
                int leftCalories = calories - totalCalories;
                leftNumberOfCaloriesText.setText(String.valueOf(Math.max(leftCalories, 0)));
            });
        });

        viewModel.getTotalCarbohydrates().observe(getViewLifecycleOwner(), totalCarbs -> {
            viewModel.getCarbohydrates().observe(getViewLifecycleOwner(), carbs -> {
                TextView leftCarbohydratesText = view.findViewById(R.id.leftCarbohydratesText);
                leftCarbohydratesText.setText(totalCarbs + "/" + carbs);
            });
        });

        viewModel.getTotalProtein().observe(getViewLifecycleOwner(), totalProtein -> {
            viewModel.getProtein().observe(getViewLifecycleOwner(), protein -> {
                TextView leftProteinsText = view.findViewById(R.id.leftProteinsText);
                leftProteinsText.setText(totalProtein + "/" + protein);
            });
        });

        viewModel.getTotalFats().observe(getViewLifecycleOwner(), totalFats -> {
            viewModel.getFats().observe(getViewLifecycleOwner(), fats -> {
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

        return view;
    }
}
