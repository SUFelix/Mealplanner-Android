package com.felix.mealplanner20.ViewModels

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felix.mealplanner20.Meals.Data.MealPlanDay
import com.felix.mealplanner20.Meals.Data.MealPlanRepository
import com.felix.mealplanner20.Meals.Data.helpers.RecipeQuantity
import com.felix.mealplanner20.use_cases.MealPlanGenerator
import com.felix.mealplanner20.use_cases.MealPlanUseCases
import com.felix.mealplanner20.use_cases.NutritionBasicUseCases
import com.felix.mealplanner20.use_cases.RecipeUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MealPlanViewModel @Inject constructor (
    private val recipeUseCase: RecipeUseCases,
    private val mealPlanUseCases: MealPlanUseCases,
    private val mealPlanGenerator: MealPlanGenerator,
    private val nutritionBasicUseCases: NutritionBasicUseCases,
    private val mealPlanRepository: MealPlanRepository
) :ViewModel() {
    val getAllMealPlanDays: Flow<List<MealPlanDay>> = mealPlanGenerator.getAllMealPlanDays()

    private val _recipeCaloriesMap = nutritionBasicUseCases.getRecipeCaloriesUseCase()
    val recipeCaloriesMap: StateFlow<Map<Long, Float>> =
        _recipeCaloriesMap
            .map { list -> list.associate { it.recipeId to it.totalCalories } }
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyMap())

    //private val _isLoading = MutableStateFlow<Boolean>(true)
    //val isLoading: StateFlow<Boolean> = _isLoading
/*
    init {
        viewModelScope.launch {
            _isLoading.value = true
            try{
                 //val recipeCalories = nutritionBasicUseCases.getRecipeCaloriesUseCase()
                 //_recipeCaloriesMap.value = recipeCalories.map { it.recipeId to it.totalCalories }
            }catch (e: Exception){
                Log.e("ERROR","init mealplanviewmodel failed")
            }finally {
                _isLoading.value = false
            }
        }
    }*/

    fun generateMealPlan(context:Context) {
        viewModelScope.launch {
            try {
                val success = mealPlanGenerator.generateMealPlanDays()
                if(!success){
                    Toast.makeText(context,"You need at least one Recipe to make a Mealplan", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) { Log.e("Error","generateMealPlan failed")}
        }
    }
    fun replaceRecipeFromMealplanDay(mealPlanDayId: Long, replaceRecipeId: Long, replaceWithId: Long) {
        if (replaceRecipeId == replaceWithId) return

        viewModelScope.launch {
            val mealPlanDay = mealPlanUseCases.getMealPlanDayByIdUseCase(mealPlanDayId)

            if (mealPlanDay != null) {
                val recipesWithQuantity = mealPlanRepository.getRecipeQuantityCombined(mealPlanDay.id)

                val isAlreadyPresent = recipesWithQuantity.any { it.recipeId == replaceWithId }

                if (isAlreadyPresent) {
                    Log.e("UpdateError", "Recipe ID $replaceWithId is already in the MealPlanDay.")
                    return@launch
                }

                mealPlanRepository.replaceRecipe(mealPlanDayId,replaceRecipeId,replaceWithId)
            } else {
                Log.e("UpdateError", "MealPlanDay with ID $mealPlanDayId not found.")
            }
        }
    }

    fun deleteMealFromMealplanDay(mealPlanDayId: Long, recipeToDeleteId: Long) {
        viewModelScope.launch {
            val mealPlanDay = mealPlanUseCases.getMealPlanDayByIdUseCase(mealPlanDayId)

            if (mealPlanDay != null) {
                mealPlanRepository.deleteRecipeFromDay(mealPlanDayId,recipeToDeleteId)
            } else {
                Log.e("UpdateError", "MealPlanDay with ID $mealPlanDayId not found.")
            }
        }
    }

    fun getRecipeQuantityCombined(mealPlanDayId: Long){
        viewModelScope.launch {
            mealPlanRepository.getRecipeQuantityCombined(mealPlanDayId)
        }
    }

    fun addMealToMealplanDay(mealPlanDayId: Long, recipeToAddId: Long) {
        viewModelScope.launch {
            val mealPlanDay = mealPlanUseCases.getMealPlanDayByIdUseCase(mealPlanDayId)
            if(mealPlanDay==null) {
                Log.e("UpdateError", "MealPlanDay with ID $mealPlanDayId not found.")
                return@launch
            }

            val recipesWithQuantity = mealPlanRepository.getRecipeQuantityCombined(mealPlanDay.id)

            val isAlreadyPresent = recipesWithQuantity.any { it.recipeId == recipeToAddId }
            if (isAlreadyPresent) {
                return@launch
            }
           mealPlanRepository.addRecipeToDay(mealPlanDayId,recipeToAddId)
        }
    }
    fun updateMealQuantity(mealPlanDayId: Long, recipeToScaleId:Long, newQty:Float) {
        viewModelScope.launch {
            val mealPlanDay = mealPlanUseCases.getMealPlanDayByIdUseCase(mealPlanDayId)
            if (mealPlanDay != null) {
                mealPlanRepository.updateRecipeQuantity(mealPlanDayId,recipeToScaleId,newQty)
            } else {
                Log.e("UpdateError", "MealPlanDay with ID $mealPlanDayId not found.")
            }
        }
    }
    suspend fun getQuantityForRecipeInMealPlan(mealPlanDayId: Long, recipeId: Long): Float? {
            val mealPlanDay = mealPlanUseCases.getMealPlanDayByIdUseCase(mealPlanDayId)
            if (mealPlanDay != null) {
                return mealPlanRepository.getRecipeQuantity(mealPlanDayId,recipeId)
            } else {
                Log.e("Get Quantity Error", "MealPlanDay with ID $mealPlanDayId not found.")
            }
        return null
    }

    suspend fun getRecipeUri(recipeId: Long): Uri? {
        val recipe = recipeUseCase.getRecipeByIdUseCase(recipeId).first()
        return recipe?.imgUri
    }

    fun getRecipeQuantitiesForDay(mealPlanDayId: Long): Flow<List<RecipeQuantity>> =
        mealPlanRepository.getRecipeQuantitiesFlowForDay(mealPlanDayId)

}
