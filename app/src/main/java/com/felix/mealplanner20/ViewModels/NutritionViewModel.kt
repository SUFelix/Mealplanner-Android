package com.felix.mealplanner20.ViewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felix.mealplanner20.Meals.Data.helpers.AllDayDetailsWithGlobalDge
import com.felix.mealplanner20.Meals.Data.helpers.DgeData
import com.felix.mealplanner20.Meals.Data.helpers.PlantMetricData
import com.felix.mealplanner20.use_cases.CalculatePlantMetricUseCase
import com.felix.mealplanner20.use_cases.GetDayDetailsUseCase
import com.felix.mealplanner20.use_cases.NutritionBasicUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NutritionViewModel @Inject constructor(
    private val nutritionUseCases: NutritionBasicUseCases,
    private val getDayDetailsUseCase: GetDayDetailsUseCase,
    private val calculatePlantMetricUseCase: CalculatePlantMetricUseCase
) : ViewModel() {

    lateinit var allDayDetailsWithGlobalDgeFlow : Flow<AllDayDetailsWithGlobalDge>
    lateinit var dgeRecommendationData:List<DgeData>
    lateinit var plantMetric: Flow<PlantMetricData>
    lateinit var caloriesAvg: Flow<Float>
    lateinit var caloriesPerDay: Flow<List<Float>>

    private val _isLoading = MutableStateFlow<Boolean>(true)
    val isLoading: StateFlow<Boolean> = _isLoading
    init {
        loadDayDetails()
    }

    private fun calculatePlantMetric() {
            plantMetric = calculatePlantMetricUseCase(getDayDetailsUseCase.getDistinctIngredientsFlow())
    }

    private fun calculateCaloriesAvg(allDayDetailsWithGlobalDgeFlow: Flow<AllDayDetailsWithGlobalDge>) {
            caloriesAvg = nutritionUseCases.calculateCaloriesUseCase.calculateCaloriesAvg(
                allDayDetailsWithGlobalDgeFlow.map { it.dayDetails }
            )
    }

    private fun calculateCaloriesPerDay(allDayDetailsWithGlobalDgeFlow: Flow<AllDayDetailsWithGlobalDge>) {
            caloriesPerDay = nutritionUseCases.calculateCaloriesUseCase.calculateCaloriesPerDay(
                allDayDetailsWithGlobalDgeFlow.map { it.dayDetails }
            )
    }

    private fun loadDayDetails() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                allDayDetailsWithGlobalDgeFlow = getDayDetailsUseCase()
                dgeRecommendationData = nutritionUseCases.getDgeRecommendationDataUseCase()

                calculatePlantMetric()
                calculateCaloriesAvg(allDayDetailsWithGlobalDgeFlow)
                calculateCaloriesPerDay(allDayDetailsWithGlobalDgeFlow)

                allDayDetailsWithGlobalDgeFlow.collect {
                        _isLoading.value = false
                }

            } catch (e: Exception) {
                Log.e("NutritionViewModel", "Failed to load day details", e)
                _isLoading.value = false
            }
        }
    }
}