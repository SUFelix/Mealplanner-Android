package com.felix.mealplanner20.Meals.Data.helpers

data class ShoppingListDisplayItem(
    val ingredientId: Long,
    val name: String,
    val quantity: Float,
    val unitOfMeasure: UnitOfMeasure
)

