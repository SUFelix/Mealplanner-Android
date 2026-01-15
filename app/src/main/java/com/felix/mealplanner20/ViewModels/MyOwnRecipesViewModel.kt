package com.felix.mealplanner20.ViewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felix.mealplanner20.Meals.Data.Recipe
import com.felix.mealplanner20.Meals.Data.RecipeCalories
import com.felix.mealplanner20.Meals.Data.RecipeRepository
import com.felix.mealplanner20.Meals.Data.SettingsRepository
import com.felix.mealplanner20.use_cases.NutritionBasicUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyOwnRecipesViewModel @Inject constructor (
    private val recipeRepository: RecipeRepository,
    private val nutritionUseCases: NutritionBasicUseCases,
    private val settingsRepository: SettingsRepository
): ViewModel() {

    lateinit var getAllRecipes: Flow<List<Recipe>>
    lateinit var getAllCalories: Flow<List<RecipeCalories>>



    private val _isLoading = MutableStateFlow<Boolean>(true)
    val isLoading: StateFlow<Boolean> = _isLoading
    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            try{
                getAllRecipes = recipeRepository.getAllRecipes()
                getAllCalories = nutritionUseCases.getRecipeCaloriesUseCase()
            }catch (e:Exception){
                Log.e("Error loading Data",e.toString())
            }finally {
                _isLoading.value = false
            }
        }
    }
}