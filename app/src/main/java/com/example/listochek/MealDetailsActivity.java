package com.example.listochek;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.listochek.adapter.MealConsumptionRecyclerAdapter;
import com.example.listochek.model.MealModel;
import com.example.listochek.utils.FirebaseUtil;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MealDetailsActivity extends AppCompatActivity {
    private static final String TAG = "MealDetailsActivity";
    private RecyclerView mealRecyclerView;
    private MealConsumptionRecyclerAdapter adapter;
    private String mealType;
    private List<MealModel> allMeals = new ArrayList<>();
    private Map<String, Double> mealFactors = new HashMap<>();
    private TextView typeOfDishText;
    private ImageButton backBtn;
    private ImageButton addMealBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_details);

        mealType = getIntent().getStringExtra("mealType");

        mealRecyclerView = findViewById(R.id.mealRecyclerView);
        mealRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        typeOfDishText = findViewById(R.id.typeOfDishText);
        backBtn = findViewById(R.id.backBtn);
        addMealBtn = findViewById(R.id.addMealBtn);

        setupMealRecyclerView();
        setupButtons();
    }

    private void setupMealRecyclerView() {
        String userId = FirebaseUtil.currentUserId();
        String todayDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        Log.d(TAG, "Fetching meals for user: " + userId + " on date: " + todayDate);

        FirebaseFirestore.getInstance()
                .collection("mealConsumption")
                .document(userId)
                .collection("meals")
                .document(todayDate)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains(mealType)) {
                        List<Map<String, Object>> mealsList = (List<Map<String, Object>>) documentSnapshot.get(mealType);
                        for (Map<String, Object> meal : mealsList) {
                            String id = (String) meal.get("id");
                            String mealId = (String) meal.get("mealId");
                            double factor = meal.containsKey("factor") ? ((Double) meal.get("factor")) : 1.0;
                            mealFactors.put(id, factor);
                            fetchMealFromCollections(mealId, factor);
                        }
                        Log.d(TAG, "Fetched mealConsumption meal IDs: " + mealFactors.keySet());
                    } else {
                        Log.d(TAG, "No mealConsumption data found for mealType: " + mealType);
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching mealConsumption data", e));
    }

    private void fetchMealFromCollections(String mealId, double factor) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Fetch meal from "meal" collection
        db.collection("meal").document(mealId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        MealModel meal = documentSnapshot.toObject(MealModel.class);
                        if (meal != null) {
                            allMeals.add(applyFactorToMeal(meal, factor));
                            setupAdapter();
                        }
                    } else {
                        fetchUserMealFromCollection(mealId, factor);
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching meal from 'meal' collection", e));
    }

    private void fetchUserMealFromCollection(String mealId, double factor) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseUtil.currentUserId();

        // Fetch meal from "usersMeal" collection
        db.collection("meal")
                .document("usersMeal")
                .collection(userId)
                .document(mealId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        MealModel meal = documentSnapshot.toObject(MealModel.class);
                        if (meal != null) {
                            allMeals.add(applyFactorToMeal(meal, factor));
                            setupAdapter();
                        }
                    } else {
                        Log.d(TAG, "Meal not found for mealId: " + mealId);
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching meal from 'usersMeal' collection", e));
    }

    private MealModel applyFactorToMeal(MealModel meal, double factor) {
        MealModel mealWithFactor = new MealModel();
        mealWithFactor.setId(meal.getId());
        mealWithFactor.setName(meal.getName());
        mealWithFactor.setWeight((int) Math.round(meal.getWeight() * factor));
        mealWithFactor.setCalories((int) Math.round(meal.getCalories() * factor));
        mealWithFactor.setProtein((int) Math.round(meal.getProtein() * factor));
        mealWithFactor.setFats((int) Math.round(meal.getFats() * factor));
        mealWithFactor.setCarbohydrates((int) Math.round(meal.getCarbohydrates() * factor));
        mealWithFactor.setFactor(factor);
        return mealWithFactor;
    }

    public void removeMeal(MealModel meal) {
        String userId = FirebaseUtil.currentUserId();
        String todayDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        FirebaseFirestore.getInstance()
                .collection("mealConsumption")
                .document(userId)
                .collection("meals")
                .document(todayDate)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains(mealType)) {
                        List<Map<String, Object>> mealsList = (List<Map<String, Object>>) documentSnapshot.get(mealType);
                        Map<String, Object> mealToRemove = null;
                        for (Map<String, Object> mealItem : mealsList) {
                            if (meal.getId().equals(mealItem.get("mealId")) && meal.getFactor() == (Double) mealItem.get("factor")) {
                                mealToRemove = mealItem;
                                break;
                            }
                        }
                        if (mealToRemove != null) {
                            mealsList.remove(mealToRemove);
                            if (mealsList.isEmpty()) {
                                documentSnapshot.getReference().update(mealType, FieldValue.delete())
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d(TAG, "Meal removed from mealConsumption");
                                            checkAndDeleteDocument(userId, todayDate);
                                            allMeals.remove(meal);
                                            adapter.notifyDataSetChanged();
                                        })
                                        .addOnFailureListener(e -> Log.e(TAG, "Error removing meal from mealConsumption", e));
                            } else {
                                documentSnapshot.getReference().update(mealType, mealsList)
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d(TAG, "Meal removed from mealConsumption");
                                            allMeals.remove(meal);
                                            adapter.notifyDataSetChanged();
                                        })
                                        .addOnFailureListener(e -> Log.e(TAG, "Error removing meal from mealConsumption", e));
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching mealConsumption data", e));
    }

    private void checkAndDeleteDocument(String userId, String todayDate) {
        FirebaseFirestore.getInstance()
                .collection("mealConsumption")
                .document(userId)
                .collection("meals")
                .document(todayDate)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> data = documentSnapshot.getData();
                        if (data == null || data.isEmpty() || data.values().stream().allMatch(value -> value instanceof List && ((List<?>) value).isEmpty())) {
                            documentSnapshot.getReference().delete()
                                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Document deleted as it was empty"))
                                    .addOnFailureListener(e -> Log.e(TAG, "Error deleting empty document", e));
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching document to check for deletion", e));
    }

    private void setupAdapter() {
        if (adapter == null) {
            adapter = new MealConsumptionRecyclerAdapter(allMeals, this);
            mealRecyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private void setupButtons() {
        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MealDetailsActivity.this, HomePage.class);
            startActivity(intent);
        });

        if (mealType.equals("Breakfast")) {
            typeOfDishText.setText("Завтрак");
        } else if (mealType.equals("Dinner")) {
            typeOfDishText.setText("Ужин");
        } else if (mealType.equals("Lunch")) {
            typeOfDishText.setText("Обед");
        } else if (mealType.equals("Snack")) {
            typeOfDishText.setText("Перекус");
        }

        addMealBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MealDetailsActivity.this, SelectABreakfast.class);
            intent.putExtra("type", mealType);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
