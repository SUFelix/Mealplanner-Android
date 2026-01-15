package com.felix.mealplanner20.use_cases

import com.felix.mealplanner20.Meals.Data.RecipeRepository

class DeleteIngredientFromRecipeUseCase(
    private val recipeRepository: RecipeRepository

) {
    suspend operator fun invoke(recipeId: Long, ingredientId: Long) {
        recipeRepository.deleteIngredientFromRecipe(recipeId, ingredientId)
    }
}