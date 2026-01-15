package com.felix.mealplanner20.use_cases

import com.felix.mealplanner20.Meals.Data.IngredientWithRecipe
import com.felix.mealplanner20.Meals.Data.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class GetAllIngredientsForOneRecipeUseCase(
    private val repository: RecipeRepository
) {
    operator fun invoke(id: Long): Flow<List<IngredientWithRecipe>> {
        return repository.getAllIngredientsForOneRecipe(id)
    }
}