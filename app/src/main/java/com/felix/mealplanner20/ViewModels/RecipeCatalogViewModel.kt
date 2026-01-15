package com.felix.mealplanner20.ViewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felix.mealplanner20.Meals.Data.IngredientWithRecipe
import com.felix.mealplanner20.Meals.Data.Recipe
import com.felix.mealplanner20.Meals.Data.RecipeCalories
import com.felix.mealplanner20.Meals.Data.RecipeRepository
import com.felix.mealplanner20.Meals.Data.SettingsRepository
import com.felix.mealplanner20.Meals.Data.helpers.Mealtype
import com.felix.mealplanner20.use_cases.NutritionBasicUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeCatalogViewModel @Inject constructor (
    private val recipeRepository: RecipeRepository,
    private val nutritionUseCases: NutritionBasicUseCases,
    private val settingsRepository: SettingsRepository
):ViewModel() {

    lateinit var getAllRecipes: Flow<List<Recipe>>
    lateinit var getAllBreakfasts: Flow<List<Recipe>>
    lateinit var getAllMeals: Flow<List<Recipe>>
    lateinit var getAllSnacks: Flow<List<Recipe>>
    lateinit var getAllBeverages: Flow<List<Recipe>>
    lateinit var getAllFavoriteRecipes: Flow<List<Recipe>>
    lateinit var getAllIngredientWithRecipe: Flow<List<IngredientWithRecipe>>
    lateinit var getAllCalories: Flow<List<RecipeCalories>>

    private val _isLoading = MutableStateFlow<Boolean>(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _selectedTabIndex = MutableStateFlow(0)
    val selectedTabIndex : StateFlow<Int> = _selectedTabIndex

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
        try{
            val settings = settingsRepository.getSettings()
            val isVegan = settings?.vegan ?: false
            val isVegetarian = settings?.vegetarian ?: false

            getAllRecipes = recipeRepository.getAllRecipes(isVegan, isVegetarian)
            getAllBreakfasts = recipeRepository.getAllBreakfasts()
            getAllSnacks = recipeRepository.getAllSnacks()
            getAllMeals = recipeRepository.getAllMeals()
            getAllBeverages = recipeRepository.getAllBeverages()
            getAllIngredientWithRecipe = recipeRepository.getAllIngredientWithRecipe()
            getAllFavoriteRecipes = recipeRepository.getAllFavoriteRecipes()
            getAllCalories = nutritionUseCases.getRecipeCaloriesUseCase()
        }catch (e:Exception){
            Log.e("Error loading Data",e.toString())
        }finally {
            _isLoading.value = false
        }
        }
    }

    fun getRecipesByIds(ids:List<Long>):Flow <List<Recipe>>{
        return recipeRepository.getRecipesByIds(ids)
    }

    fun updateSelectedTabIndex(newIndex: Int) {
        _selectedTabIndex.value = newIndex
    }

    fun observeWeightsByMealTypeAsMap(mealType: Mealtype): Flow<Map<Long, Float>> =
        recipeRepository.observeWeightsByMealTypeAsMap(mealType)

    fun updateWeight(recipeId: Long, mealType: Mealtype, weight: Float) {
        viewModelScope.launch {
            recipeRepository.updateWeight(recipeId, mealType, weight)
        }
    }
}