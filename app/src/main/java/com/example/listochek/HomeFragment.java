package com.example.listochek;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.listochek.utils.FirebaseUtil;
import com.example.listochek.utils.UserPointsViewModel;

public class HomeFragment extends Fragment {
    private ImageView plantImage;
    private ImageButton hydrateBtn, fertilizeBtn;
    private com.example.listochek.utils.PlantViewModel plantViewModel;
    private UserPointsViewModel userPointsViewModel;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        plantImage = view.findViewById(R.id.plantImage);
        hydrateBtn = view.findViewById(R.id.hydrateBtn);
        fertilizeBtn = view.findViewById(R.id.fertilizeBtn);
        plantViewModel = new ViewModelProvider(requireActivity()).get(com.example.listochek.utils.PlantViewModel.class);
        userPointsViewModel = new ViewModelProvider(requireActivity()).get(UserPointsViewModel.class);

        hydrateBtn.setOnClickListener(v -> {
            if (userPointsViewModel.getWaterPoints().getValue() > 0) {
                plantViewModel.waterPlant();
                userPointsViewModel.decrementWaterPoints(FirebaseUtil.currentUserId());
            }
        });

        fertilizeBtn.setOnClickListener(v -> {
            if (userPointsViewModel.getFoodPoints().getValue() > 0) {
                plantViewModel.fertilizePlant();
                userPointsViewModel.decrementFoodPoints(FirebaseUtil.currentUserId());
            }
        });

        return view;
    }
}
