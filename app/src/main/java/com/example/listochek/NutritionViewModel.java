package com.example.listochek;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class NutritionViewModel extends ViewModel {
    private MutableLiveData<Integer> calories = new MutableLiveData<>();
    private MutableLiveData<Integer> fats = new MutableLiveData<>();
    private MutableLiveData<Integer> protein = new MutableLiveData<>();
    private MutableLiveData<Integer> carbohydrates = new MutableLiveData<>();

    public void loadCharacteristics(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("characteristics").document(userId);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    calories.setValue(document.getLong("calories").intValue());
                    fats.setValue(document.getLong("fats").intValue());
                    protein.setValue(document.getLong("protein").intValue());
                    carbohydrates.setValue(document.getLong("carbohydrates").intValue());
                }
            }
        });
    }

    public LiveData<Integer> getCalories() {
        return calories;
    }

    public LiveData<Integer> getFats() {
        return fats;
    }

    public LiveData<Integer> getProtein() {
        return protein;
    }

    public LiveData<Integer> getCarbohydrates() {
        return carbohydrates;
    }
}

