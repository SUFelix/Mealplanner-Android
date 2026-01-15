package com.felix.mealplanner20.use_cases

import com.felix.mealplanner20.Meals.Data.Recipe
import com.felix.mealplanner20.Meals.Data.RecipeRepository
import kotlinx.coroutines.flow.Flow

class GetAllSnacksUseCase(
    private val repository: RecipeRepository
) {
    operator fun invoke(): Flow<List<Recipe>> {
        return repository.getAllSnacks()
    }
}