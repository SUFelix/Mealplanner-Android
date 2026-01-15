package com.felix.mealplanner20.use_cases

import com.felix.mealplanner20.Meals.Data.Ingredient
import com.felix.mealplanner20.Meals.Data.IngredientRepository
import com.felix.mealplanner20.Meals.Data.helpers.UnitOfMeasure
import com.felix.mealplanner20.Shopping.Data.ShoppingListItem
import com.felix.mealplanner20.Shopping.Data.ShoppingListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetShoppingListUseCase @Inject constructor(
    private val shoppingListRepository: ShoppingListRepository,
    private val ingredientRepository: IngredientRepository
) {
    suspend operator fun invoke(): Flow<List<ShoppingListItem>> {
        return shoppingListRepository.getShoppingListItems()
    }

    // Alternativ, falls du Ingredients mit den zugehörigen ShoppingListItem-Daten zurückgeben möchtest
    suspend fun getShoppingListWithIngredients(): Flow<List<ShoppingListItemWithIngredient>> {
        return shoppingListRepository.getShoppingListItems().flatMapLatest { shoppingListItems ->
            if (shoppingListItems.isNotEmpty()) {
                val ingredientIds = shoppingListItems.map { it.ingredientId }
                ingredientRepository.getIngredientListFlowByIdList(ingredientIds).map { ingredients ->
                    shoppingListItems.map { shoppingListItem ->
                        val ingredient = ingredients.find { it.id == shoppingListItem.ingredientId }
                        if (ingredient != null) {
                            ShoppingListItemWithIngredient(
                                id = shoppingListItem.id,
                                ingredientId = shoppingListItem.ingredientId,
                                quantity = shoppingListItem.quantity,
                                unitOfMeasure = shoppingListItem.unitOfMeasure,
                                ingredient = ingredient
                            )
                        } else {
                            // Handle the case where the ingredient is not found
                            null
                        }
                    }.filterNotNull()
                }
            } else {
                flowOf(emptyList())
            }
        }
    }
}
data class ShoppingListItemWithIngredient(
    val id: Long,
    val ingredientId: Long,
    val quantity: Float,
    val unitOfMeasure: UnitOfMeasure,
    val ingredient: Ingredient
)


