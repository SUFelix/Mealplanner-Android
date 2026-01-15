package com.felix.mealplanner20.use_cases

import com.felix.mealplanner20.Meals.Data.IngredientRepository
import com.felix.mealplanner20.Meals.Data.RecipeRepository
import com.felix.mealplanner20.Meals.Data.helpers.UnitOfMeasure

class AddIngredientToRecipeUseCase(
    private val repository: RecipeRepository,
    private val ingredientRepository: IngredientRepository
) {
    suspend operator fun invoke(recipeId: Long, ingredientId: Long, quantity: Float, unitOfMeasure: UnitOfMeasure, originalQuantity: Float) {

        repository.addIngredientToRecipe(recipeId, ingredientId, quantity, unitOfMeasure,originalQuantity)
    }
}

