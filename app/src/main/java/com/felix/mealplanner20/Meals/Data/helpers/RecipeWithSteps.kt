package com.felix.mealplanner20.Meals.Data.helpers

import androidx.room.Embedded
import androidx.room.Relation
import com.felix.mealplanner20.Meals.Data.Recipe
import com.felix.mealplanner20.Meals.Data.RecipeDescription

data class RecipeWithSteps(
    @Embedded val recipe: Recipe,
    @Relation(
        parentColumn = "id",
        entityColumn = "recipeId"
    )
    val steps: List<RecipeDescription>
)

