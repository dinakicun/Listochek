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

        breakfast_btn = view.findViewById(R.id.addBreakfastBtn);
        lunch_btn = view.findViewById(R.id.addLunchBtn);
        dinner_btn = view.findViewById(R.id.addDinnerBtn);
        snack_btn = view.findViewById(R.id.addSnackBtn);
        breakfast_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SelectABreakfast.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        lunch_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SelectALunch.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        dinner_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SelectADinner.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        snack_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SelectASnack.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        return view;
    }
}