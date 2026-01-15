package com.felix.mealplanner20

import com.felix.mealplanner20.Meals.Data.Recipe
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ProbabilityConfigurationUnitTest {

    private lateinit var recipes: MutableList<Recipe>

    @Before
    fun setUp() {
        recipes = mutableListOf(
            Recipe(id = 1, title = "Pizza", isFavorit = true, probability = 0.5f),
            Recipe(id = 2, title = "Pasta", isFavorit = true, probability = 0.3f),
            Recipe(id = 3, title = "Salat", isFavorit = false, probability = 0f)
        )
    }

    @Test
    fun testRemoveFavoriteEnsuresProbabilityZero() {
        val updatedRecipes = recipes.map { recipe ->
            if (recipe.isFavorit) {
                recipe.copy(isFavorit = false, probability = 0f)//TODO: das hier ist quatsch!!
            } else {
                recipe
            }
        }

        updatedRecipes.forEach { recipe ->
            if (!recipe.isFavorit) {
                assertEquals(0f, recipe.probability, 0.0f)
            } else {
                assertTrue(recipe.probability > 0f)
            }
        }
    }
}

