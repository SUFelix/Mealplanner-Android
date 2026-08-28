package com.felix.mealplanner20.ViewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felix.mealplanner20.Meals.Data.DTO.IngredientMatchReviewDTO
import com.felix.mealplanner20.use_cases.ConfirmMatchUseCase
import com.felix.mealplanner20.use_cases.GetPendingMatchesUseCase
import com.felix.mealplanner20.use_cases.RejectMatchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FoodAdminMatchReviewViewModel @Inject constructor(
    private val getPendingMatchesUseCase: GetPendingMatchesUseCase,
    private val confirmMatchUseCase: ConfirmMatchUseCase,
    private val rejectMatchUseCase: RejectMatchUseCase
) : ViewModel() {

    private val _pendingMatches = MutableStateFlow<List<IngredientMatchReviewDTO>>(emptyList())
    val pendingMatches: StateFlow<List<IngredientMatchReviewDTO>> = _pendingMatches

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _processingTaskId = MutableStateFlow<Long?>(null)
    val processingTaskId: StateFlow<Long?> = _processingTaskId

    init {
        loadPendingMatches()
    }

    fun loadPendingMatches() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _pendingMatches.value = getPendingMatchesUseCase.execute()
            } catch (e: Exception) {
                Log.e("FoodAdminMatchReviewViewModel", "Error loading pending matches", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun confirm(taskId: Long) {
        viewModelScope.launch {
            _processingTaskId.value = taskId
            if (confirmMatchUseCase.execute(taskId)) {
                _pendingMatches.update { list -> list.filterNot { it.taskId == taskId } }
            }
            _processingTaskId.value = null
        }
    }

    fun reject(taskId: Long) {
        viewModelScope.launch {
            _processingTaskId.value = taskId
            if (rejectMatchUseCase.execute(taskId)) {
                _pendingMatches.update { list -> list.filterNot { it.taskId == taskId } }
            }
            _processingTaskId.value = null
        }
    }
}
