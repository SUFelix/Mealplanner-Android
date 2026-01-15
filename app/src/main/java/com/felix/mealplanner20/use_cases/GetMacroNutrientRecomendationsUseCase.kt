package com.felix.mealplanner20.use_cases

import com.felix.mealplanner20.Meals.Data.SettingsRepository

class GetMacroNutrientRecomendationsUseCase(private val settingsRepository: SettingsRepository) {

    suspend operator fun invoke(): Map<String, Float?> {
        settingsRepository.getSettings()?.let {settings->

        val protein = settings.proteinRequirement
        val fat = settings.fatRequirement
        val calorieRequirement = settings.calorieRequirement

        val saturatedFat = fat * 0.4f

        val proteinCalories = protein * PROTEIN_CALORIES
        val fatCalories = fat * FAT_CALORIES
        val carbsCalories = calorieRequirement - (proteinCalories + fatCalories)
        val carbs = carbsCalories / CARBS_CALORIES.toFloat()


        val dailyRecommendations = mapOf(
            PROTEIN to protein?.toFloat(),
            CARBS to carbs,
            FAT to fat?.toFloat(),
            SATURATED_FAT to saturatedFat,
            SUGAR to 25f,
            FIBRE to 30f,
            ALCOHOL to 10f
        )
        return dailyRecommendations
        }
        return  emptyMap()
    }


    fun isSugarOrAlcohol(value: String): Boolean {
        return when (value) {
            SUGAR, ALCOHOL -> true
            else -> false
        }
    }
}


