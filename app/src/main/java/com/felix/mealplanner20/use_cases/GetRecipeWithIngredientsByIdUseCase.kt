package com.felix.mealplanner20.use_cases

import com.felix.mealplanner20.Meals.Data.IngredientWithRecipe
import com.felix.mealplanner20.Meals.Data.RecipeRepository
import kotlinx.coroutines.flow.Flow

class GetRecipeWithIngredientsByIdUseCase(
    private val repository: RecipeRepository
) {
    operator fun invoke(recipeId: Long): Flow<List<IngredientWithRecipe>> {
        return repository.getRecipeWithIngredientsById(recipeId)
    }
}