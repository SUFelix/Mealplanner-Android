package com.felix.mealplanner20.Meals.Data.DTO

import kotlinx.serialization.Serializable

@Serializable
data class IngredientWithoutRecipeIdDTO(
    val ingredientId: Long,
    val ingredientQuantity: Float,
    val unitOfMeasure: String,
    val originalQuantity: Float,
)
