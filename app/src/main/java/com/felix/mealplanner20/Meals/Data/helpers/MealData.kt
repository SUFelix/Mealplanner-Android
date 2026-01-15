package com.felix.mealplanner20.Meals.Data.helpers

import com.felix.mealplanner20.Meals.Data.Recipe
import com.felix.mealplanner20.Meals.Data.Settings

data class MealData(
    val allRecipes: List<Recipe>,
    val allBreakfasts: List<Recipe>,
    val allSnacks: List<Recipe>,
    val allMeals: List<Recipe>,
    val settings: Settings
)

