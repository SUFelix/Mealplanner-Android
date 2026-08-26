package com.felix.mealplanner20.ViewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felix.mealplanner20.use_cases.ApproveAllowedUnitUseCase
import com.felix.mealplanner20.use_cases.GetIngredientByIdUseCase
import com.felix.mealplanner20.use_cases.GetPendingAllowedUnitsUseCase
import com.felix.mealplanner20.use_cases.GetPendingIngredientsUseCase
import com.felix.mealplanner20.use_cases.RejectAllowedUnitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PendingAllowedUnitUi(
    val ingredientId: Long,
    val unitOfMeasure: String,
    val gramsPerUnit: Float,
    val germanName: String,
    val englishName: String?
)

@HiltViewModel
class FoodAdminAllowedUnitViewModel @Inject constructor(
    private val getPendingAllowedUnitsUseCase: GetPendingAllowedUnitsUseCase,
    private val approveAllowedUnitUseCase: ApproveAllowedUnitUseCase,
    private val rejectAllowedUnitUseCase: RejectAllowedUnitUseCase,
    private val getIngredientByIdUseCase: GetIngredientByIdUseCase,
    private val getPendingIngredientsUseCase: GetPendingIngredientsUseCase
) : ViewModel() {

    private val _pendingAllowedUnits = MutableStateFlow<List<PendingAllowedUnitUi>>(emptyList())
    val pendingAllowedUnits: StateFlow<List<PendingAllowedUnitUi>> = _pendingAllowedUnits

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _processingKey = MutableStateFlow<String?>(null)
    val processingKey: StateFlow<String?> = _processingKey

    init {
        loadPendingAllowedUnits()
    }

    fun loadPendingAllowedUnits() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val pendingUnits = getPendingAllowedUnitsUseCase.execute()
                // Fallback for freshly created, not-yet-approved ingredients: GET ingredients/{id}
                // 404s for those (it only serves APPROVED ones), so we match against the pending
                // ingredient review queue instead, which already carries the full name.
                val pendingIngredientsById = getPendingIngredientsUseCase.execute().associateBy { it.id }

                _pendingAllowedUnits.value = pendingUnits.map { unit ->
                    val approvedIngredient = getIngredientByIdUseCase.execute(unit.ingredientId)
                    val germanName = approvedIngredient?.germanName
                        ?: pendingIngredientsById[unit.ingredientId]?.germanName
                        ?: "#${unit.ingredientId}"
                    val englishName = approvedIngredient?.englishName
                        ?: pendingIngredientsById[unit.ingredientId]?.englishName
                    PendingAllowedUnitUi(
                        ingredientId = unit.ingredientId,
                        unitOfMeasure = unit.unitOfMeasure,
                        gramsPerUnit = unit.gramsPerUnit,
                        germanName = germanName,
                        englishName = englishName
                    )
                }
            } catch (e: Exception) {
                Log.e("FoodAdminAllowedUnitViewModel", "Error loading pending allowed units", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun approve(ingredientId: Long, unit: String) {
        viewModelScope.launch {
            _processingKey.value = key(ingredientId, unit)
            if (approveAllowedUnitUseCase.execute(ingredientId, unit)) {
                removeFromList(ingredientId, unit)
            }
            _processingKey.value = null
        }
    }

    fun reject(ingredientId: Long, unit: String) {
        viewModelScope.launch {
            _processingKey.value = key(ingredientId, unit)
            if (rejectAllowedUnitUseCase.execute(ingredientId, unit)) {
                removeFromList(ingredientId, unit)
            }
            _processingKey.value = null
        }
    }

    private fun removeFromList(ingredientId: Long, unit: String) {
        _pendingAllowedUnits.update { list ->
            list.filterNot { it.ingredientId == ingredientId && it.unitOfMeasure == unit }
        }
    }

    fun key(ingredientId: Long, unit: String) = "$ingredientId:$unit"
}
