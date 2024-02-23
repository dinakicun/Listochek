package com.example.listochek.utils;

import com.example.listochek.model.CharacteristicsModel;

public class NutritionCalculator {

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
}