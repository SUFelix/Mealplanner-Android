package com.felix.mealplanner20.use_cases

import com.felix.mealplanner20.Meals.Data.Ingredient
import com.felix.mealplanner20.Meals.Data.IngredientRepository
import com.felix.mealplanner20.Meals.Data.IngredientWithRecipe
import com.felix.mealplanner20.Meals.Data.MacronutrientRatio
import kotlinx.coroutines.flow.first

class CalculateCPFratioUseCase(
    private val ingredientRepository: IngredientRepository
) {
    suspend operator fun invoke(ingredientWithRecipe: List<IngredientWithRecipe>): MacronutrientRatio {
        val triple = calculateTotalNutrients(ingredientWithRecipe)
        val calorietriple = calculateTotalCalories(triple)

       return  MacronutrientRatio(calorietriple.first.toInt(),calorietriple.second.toInt(),calorietriple.third.toInt())
    }


    fun calculateTotalCalories(nutrients: Triple<Float, Float, Float>): Triple<Float, Float, Float> {
        val carbsCalories = nutrients.first * CARBS_CALORIES
        val proteinCalories = nutrients.second * PROTEIN_CALORIES
        val fatCalories = nutrients.third * FAT_CALORIES


        val totalCalories = proteinCalories + fatCalories + carbsCalories

        val carbsPercentage = if (totalCalories > 0) (carbsCalories / totalCalories) * 100 else 0f
        val proteinPercentage = if (totalCalories > 0) (proteinCalories / totalCalories) * 100 else 0f
        val fatPercentage = if (totalCalories > 0) (fatCalories / totalCalories) * 100 else 0f

        return Triple(carbsPercentage, proteinPercentage, fatPercentage)
    }

    suspend fun calculateTotalNutrients(ingredientWithRecipe: List<IngredientWithRecipe>): Triple<Float, Float, Float> {
        var totalProtein = 0f
        var totalFat = 0f
        var totalCarbs = 0f

        // Gehe jedes Element in der ingredientWithRecipe Liste durch
        for (item in ingredientWithRecipe) {
            // Hole die Zutat anhand der ingredientId
            val ingredient = ingredientRepository.getIngredientById(item.ingredientId).first()

            // Multipliziere die Menge der Zutat (ingredientQuantity) mit den Nährwerten der Zutat
            totalProtein += ingredient.protein * item.ingredientQuantity
            totalFat += ingredient.fat * item.ingredientQuantity
            totalCarbs += ingredient.carbs * item.ingredientQuantity
        }

        // Gebe die Gesamtnährwerte als Triple zurück
        return Triple( totalCarbs,totalProtein,totalFat)
    }


}

