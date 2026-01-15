package com.felix.mealplanner20.use_cases

import com.felix.mealplanner20.Meals.Data.IngredientRepository
import com.felix.mealplanner20.Meals.Data.IngredientWithRecipe
import kotlinx.coroutines.flow.first

class CalculateTotalCaloriesForIngredientWithRecipeListUseCase(
    private val ingredientRepository: IngredientRepository
) {
    suspend operator fun invoke(ingredientWithRecipe: List<IngredientWithRecipe>): Float {

        return  calculateTotalCalories(ingredientWithRecipe)
    }

   /* suspend fun calculateTotalCalories(ingredientWithRecipe: List<IngredientWithRecipe>): Float {
        var totalProtein = 0f
        var totalFat = 0f
        var totalCarbs = 0f
        var totalALcohol = 0f

        for (item in ingredientWithRecipe) {
            val ingredient = ingredientRepository.getIngredientById(item.ingredientId).first()

            totalProtein += ingredient.protein * item.ingredientQuantity/100
            totalFat += ingredient.fat * item.ingredientQuantity/100
            totalCarbs += ingredient.carbs * item.ingredientQuantity/100
            totalALcohol += ingredient.alcohol * item.ingredientQuantity/100
        }

        val carbsCalories = totalCarbs * CARBS_CALORIES
        val proteinCalories =totalProtein * PROTEIN_CALORIES
        val fatCalories = totalFat * FAT_CALORIES
        val alcoholCalories = totalALcohol * ALCOHOL_CALORIES

        val totalRecipeCalories = carbsCalories+proteinCalories+fatCalories+alcoholCalories

        return totalRecipeCalories
    }*/

    suspend fun calculateTotalCalories(ingredientWithRecipe: List<IngredientWithRecipe>): Float {
        var totalCalories = 0f

        for (item in ingredientWithRecipe) {
            val ingredient = ingredientRepository.getIngredientById(item.ingredientId).first()

            // Nutze die offiziellen kcal pro 100g
            totalCalories += ingredient.calories * item.ingredientQuantity / 100
        }

        return totalCalories
    }
}