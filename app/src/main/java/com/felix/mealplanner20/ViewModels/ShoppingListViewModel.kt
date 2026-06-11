package com.felix.mealplanner20.Shopping

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.felix.mealplanner20.Meals.Data.Ingredient
import com.felix.mealplanner20.Shopping.Data.ShoppingListItem
import com.felix.mealplanner20.Shopping.Data.ShoppingListRepository
import com.felix.mealplanner20.use_cases.RecipeUseCases
import com.felix.mealplanner20.use_cases.ShoppingListItemWithIngredient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShoppingListViewModel @Inject constructor(
    private val recipeUseCases: RecipeUseCases,
    private val shoppingListRepository: ShoppingListRepository
) : ViewModel() {

    private val _shoppingListItems = MutableStateFlow<List<ShoppingListItemWithIngredient>>(emptyList())
    val shoppingListItems: StateFlow<List<ShoppingListItemWithIngredient>> = _shoppingListItems.asStateFlow()

    private val _showCustomItemInput = MutableStateFlow(false)
    val showCustomItemInput: StateFlow<Boolean> = _showCustomItemInput.asStateFlow()

    init {
        loadShoppingList()
    }

    private fun loadShoppingList() {
        viewModelScope.launch {
            recipeUseCases.getShoppingListUseCase.getShoppingListWithIngredients().collect { items ->
                _shoppingListItems.value = items
            }
        }
    }

    fun refresh() {
        loadShoppingList()
    }

    fun deleteItemFromShoppingList(ingredientId: Long) {
        _shoppingListItems.value.find { it.id == ingredientId }?.let {
            _shoppingListItems.value -= it
        }
        viewModelScope.launch {
            shoppingListRepository.deleteOneItemFromShoppingListUseCase(ingredientId)
        }
    }

    fun createShoppingList() {
        viewModelScope.launch {
            try {
                recipeUseCases.createShoppingListUseCase()
                refresh()
            } catch (e: Exception) {
                Log.e("Error", "createShoppingList failed: ${e.message}")
            }
        }
    }

    fun toggleCustomItemInput() {
        _showCustomItemInput.value = !_showCustomItemInput.value
    }

    fun addCustomItem(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            recipeUseCases.addCustomShoppingListItemUseCase(name.trim())
            _showCustomItemInput.value = false
        }
    }

    fun toggleItemChecked(id: Long, isChecked: Boolean) {
        viewModelScope.launch {
            shoppingListRepository.updateCheckedState(id, isChecked)
        }
    }

    fun clearShoppingList() {
        viewModelScope.launch {
            try {
                recipeUseCases.clearShoppingListUseCase()
                refresh() // Liste neu laden, um die leere Liste anzuzeigen
            } catch (e: Exception) {
                Log.e("Error", "clearShoppingList failed: ${e.message}")
            }
        }
    }
}