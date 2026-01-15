package com.felix.mealplanner20.use_cases

import com.felix.mealplanner20.Meals.Data.IngredientRepository

class SynchronizeIngredientsUseCase(
    private val ingredientRepository: IngredientRepository
) {
    suspend operator fun invoke(): Unit {
        ingredientRepository.syncronizeIngredients()
    }
}

