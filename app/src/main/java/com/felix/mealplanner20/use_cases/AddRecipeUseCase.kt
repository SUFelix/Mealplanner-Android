package com.felix.mealplanner20.use_cases

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import com.felix.mealplanner20.Meals.Data.Recipe
import com.felix.mealplanner20.Meals.Data.RecipeRepository
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

const val DEFAULT_NEW_PROBABILITY_BEFORE_NORMALIZATION = 0.1f
const val DEFAULT_NEW_WEIGHT = 0.1f

class AddRecipeUseCase(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(recipe: Recipe):Long {

        val newRecipeId =  repository.addRecipe(recipe)

        recipe.activeMealtypes().forEach { mealType ->
            repository.updateWeight(newRecipeId, mealType, DEFAULT_NEW_WEIGHT)
        }

        //TODO die gesamte Logik, die die Ingredients für ein rezept speicert sollte auch hier sein, viewmodel  muss schlanker werden

        return newRecipeId
    }
}













