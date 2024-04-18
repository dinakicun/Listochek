package com.example.listochek.utils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CombinedWaterViewModel extends ViewModel {
    private MutableLiveData<Double> waterGoal = new MutableLiveData<>();
    private MutableLiveData<Double> currentVolume = new MutableLiveData<>();
    private MutableLiveData<Integer> glassesOfWater = new MutableLiveData<>();
    private MutableLiveData<Double> remainingWaterGoal = new MutableLiveData<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void loadData(String userId) {
        loadWaterGoal(userId);
        loadDailyWaterIntake(userId);
    }

    public void loadWaterGoal(String userId) {
        DocumentReference docRef = db.collection("characteristics").document(userId);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Double goal = document.getDouble("water");
                    if (goal != null) {
                        waterGoal.setValue(goal / 1000);
                    }
                }
            }
        });
    }

    public void loadDailyWaterIntake(String userId) {
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        db.collection("waterIntake").document(userId).collection("Intakes").document(todayDate)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Double volume = documentSnapshot.getDouble("volume");
                        if (volume != null) {
                            currentVolume.setValue(volume);
                            glassesOfWater.setValue((int) (volume / 0.25));
                            if (waterGoal.getValue() != null) {
                                double goal = waterGoal.getValue();
                                remainingWaterGoal.setValue(Math.max(0, goal - volume));
                            }
                        } else {
                            currentVolume.setValue(0.0);
                            glassesOfWater.setValue(0);
                            remainingWaterGoal.setValue(waterGoal.getValue());
                        }
                    } else {
                        currentVolume.setValue(0.0);
                        glassesOfWater.setValue(0);
                        remainingWaterGoal.setValue(waterGoal.getValue());
                    }
                })
                .addOnFailureListener(e -> {
                    currentVolume.setValue(0.0);
                    glassesOfWater.setValue(0);
                    remainingWaterGoal.setValue(waterGoal.getValue());
                });
    }

    public void updateWaterIntakeData(String userId)
    {
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        db.collection("waterIntake").document(userId).collection("Intakes").document(todayDate)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Double volume = documentSnapshot.getDouble("volume");
                        if (volume != null) {
                            currentVolume.setValue(volume);
                            if (waterGoal.getValue() != null) {
                                double goal = waterGoal.getValue();
                                remainingWaterGoal.setValue(Math.max(0, goal - volume));
                            }
                        } else {
                            currentVolume.setValue(0.0);
                            remainingWaterGoal.setValue(waterGoal.getValue());
                        }
                    } else {
                        currentVolume.setValue(0.0);
                        remainingWaterGoal.setValue(waterGoal.getValue());
                    }
                })
                .addOnFailureListener(e -> {
                    currentVolume.setValue(0.0);
                    remainingWaterGoal.setValue(waterGoal.getValue());
                });
    }

    public LiveData<Double> getWaterGoal() {
        return waterGoal;
    }

    public LiveData<Double> getCurrentVolume() {
        return currentVolume;
    }

    public LiveData<Integer> getGlassesOfWater() {
        return glassesOfWater;
    }

    public LiveData<Double> getRemainingWaterGoal() {
        return remainingWaterGoal;
    }
}
