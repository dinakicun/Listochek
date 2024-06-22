package com.example.listochek.utils;

import com.example.listochek.model.CharacteristicsModel;
import com.example.listochek.model.MealModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class NutritionCalculator {

    private static int totalCalories;
    private static int totalProtein;
    private static int totalCarbohydrates;
    private static int totalFats;

    // Коэффициенты для расчета углеводов, белков, жиров в зависимости от целевой калорийности и воды.
    private static final double PROTEIN_PER_KG = 1.2; // грамм белка на кг идеального веса
    private static final double FAT_PER_KG = 1.0; // грамм жира на кг идеального веса
    private static final double CARBS_PERCENTAGE = 0.5; // 50% калорий должны приходиться на углеводы
    private static final double WATER_PER_KG = 30; // мл воды на кг веса

    public static CharacteristicsModel calculateNutrition(String sex, int age, int height, int weight, boolean activeLifestyle, String userId) {
        int dailyCalories = calculateCalories(sex, age, height, weight, activeLifestyle);
        int protein = (int) (weight * PROTEIN_PER_KG); // Граммы белка
        int fat = (int) (weight * FAT_PER_KG); // Граммы жира
        // Калории из белков и жиров
        int proteinCalories = protein * 4; // 1 грамм белка = 4 калории
        int fatCalories = fat * 9; // 1 грамм жира = 9 калорий

        // Правильный расчет калорий от углеводов: общее число калорий для углеводов = 50% от дневной нормы
        int carbsCaloriesTotal = (int) (dailyCalories * CARBS_PERCENTAGE);
        // Калории, которые должны приходиться на углеводы, уже включают в себя желаемые 50% от общего числа калорий
        // Не нужно вычитать калории от белков и жиров из общего числа калорий для углеводов
        int carbs = carbsCaloriesTotal / 4; // Переводим калории в граммы углеводов

        double water = weight * WATER_PER_KG; // Расчет нормы воды

        return new CharacteristicsModel(dailyCalories, fat, protein, carbs, userId, water);
    }

    private static int calculateCalories(String sex, int age, int height, int weight, boolean activeLifestyle) {
        double bmr;
        if ("М".equalsIgnoreCase(sex)) {
            bmr = 10 * weight + 6.25 * height - 5 * age + 5;
        } else {
            bmr = 10 * weight + 6.25 * height - 5 * age - 161;
        }

        double multiplier = activeLifestyle ? 1.55 : 1.2;
        return (int) Math.round(bmr * multiplier);
    }

    public static void sumDailyIntake(String userId, Runnable callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        totalCalories = 0;
        totalProtein = 0;
        totalCarbohydrates = 0;
        totalFats = 0;

        db.collection("mealConsumption")
                .document(userId)
                .collection("meals")
                .document(currentDate)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> data = documentSnapshot.getData();
                        if (data != null) {
                            for (Map.Entry<String, Object> entry : data.entrySet()) {
                                List<Map<String, Object>> meals = (List<Map<String, Object>>) entry.getValue();
                                for (Map<String, Object> mealData : meals) {
                                    String mealId = (String) mealData.get("mealId");
                                    Double factor = (Double) mealData.get("factor");
                                    if (factor == null) {
                                        factor = 1.0; // Если factor отсутствует, инициализируем его значением 1.0
                                    }
                                    if (mealId != null) {
                                        fetchMealData(db, userId, mealId, callback, factor);
                                    }
                                }
                            }
                        } else {
                            callback.run();
                        }
                    } else {
                        callback.run();
                    }
                })
                .addOnFailureListener(e -> callback.run());
    }



    private static void fetchMealData(FirebaseFirestore db, String userId, String mealId, Runnable callback, double factor) {
        db.collection("meal").document(mealId).get().addOnSuccessListener(mealSnapshot -> {
            if (mealSnapshot.exists()) {
                MealModel meal = mealSnapshot.toObject(MealModel.class);
                if (meal != null) {
                    updateTotals(meal, factor);
                }
                callback.run();
            } else {
                db.collection("meal").document("usersMeal").collection(userId).document(mealId).get().addOnSuccessListener(userMealSnapshot -> {
                    if (userMealSnapshot.exists()) {
                        MealModel meal = userMealSnapshot.toObject(MealModel.class);
                        if (meal != null) {
                            updateTotals(meal, factor);
                        }
                    }
                    callback.run();
                }).addOnFailureListener(e -> callback.run());
            }
        }).addOnFailureListener(e -> callback.run());
    }


    private static void updateTotals(MealModel meal, double factor) {
        totalCalories += meal.getCalories() * factor;
        totalProtein += meal.getProtein() * factor;
        totalCarbohydrates += meal.getCarbohydrates() * factor;
        totalFats += meal.getFats() * factor;
    }


    private static void updateTotals(MealModel meal) {
        totalCalories += meal.getCalories() != null ? meal.getCalories() : 0;
        totalProtein += meal.getProtein() != null ? meal.getProtein() : 0;
        totalCarbohydrates += meal.getCarbohydrates() != null ? meal.getCarbohydrates() : 0;
        totalFats += meal.getFats() != null ? meal.getFats() : 0;
    }

    public static int getTotalCalories() {
        return totalCalories;
    }

    public static int getTotalProtein() {
        return totalProtein;
    }

    public static int getTotalCarbohydrates() {
        return totalCarbohydrates;
    }

    public static int getTotalFats() {
        return totalFats;
    }
}
