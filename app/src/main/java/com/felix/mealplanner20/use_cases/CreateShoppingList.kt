package com.felix.mealplanner20.use_cases

import com.felix.mealplanner20.Shopping.Data.ShoppingListRepository
import com.felix.mealplanner20.Meals.Data.MealPlanRepository
import com.felix.mealplanner20.Meals.Data.RecipeRepository
import com.felix.mealplanner20.Meals.Data.helpers.UnitOfMeasure
import kotlinx.coroutines.flow.first

class CreateShoppingListUseCase(
    private val shoppingListRepository: ShoppingListRepository,
    private val mealPlanRepository: MealPlanRepository,
    private val recipeRepository: RecipeRepository
) {
    suspend operator fun invoke() {
        val mealPlanDays = mealPlanRepository.getAllMealPlanDays().first()

        val ingredientQuantities = mutableMapOf<Long, Pair<Float, UnitOfMeasure>>()

        for (mealPlanDay in mealPlanDays) {
            val recipeQuantities = mealPlanRepository
                .getRecipeQuantitiesFlowForDay(mealPlanDay.id)
                .first()

            for ((recipeId, quantity) in recipeQuantities) {
                val ingredientsForRecipe = recipeRepository.getIngredientsForRecipe(recipeId)

                ingredientsForRecipe.forEach { ingredientWithRecipe ->
                    val currentQuantity = ingredientQuantities[ingredientWithRecipe.ingredientId]?.first ?: 0f
                    val currentUnitOfMeasure = ingredientQuantities[ingredientWithRecipe.ingredientId]?.second
                    val newQuantity = currentQuantity + (ingredientWithRecipe.ingredientQuantity * quantity)

                    ingredientQuantities[ingredientWithRecipe.ingredientId] =
                        Pair(newQuantity, currentUnitOfMeasure ?: ingredientWithRecipe.unitOfMeasure)
                }
            }
        }

        ingredientQuantities.forEach { (ingredientId, quantityUnitPair) ->
            shoppingListRepository.addIngredientToShoppingList(
                ingredientId = ingredientId,
                quantity = quantityUnitPair.first,
                unitOfMeasure = quantityUnitPair.second
            )
        }
    }
}



