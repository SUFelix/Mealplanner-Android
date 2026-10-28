package com.felix.mealplanner20.ViewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felix.mealplanner20.Meals.Data.DTO.IngredientDTO
import com.felix.mealplanner20.use_cases.ApproveIngredientUseCase
import com.felix.mealplanner20.use_cases.GetPendingIngredientsUseCase
import com.felix.mealplanner20.use_cases.RejectIngredientUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FoodAdminReviewViewModel @Inject constructor(
    private val getPendingIngredientsUseCase: GetPendingIngredientsUseCase,
    private val approveIngredientUseCase: ApproveIngredientUseCase,
    private val rejectIngredientUseCase: RejectIngredientUseCase
) : ViewModel() {

    private val _pendingIngredients = MutableStateFlow<List<IngredientDTO>>(emptyList())
    val pendingIngredients: StateFlow<List<IngredientDTO>> = _pendingIngredients

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _processingId = MutableStateFlow<Long?>(null)
    val processingId: StateFlow<Long?> = _processingId

    init {
        loadPendingIngredients()
    }

    fun loadPendingIngredients() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _pendingIngredients.value = getPendingIngredientsUseCase.execute()
            } catch (e: Exception) {
                Log.e("FoodAdminReviewViewModel", "Error loading pending ingredients", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun approve(id: Long) {
        viewModelScope.launch {
            _processingId.value = id
            if (approveIngredientUseCase.execute(id)) {
                _pendingIngredients.update { list -> list.filterNot { it.id == id } }
            }
            _processingId.value = null
        }
    }

    fun reject(id: Long) {
        viewModelScope.launch {
            _processingId.value = id
            if (rejectIngredientUseCase.execute(id)) {
                _pendingIngredients.update { list -> list.filterNot { it.id == id } }
            }
            _processingId.value = null
        }
    }
}
