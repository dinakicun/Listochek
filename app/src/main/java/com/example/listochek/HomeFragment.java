package com.example.listochek;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.listochek.model.PlantModel;
import com.example.listochek.utils.FirebaseUtil;
import com.example.listochek.utils.PlantViewModel;
import com.example.listochek.utils.UserPointsViewModel;

public class HomeFragment extends Fragment {
    private ImageView plantImage;
    private ImageButton hydrateBtn, fertilizeBtn;
    private PlantViewModel plantViewModel;
    private UserPointsViewModel userPointsViewModel;
    private int plantLevel;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        plantImage = view.findViewById(R.id.plantImage);
        hydrateBtn = view.findViewById(R.id.hydrateBtn);
        fertilizeBtn = view.findViewById(R.id.fertilizeBtn);
        plantViewModel = new ViewModelProvider(requireActivity()).get(PlantViewModel.class);
        userPointsViewModel = new ViewModelProvider(requireActivity()).get(UserPointsViewModel.class);

        updatePlantImage(plantLevel);

        plantViewModel.getPlant().observe(getViewLifecycleOwner(), new Observer<PlantModel>() {
            @Override
            public void onChanged(PlantModel plantModel) {
                if (plantModel != null) {
                    plantLevel = plantModel.getPlantLevel();
                    updatePlantImage(plantLevel);
                }
            }
        });

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

        plantImage.setOnClickListener(v -> displayPlantInfo());

        return view;
    }

    public void setPlantLevel(int level) {
        this.plantLevel = level;
        if (plantImage != null) {
            updatePlantImage(level);
        }
    }

    private void updatePlantImage(int level) {
        switch (level) {
            case 0:
                plantImage.setImageResource(R.drawable.plant_level_1);
                break;
            case 1:
                plantImage.setImageResource(R.drawable.plant_level_1);
                break;
            case 2:
                plantImage.setImageResource(R.drawable.plant_level_2);
                break;
            case 3:
                plantImage.setImageResource(R.drawable.plant_level_3);
                break;
            case 4:
                plantImage.setImageResource(R.drawable.plant_level_4);
                break;
            case 5:
                plantImage.setImageResource(R.drawable.plant_level_5);
                break;
            case 6:
                plantImage.setImageResource(R.drawable.plant_level_6);
                break;
            default:
                plantImage.setImageResource(R.drawable.plant_level_6);
                break;
        }
    }

    private void displayPlantInfo() {
        PlantModel plantModel = plantViewModel.getPlant().getValue();
        if (plantModel != null) {
            int waterExperience = plantModel.getWaterExperience();
            int fertilizerExperience = plantModel.getFertilizerExperience();

            int maxExperience = 1000;
            int plantLevel = plantModel.getPlantLevel();

            int effectiveWaterPoints = waterExperience % maxExperience;
            int effectiveFertilizerPoints = fertilizerExperience % maxExperience;


            if (waterExperience >= plantLevel * maxExperience) {
                effectiveWaterPoints = Math.min(maxExperience, waterExperience - (plantLevel * maxExperience));
            }

            if (fertilizerExperience >= plantLevel * maxExperience) {
                effectiveFertilizerPoints = Math.min(maxExperience, fertilizerExperience - (plantLevel * maxExperience));
            }

            effectiveWaterPoints = Math.min(maxExperience, effectiveWaterPoints);
            effectiveFertilizerPoints = Math.min(maxExperience, effectiveFertilizerPoints);

            String message = "Вода: " + effectiveWaterPoints + "/" + maxExperience + "\n" +
                    "Удобрение: " + effectiveFertilizerPoints + "/" + maxExperience;

            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        }
    }





}
