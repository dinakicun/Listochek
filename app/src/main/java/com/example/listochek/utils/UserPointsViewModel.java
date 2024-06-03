package com.example.listochek.utils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;

public class UserPointsViewModel extends ViewModel {
    private MutableLiveData<Integer> waterPoints = new MutableLiveData<>();
    private MutableLiveData<Integer> foodPoints = new MutableLiveData<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void loadUserPoints(String userId) {
        DocumentReference docRef = db.collection("users").document(userId);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Long wp = document.getLong("waterPoints");
                    Long fp = document.getLong("foodPoints");
                    if (wp != null) {
                        waterPoints.setValue(wp.intValue());
                    } else {
                        waterPoints.setValue(1);
                        document.getReference().update("waterPoints", 1);
                    }
                    if (fp != null) {
                        foodPoints.setValue(fp.intValue());
                    } else {
                        foodPoints.setValue(1);
                        document.getReference().update("foodPoints", 1);
                    }
                } else {
                    waterPoints.setValue(1);
                    foodPoints.setValue(1);
                    docRef.update("waterPoints", 1, "foodPoints", 1);
                }
            }
        });
    }

    public void incrementFoodPoints(String userId) {
        DocumentReference userDocRef = db.collection("users").document(userId);
        db.runTransaction((Transaction.Function<Void>) transaction -> {
                    DocumentSnapshot snapshot = transaction.get(userDocRef);
                    Long currentFoodPoints = snapshot.getLong("foodPoints");
                    if (currentFoodPoints != null) {
                        transaction.update(userDocRef, "foodPoints", currentFoodPoints + 1);
                    }
                    return null;
                }).addOnSuccessListener(aVoid -> loadUserPoints(userId))
                .addOnFailureListener(e -> {});
    }

    public void decrementFoodPoints(String userId) {
        DocumentReference userDocRef = db.collection("users").document(userId);
        db.runTransaction((Transaction.Function<Void>) transaction -> {
                    DocumentSnapshot snapshot = transaction.get(userDocRef);
                    Long currentFoodPoints = snapshot.getLong("foodPoints");
                    if (currentFoodPoints != null && currentFoodPoints > 0) {
                        transaction.update(userDocRef, "foodPoints", currentFoodPoints - 1);
                    }
                    return null;
                }).addOnSuccessListener(aVoid -> loadUserPoints(userId))
                .addOnFailureListener(e -> {});
    }

    public void decrementWaterPoints(String userId) {
        DocumentReference userDocRef = db.collection("users").document(userId);
        db.runTransaction((Transaction.Function<Void>) transaction -> {
                    DocumentSnapshot snapshot = transaction.get(userDocRef);
                    Long currentWaterPoints = snapshot.getLong("waterPoints");
                    if (currentWaterPoints != null && currentWaterPoints > 0) {
                        transaction.update(userDocRef, "waterPoints", currentWaterPoints - 1);
                    }
                    return null;
                }).addOnSuccessListener(aVoid -> loadUserPoints(userId))
                .addOnFailureListener(e -> {});
    }

    public LiveData<Integer> getWaterPoints() {
        return waterPoints;
    }

    public LiveData<Integer> getFoodPoints() {
        return foodPoints;
    }
}
