package com.felix.mealplanner20.Meals.Data

import androidx.room.Entity
import androidx.room.ForeignKey
import com.felix.mealplanner20.Meals.Data.helpers.UnitOfMeasure

@Entity(
    tableName = "ingredient_recipe_join_table",
    primaryKeys = ["recipeId", "ingredientId"],
    foreignKeys = [
        ForeignKey(
            entity = Recipe::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Ingredient::class,
            parentColumns = ["id"],
            childColumns = ["ingredientId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class IngredientWithRecipe(
    val recipeId: Long = 0L,
    val ingredientId: Long = 0L,
    val ingredientQuantity: Float = 1f,
    val unitOfMeasure: UnitOfMeasure = UnitOfMeasure.PIECE,
    val originalQuantity: Float = 1f,
)
