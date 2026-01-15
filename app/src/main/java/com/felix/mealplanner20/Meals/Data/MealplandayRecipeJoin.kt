package com.felix.mealplanner20.Meals.Data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "mealplanday_recipe_table",
    primaryKeys = ["mealPlanDayId", "recipeId"],
    foreignKeys = [
        ForeignKey(
            entity = MealPlanDay::class,
            parentColumns = ["id"],
            childColumns = ["mealPlanDayId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Recipe::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["mealPlanDayId"]),
        Index(value = ["recipeId"])
    ]
)
data class MealPlanDayRecipeEntity(
    val mealPlanDayId: Long,
    val recipeId: Long,
    val quantity: Float
)
