package com.felix.mealplanner20.Meals.Data.DTO

import kotlinx.serialization.Serializable

@Serializable
data class IngredientDTO(
    val id:Long,
    val germanName: String,
    val englishName: String?,
    val calories: Float,
    val fat: Float,
    val saturatedFat: Float,
    val carbs: Float,
    val sugar: Float,
    val protein: Float,
    val fibre: Float,
    val dgeType: String,
    val alcohol: Float,
    val isFavorit: Boolean,
    val unitOfMeasure: String
)

