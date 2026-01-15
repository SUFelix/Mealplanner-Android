package com.felix.mealplanner20.Meals.Data

import androidx.room.Entity
import androidx.room.ForeignKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "ingredient_allowed_unit",
    primaryKeys = ["ingredientId", "unitOfMeasure"],
    foreignKeys = [
        ForeignKey(
            entity = Ingredient::class,
            parentColumns = ["id"],
            childColumns = ["ingredientId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class IngredientAllowedUnit(
    val ingredientId: Long,
    val unitOfMeasure: String,
    val gramsPerUnit: Float
)
