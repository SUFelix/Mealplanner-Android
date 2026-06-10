package com.felix.mealplanner20.use_cases

import com.felix.mealplanner20.Meals.Data.Ingredient
import com.felix.mealplanner20.Meals.Data.IngredientRepository
import com.felix.mealplanner20.Meals.Data.helpers.UnitOfMeasure
import com.felix.mealplanner20.Shopping.Data.ShoppingListItem
import com.felix.mealplanner20.Shopping.Data.ShoppingListRepository
import kotlinx.coroutines.flow.Flow
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

    suspend fun getShoppingListWithIngredients(): Flow<List<ShoppingListItemWithIngredient>> {
        return shoppingListRepository.getShoppingListItems().flatMapLatest { shoppingListItems ->
            if (shoppingListItems.isNotEmpty()) {
                val ingredientIds = shoppingListItems
                    .filter { it.customName == null }
                    .map { it.ingredientId }

                if (ingredientIds.isEmpty()) {
                    flowOf(shoppingListItems.map { item ->
                        ShoppingListItemWithIngredient(
                            id = item.id,
                            ingredientId = item.ingredientId,
                            quantity = item.quantity,
                            unitOfMeasure = item.unitOfMeasure,
                            isChecked = item.isChecked,
                            ingredient = null,
                            customName = item.customName
                        )
                    })
                } else {
                    ingredientRepository.getIngredientListFlowByIdList(ingredientIds).map { ingredients ->
                        shoppingListItems.mapNotNull { item ->
                            if (item.customName != null) {
                                ShoppingListItemWithIngredient(
                                    id = item.id,
                                    ingredientId = item.ingredientId,
                                    quantity = item.quantity,
                                    unitOfMeasure = item.unitOfMeasure,
                                    isChecked = item.isChecked,
                                    ingredient = null,
                                    customName = item.customName
                                )
                            } else {
                                val ingredient = ingredients.find { it.id == item.ingredientId }
                                    ?: return@mapNotNull null
                                ShoppingListItemWithIngredient(
                                    id = item.id,
                                    ingredientId = item.ingredientId,
                                    quantity = item.quantity,
                                    unitOfMeasure = item.unitOfMeasure,
                                    isChecked = item.isChecked,
                                    ingredient = ingredient,
                                    customName = null
                                )
                            }
                        }
                    }
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
    val isChecked: Boolean,
    val ingredient: Ingredient?,
    val customName: String?
)
