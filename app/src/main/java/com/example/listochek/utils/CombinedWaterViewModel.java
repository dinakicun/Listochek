package com.example.listochek.utils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
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

    public Task<Void> loadData(String userId) {
        return Tasks.whenAll(loadWaterGoal(userId), loadDailyWaterIntake(userId));
    }

    public Task<Void> loadWaterGoal(String userId) {
        DocumentReference docRef = db.collection("characteristics").document(userId);
        return docRef.get().continueWith(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Double goal = document.getDouble("water");
                    if (goal != null) {
                        waterGoal.postValue(goal / 1000);
                        updateRemainingWaterGoal();
                    }
                }
            }
            return null;
        });
    }

    public Task<Void> loadDailyWaterIntake(String userId) {
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        DocumentReference docRef = db.collection("waterIntake").document(userId).collection("Intakes").document(todayDate);
        return docRef.get().continueWith(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot documentSnapshot = task.getResult();
                Double volume = documentSnapshot.getDouble("volume");
                if (volume != null) {
                    currentVolume.postValue(volume);
                    glassesOfWater.postValue((int) (volume / 0.25));
                    updateRemainingWaterGoal();
                } else {
                    currentVolume.postValue(0.0);
                    glassesOfWater.postValue(0);
                    updateRemainingWaterGoal();
                }
            } else {
                currentVolume.postValue(0.0);
                glassesOfWater.postValue(0);
                updateRemainingWaterGoal();
            }
            return null;
        });
    }

    public void updateWaterIntakeData(String userId) {
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        DocumentReference docRef = db.collection("waterIntake").document(userId).collection("Intakes").document(todayDate);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Double volume = documentSnapshot.getDouble("volume");
                if (volume != null) {
                    currentVolume.postValue(volume);
                    glassesOfWater.postValue((int) (volume / 0.25));
                    updateRemainingWaterGoal();
                } else {
                    currentVolume.postValue(0.0);
                    glassesOfWater.postValue(0);
                    updateRemainingWaterGoal();
                }
            } else {
                currentVolume.postValue(0.0);
                glassesOfWater.postValue(0);
                updateRemainingWaterGoal();
            }
        }).addOnFailureListener(e -> {
            currentVolume.postValue(0.0);
            glassesOfWater.postValue(0);
            updateRemainingWaterGoal();
        });
    }

    public void updateRemainingWaterGoal() {
        Double goal = waterGoal.getValue();
        Double volume = currentVolume.getValue();
        if (goal != null && volume != null) {
            remainingWaterGoal.setValue(Math.max(0, goal - volume));
        }
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
