package com.felix.mealplanner20.use_cases

import android.net.Uri
import com.felix.mealplanner20.Meals.Data.MacronutrientRatio
import com.felix.mealplanner20.Meals.Data.RecipeRepository

class UpdateRecipeMainTable(
    private val recipeRepository: RecipeRepository
) {
    suspend operator fun invoke(
        recipeId: Long,
        newTitle: String,
        newIsMeal:Boolean,
        newIsBreakfast:Boolean,
        newIsSnack:Boolean,
        newIsBeverage:Boolean,
        newIsDessert:Boolean,
        isVegan:Boolean,
        isVegetarian:Boolean,
        newUri: Uri?,
        newCreatedBy:String?,
        newServings:Float,
        newCPFratio:MacronutrientRatio,
        newCaloriesPerserving:Float
    ) {
        recipeRepository.updateRecipeMainTable(recipeId, newTitle,newIsMeal,newIsBreakfast,newIsSnack,newIsBeverage,newIsDessert,isVegan,isVegetarian,newUri,newCreatedBy,newServings,newCPFratio,newCaloriesPerserving)
    }
}