package com.example.listochek.utils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.listochek.model.PlantModel;
import com.example.listochek.utils.FirebaseUtil;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class PlantViewModel extends ViewModel {
    private MutableLiveData<PlantModel> plant = new MutableLiveData<>();
    private Timer timer = new Timer();
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userId = FirebaseUtil.currentUserId();

    public PlantViewModel() {
        loadPlantData(userId);
        startTimer();
    }

    public LiveData<PlantModel> getPlant() {
        return plant;
    }

    private void loadPlantData(String userId) {
        DocumentReference plantDocRef = db.collection("plants").document(userId);
        plantDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    PlantModel plantModel = document.toObject(PlantModel.class);
                    plant.setValue(plantModel);
                } else {
                    PlantModel newPlantModel = new PlantModel();
                    plant.setValue(newPlantModel);
                    plantDocRef.set(newPlantModel);
                }
            } else {
                Log.e("PlantViewModel", "Failed to load plant data", task.getException());
            }
        });
    }

    public void waterPlant() {
        PlantModel plantModel = plant.getValue();
        if (plantModel != null) {
            Log.d("PlantViewModel", "Watering plant");
            plantModel.setLastWatered(new Date());
            plantModel.setWaterExperience(plantModel.getWaterExperience() + 100);
            plant.setValue(plantModel);
            savePlantData(plantModel);
        }
    }

    public void fertilizePlant() {
        PlantModel plantModel = plant.getValue();
        if (plantModel != null) {
            Log.d("PlantViewModel", "Fertilizing plant");
            plantModel.setLastFertilized(new Date());
            plantModel.setFertilizerExperience(plantModel.getFertilizerExperience() + 100);
            plant.setValue(plantModel);
            savePlantData(plantModel);
        }
    }

    private void savePlantData(PlantModel plantModel) {
        db.collection("plants").document(userId).set(plantModel)
                .addOnSuccessListener(aVoid -> Log.d("PlantViewModel", "Plant data saved successfully"))
                .addOnFailureListener(e -> Log.e("PlantViewModel", "Failed to save plant data", e));
    }

    private void startTimer() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updatePlantState();
            }
        }, 0, 86400000); // Обновление состояния каждые 24 часа
    }

    private void updatePlantState() {
        PlantModel plantModel = plant.getValue();
        if (plantModel != null) {
            long currentTime = new Date().getTime();
            long lastWateredTime = plantModel.getLastWatered().getTime();
            long lastFertilizedTime = plantModel.getLastFertilized().getTime();

            // Проверка времени с последнего полива
            if (currentTime - lastWateredTime > 172800000) { // Более 48 часов
                plantModel.setWaterLevel(plantModel.getWaterLevel() - 1);
                plantModel.setWaterExperience(plantModel.getWaterExperience() - 200);
            }

            // Проверка времени с последнего удобрения
            if (currentTime - lastFertilizedTime > 172800000) { // Более 48 часов
                plantModel.setFertilizerLevel(plantModel.getFertilizerLevel() - 1);
                plantModel.setFertilizerExperience(plantModel.getFertilizerExperience() - 200);
            }

            // Обновляем LiveData в главном потоке
            mainHandler.post(() -> plant.setValue(plantModel));
            savePlantData(plantModel);
        }
    }
}
