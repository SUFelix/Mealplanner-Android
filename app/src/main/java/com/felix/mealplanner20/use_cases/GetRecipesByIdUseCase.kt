package com.felix.mealplanner20.use_cases

import com.felix.mealplanner20.Meals.Data.Recipe
import com.felix.mealplanner20.Meals.Data.RecipeRepository
import kotlinx.coroutines.flow.Flow

class GetRecipesByIdsUseCase(
    private val repository: RecipeRepository
) {
    operator fun invoke(
        ids:List<Long>):Flow <List<Recipe>>{
        return repository.getRecipesByIds(ids)
    }
}