package com.felix.mealplanner20.use_cases

data class NutritionBasicUseCases(
    val getRecipeCaloriesUseCase: GetRecipeCaloriesUseCase,
    val getMacroNutrientRecommendationsUseCase: GetMacroNutrientRecomendationsUseCase,
    val getDgeRecommendationDataUseCase: GetDgeRecommendationDataUseCase,
    val calculateCaloriesUseCase: CalculateCaloriesUseCase
)
