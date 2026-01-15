package com.felix.mealplanner20.Shopping.Data

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.felix.mealplanner20.Meals.Data.Ingredient
import com.felix.mealplanner20.Meals.Data.IngredientDao
import com.felix.mealplanner20.Meals.Data.helpers.UnitOfMeasure
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class ShoppingListRepository(
    private val shoppingListDao: ShoppingListDao,
    private val ingredientDao: IngredientDao
) {
    fun getShoppingListItems(): Flow<List<ShoppingListItem>> = shoppingListDao.getAllShoppingListItems()

    suspend fun addIngredientToShoppingList(ingredientId: Long, quantity: Float = 1f, unitOfMeasure: UnitOfMeasure = UnitOfMeasure.GRAM) {
        val item = ShoppingListItem(
            ingredientId = ingredientId,
            quantity = quantity,
            unitOfMeasure = unitOfMeasure
        )
        shoppingListDao.addShoppingListItem(item)
    }

    suspend fun deleteOneItemFromShoppingListUseCase(ingredientId:Long){
        shoppingListDao.deleteShoppingListItem(ingredientId)
    }
    fun getIngredientsForShoppingList(): Flow<List<Ingredient>> = getShoppingListItems().flatMapLatest { items ->
        val ingredientIds = items.map { it.ingredientId }
        ingredientDao.getIngredientListByIdList(ingredientIds)
    }

    suspend fun clearShoppingList(){
        shoppingListDao.clearShoppingList()
    }
}

