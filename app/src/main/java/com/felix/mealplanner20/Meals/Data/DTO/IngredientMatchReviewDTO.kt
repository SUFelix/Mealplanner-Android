package com.felix.mealplanner20.Meals.Data.DTO

data class IngredientMatchReviewDTO(
    val taskId: Long,
    val extractedName: String,
    val originalText: String?,
    val recipeTitle: String?,
    val confidence: Float,
    val matches: List<MatchSuggestionDTO> = emptyList(),
    val reasoning: String?
)

data class MatchSuggestionDTO(
    val ingredientId: Long,
    val matchedText: String
)
