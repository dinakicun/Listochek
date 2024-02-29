package com.example.listochek;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.listochek.utils.NutritionViewModel;


public class CaloriesFragment extends Fragment {

    public CaloriesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calories, container, false);

        NutritionViewModel viewModel = new ViewModelProvider(requireActivity()).get(NutritionViewModel.class);

        viewModel.getCalories().observe(getViewLifecycleOwner(), calories -> {
            TextView goalView = view.findViewById(R.id.goalView);
            goalView.setText("Ваша цель: " + calories + " калорий");
        });

        viewModel.getCarbohydrates().observe(getViewLifecycleOwner(), carbs -> {
            TextView leftCarbohydratesText = view.findViewById(R.id.leftCarbohydratesText);
            leftCarbohydratesText.setText("0/" + carbs );
        });

        viewModel.getProtein().observe(getViewLifecycleOwner(), protein -> {
            TextView leftProteinsText = view.findViewById(R.id.leftProteinsText);
            leftProteinsText.setText("0/" + protein);
        });

        viewModel.getFats().observe(getViewLifecycleOwner(), fats -> {
            TextView leftFatsText = view.findViewById(R.id.leftFatsText);
            leftFatsText.setText("0/" + fats);
        });

        return view;
    }
}