package com.felix.mealplanner20.Meals.Data.DTO

import android.net.Uri
import com.felix.mealplanner20.Meals.Data.MacronutrientRatio
import com.felix.mealplanner20.Meals.Data.Recipe
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.serialization.Serializable

@Serializable
data class RecipeDTO(
    val id: Long?,
    val title: String,
    val englishTitle: String,
    val germanTitle: String,
    val isFavorite: Boolean,
    val isMeal: Boolean,
    val isSnack: Boolean,
    val isBreakfast: Boolean,
    val isBeverage: Boolean,
    val imgUri: String?,
    val probability: Float,
    val isVegan: Boolean,
    val isVegetarian: Boolean,
    val createdBy:String? = null,
    val servings:Float,
    val cpfRatio: String?,
    val caloriesPerServing:Float,
    val isDessert:Boolean = false
){
    public fun toRecipe(): Recipe {
        return Recipe(
            id = this.id?: 0L,
            title = this.title,
            englishTitle = this.englishTitle,
            germanTitle = this.germanTitle,
            isFavorit = this.isFavorite,
            isMeal = this.isMeal,
            isSnack = this.isSnack,
            isBreakfast = this.isBreakfast,
            isBeverage = this.isBeverage,
            imgUri = this.imgUri?.let { Uri.parse(it) },
            probability = this.probability,
            isVegan = this.isVegan,
            isVegetarian = this.isVegetarian,
            createdBy = this.createdBy,
            servings = this.servings,
            cpfRatio = toMacronutrientRatio(this.cpfRatio),
            caloriesPerServing = this.caloriesPerServing,
            isDessert = this.isDessert
        )
    }
    private fun toMacronutrientRatio(value: String?): MacronutrientRatio? {
        val gson = Gson()
        val type = object : TypeToken<MacronutrientRatio>() {}.type
        return gson.fromJson(value, type)
    }
}
