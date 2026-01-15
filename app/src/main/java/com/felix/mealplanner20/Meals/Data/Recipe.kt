package com.felix.mealplanner20.Meals.Data

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.felix.mealplanner20.Meals.Data.DTO.RecipeDTO
import com.felix.mealplanner20.Meals.Data.helpers.Mealtype
import com.google.gson.Gson
import kotlinx.serialization.Serializable

@Entity("recipe_table")
data class Recipe(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo(name = "remote_id")
    val remoteId: Long?= null,
    @ColumnInfo(name = "recipe-name")
    val title: String= "untitled recipe",
    @ColumnInfo(name = "english-recipe-name")
    val englishTitle: String= "untitled recipe",
    @ColumnInfo(name = "german-recipe-name")
    val germanTitle: String= "Unbenanntes Rezept",
    @ColumnInfo(name = "isFavorit")
    val isFavorit: Boolean = false,
    @ColumnInfo(name = "isMeal")
    val isMeal: Boolean = true,
    @ColumnInfo(name = "isSnack")
    val isSnack: Boolean = false,
    @ColumnInfo(name = "isBreakfast")
    val isBreakfast: Boolean = false,
    @ColumnInfo(name = "isBeverage")
    val isBeverage: Boolean = false,
    @ColumnInfo(name = "imgUri")
    val imgUri: Uri? = null,
    @ColumnInfo(name = "probability")
    val probability: Float = 0f,
    @ColumnInfo(name = "isVegan")
    val isVegan: Boolean = false,
    @ColumnInfo(name = "isVegetarian")
    val isVegetarian: Boolean = false,
    @ColumnInfo(name = "createdBy")
    val createdBy: String? = "system",
    @ColumnInfo(name = "servings")
    val servings:Float = 1f,
    @ColumnInfo(name = "cpfRatio")
    val cpfRatio: MacronutrientRatio? = MacronutrientRatio(),
    @ColumnInfo(name = "caloriesPerServing")
    val caloriesPerServing: Float = 1f,
    @ColumnInfo(name = "isDessert")
    val isDessert:Boolean = false
){
     fun toRecipeDTO(): RecipeDTO {
        return RecipeDTO(
            id = this.id,
            title = this.title,
            englishTitle = this.englishTitle,
            germanTitle = this.germanTitle,
            isFavorite = this.isFavorit,
            isMeal = this.isMeal,
            isSnack = this.isSnack,
            isBreakfast = this.isBreakfast,
            isBeverage = this.isBeverage,
            imgUri = this.imgUri?.toString()?:"DefaultURI",
            probability = this.probability,
            isVegan = this.isVegan,
            isVegetarian = this.isVegetarian,
            createdBy = "test",
            servings = this.servings,
            cpfRatio = fromMacronutrientRatio(this.cpfRatio),
            caloriesPerServing = this.caloriesPerServing,
            isDessert = this.isDessert
        )
    }
    private fun fromMacronutrientRatio(value: MacronutrientRatio?): String? {
        val gson = Gson()
        return gson.toJson(value)
    }

    internal fun activeMealtypes(): List<Mealtype> = buildList {
        if (isMeal) add(Mealtype.MEAL)
        if (isBreakfast) add(Mealtype.BREAKFAST)
        if (isSnack) add(Mealtype.SNACK)
        if (isBeverage) add(Mealtype.BEVERAGE)
        if (isDessert) add(Mealtype.DESSERT)
    }
}
@Serializable
data class MacronutrientRatio(
    val carbs: Int =30,
    val protein: Int =30,
    val fat: Int = 40
)





