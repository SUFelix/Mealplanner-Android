package com.felix.mealplanner20.use_cases

import com.felix.mealplanner20.Meals.Data.RecipeRepository

class DeleteRecipeByIdUseCase(
    private val recipeRepository: RecipeRepository

) {
    suspend operator fun invoke(id: Long) {
        recipeRepository.deleteRecipeById(id)
    }
}

