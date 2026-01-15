package com.felix.mealplanner20.Meals.Data.DTO

import com.felix.mealplanner20.Meals.Data.RecipeDescription
import kotlinx.serialization.Serializable

@Serializable
data class RecipeWithIngredientsDTO(
    val recipe: RecipeDTO,
    val ingredients: List<IngredientWithRecipeDTO>
)

@Serializable
data class RecipeWithIngredientsWithoutRecipeIdDTO(
    val recipe: RecipeDTO,
    val ingredients: List<IngredientWithoutRecipeIdDTO>
)

@Serializable
data class RecipeFullDTO(
    val recipe: RecipeDTO,
    val ingredients: List<IngredientWithoutRecipeIdDTO>,
    val steps: List<RecipeDescription>
)

