package com.felix.mealplanner20.Meals.Data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.felix.mealplanner20.Meals.Data.helpers.Mealtype

@Entity(
    tableName = "recipe_mealtype_weight",
    primaryKeys = ["recipe_id", "meal_type"],
    foreignKeys = [
        ForeignKey(
            entity = Recipe::class,
            parentColumns = ["id"],
            childColumns = ["recipe_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION
        )
    ],
    indices = [
        Index(value = ["recipe_id"]),
        Index(value = ["meal_type"])
    ]
)
data class RecipeMealTypeWeight(
    @ColumnInfo(name = "recipe_id") val recipeId: Long,
    @ColumnInfo(name = "meal_type") val mealType: Mealtype,
    @ColumnInfo(name = "weight") val weight: Float
)