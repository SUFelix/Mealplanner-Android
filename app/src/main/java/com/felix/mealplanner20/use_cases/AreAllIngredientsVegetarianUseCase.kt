package com.felix.mealplanner20.use_cases

import com.felix.mealplanner20.Meals.Data.IngredientRepository

class AreAllIngredientsVegetarianUseCase(
    private val ingredientRepository: IngredientRepository
) {
    suspend operator fun invoke(ingredientIds: List<Long>): Boolean {
        val ingredients = ingredientRepository.getIngredientListByIdList(ingredientIds)
        return ingredients.all { it.isVegetarian() }
    }
}
