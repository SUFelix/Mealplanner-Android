package com.felix.mealplanner20.Meals.Data.DTO

import com.felix.mealplanner20.Meals.Data.IngredientWithRecipe
import com.felix.mealplanner20.Meals.Data.helpers.UnitOfMeasure
import kotlinx.serialization.Serializable

@Serializable
data class IngredientWithRecipeDTO(
    val recipeId: Long,
    val ingredientId: Long,
    val ingredientQuantity: Float,
    val unitOfMeasure: String,
    val originalQuantity: Float

) {
    fun toIngredientWithRecipe(): IngredientWithRecipe {
        return IngredientWithRecipe(
            recipeId = this.recipeId,
            ingredientId = this.ingredientId,
            ingredientQuantity = this.ingredientQuantity,
            unitOfMeasure = UnitOfMeasure.valueOf(this.unitOfMeasure),
            originalQuantity = this.originalQuantity,
        )
    }
}

