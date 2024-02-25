package com.example.listochek;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class WaterViewModel extends ViewModel {
    private MutableLiveData<Double> waterGoal = new MutableLiveData<>();

    public void loadWaterGoal(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("characteristics").document(userId);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Double waterGoalLiters = document.getDouble("water") / 1000;
                    waterGoal.setValue(waterGoalLiters);
                }
            }
        });
    }

    public LiveData<Double> getWaterGoal() {
        return waterGoal;
    }
}

