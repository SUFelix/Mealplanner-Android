package com.felix.mealplanner20.use_cases

import com.felix.mealplanner20.Meals.Data.Ingredient
import com.felix.mealplanner20.Meals.Data.helpers.DayDetailData
import com.felix.mealplanner20.Meals.Data.helpers.getAdjustedQuantity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CalculateCaloriesUseCase {
    fun calculateCaloriesAvg(dayDetailsFlow: Flow<List<DayDetailData>>): Flow<Float> {
        return calculateCaloriesPerDay(dayDetailsFlow).map { caloriesPerDayList ->
            if (caloriesPerDayList.isEmpty()) {
                return@map 0f
            }

            val totalCalories = caloriesPerDayList.sum()
            val averageCalories = totalCalories / caloriesPerDayList.size
            averageCalories
        }
    }

    fun calculateCaloriesPerDay(dayDetails: Flow<List<DayDetailData>>): Flow<List<Float>> {
        return dayDetails.map { details ->
            if (details.isEmpty()) {
                return@map listOf(0f)
            }

            details.map { detail ->
                val protein = detail.nutrients[PROTEIN] ?: 0f
                val carbs = detail.nutrients[CARBS] ?: 0f
                val fat = detail.nutrients[FAT] ?: 0f
                val alcohol = detail.nutrients[ALCOHOL] ?: 0f

                val caloriesFromProtein = protein * PROTEIN_CALORIES
                val caloriesFromCarbs = carbs * CARBS_CALORIES
                val caloriesFromFat = fat * FAT_CALORIES
                val caloriesFromAlcohol = alcohol * ALCOHOL_CALORIES

                val totalDayCalories = caloriesFromProtein + caloriesFromCarbs + caloriesFromFat + caloriesFromAlcohol
                totalDayCalories
            }
        }
    }

    fun calculateCaloriesForIngredientQtyPair(ingredientsWithQuantities: Pair<Ingredient, Float>): Float {
        val ingredient = ingredientsWithQuantities.first
        val quantity = ingredientsWithQuantities.second
        val adjustedQuantity = getAdjustedQuantity(ingredient, quantity)

        return (
                ingredient.protein * PROTEIN_CALORIES
                        + ingredient.carbs * CARBS_CALORIES +
                        ingredient.fat * FAT_CALORIES +
                        ingredient.alcohol * ALCOHOL_CALORIES) * adjustedQuantity
    }
}