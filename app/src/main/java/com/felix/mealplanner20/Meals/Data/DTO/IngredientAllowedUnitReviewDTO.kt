package com.felix.mealplanner20.Meals.Data.DTO

data class IngredientAllowedUnitReviewDTO(
    val ingredientId: Long,
    val unitOfMeasure: String,
    val gramsPerUnit: Float,
    val status: String
)
