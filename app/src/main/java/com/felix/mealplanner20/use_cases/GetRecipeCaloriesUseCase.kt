package com.felix.mealplanner20.use_cases

import com.felix.mealplanner20.Meals.Data.RecipeCalories
import com.felix.mealplanner20.Meals.Data.RecipeRepository
import kotlinx.coroutines.flow.Flow

class GetRecipeCaloriesUseCase(
    private val repository: RecipeRepository
) {
    operator fun invoke(): Flow<List<RecipeCalories>> {
        return repository.getRecipeCalories()
    }
}
