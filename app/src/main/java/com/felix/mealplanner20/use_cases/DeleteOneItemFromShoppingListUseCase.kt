package com.felix.mealplanner20.use_cases

import com.felix.mealplanner20.Shopping.Data.ShoppingListRepository
import javax.inject.Inject

class DeleteOneItemFromShoppingListUseCase @Inject constructor(
    private val shoppingListRepository: ShoppingListRepository
) {
    suspend operator fun invoke(ingredientId:Long) {
        shoppingListRepository.deleteOneItemFromShoppingListUseCase(ingredientId)
    }
}