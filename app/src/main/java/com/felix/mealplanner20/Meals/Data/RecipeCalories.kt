package com.felix.mealplanner20.Meals.Data

import androidx.room.ColumnInfo

data class RecipeCalories(
    @ColumnInfo(name = "recipe_id")
    val recipeId: Long,
    @ColumnInfo(name = "recipe_name")
    val recipeName: String,
    @ColumnInfo(name = "total_calories")
    val totalCalories: Float
)
