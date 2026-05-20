package com.felix.mealplanner20.Meals.Data

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.felix.mealplanner20.Meals.Data.helpers.Language

const val CALORIE_REQUIREMENT_DEFAULT_VALUE  = 2000
const val PROTEIN_REQUIREMENT_DEFAULT_VALUE  = 81
const val FAT_REQUIREMENT_DEFAULT_VALUE  = 75
const val MEALS_PER_DAY_DEFAULT_VALUE  = 1
const val BREAKFASTS_PER_DAY_DEFAULT_VALUE  = 1
const val SNACKS_PER_DAY_DEFAULT_VALUE  = 1
const val PLANNING_HORIZON_IN_DAYS_DEFAULT_VALUE = 3
const val EMPTY_STRING = ""

@Entity(tableName = "settings_table")
data class Settings(
    @PrimaryKey
    val id: Int = 0,
    @ColumnInfo(name = "enableDynamicColors")
    val enableDynamicColors: Boolean = true,//TODO ggf wieder enfernen oder nutzbar machen
    @ColumnInfo(name = "mealsPerDay")
    val mealsPerDay: Int = MEALS_PER_DAY_DEFAULT_VALUE,
    @ColumnInfo(name = "breakfastsPerDay")
    val breakfastsPerDay: Int = BREAKFASTS_PER_DAY_DEFAULT_VALUE,
    @ColumnInfo(name = "snacksPerDay")
    val snacksPerDay: Int = SNACKS_PER_DAY_DEFAULT_VALUE,
    @ColumnInfo(name = "planningHorizonInDays")
    val planningHorizonInDays: Int = PLANNING_HORIZON_IN_DAYS_DEFAULT_VALUE,
    @ColumnInfo(name = "vegan")
    val vegan: Boolean = false,
    @ColumnInfo(name = "vegetarian")
    val vegetarian: Boolean = false,
    @ColumnInfo(name = "language")
    val language: Language = Language.ENGLISH,
    @ColumnInfo(name = "calorieRequirement")
    val calorieRequirement: Int = CALORIE_REQUIREMENT_DEFAULT_VALUE,
    @ColumnInfo(name = "proteinRequirement")
    val proteinRequirement: Int = PROTEIN_REQUIREMENT_DEFAULT_VALUE,
    @ColumnInfo(name = "fatRequirement")
    val fatRequirement: Int = FAT_REQUIREMENT_DEFAULT_VALUE,
    @ColumnInfo(name = "profilePictureLocalUri")
    val profilePictureLocalUri: Uri? = null,
    @ColumnInfo(name = "showOriginalTitle")
    val showOriginalTitle: Boolean = false,
    @ColumnInfo(name = "hasSeenOnboarding")
    val hasSeenOnboarding: Boolean = false
)

 fun defaultSettings(): Settings {
    return Settings(
        planningHorizonInDays = PLANNING_HORIZON_IN_DAYS_DEFAULT_VALUE,
        mealsPerDay = MEALS_PER_DAY_DEFAULT_VALUE,
        breakfastsPerDay = BREAKFASTS_PER_DAY_DEFAULT_VALUE,
        snacksPerDay = SNACKS_PER_DAY_DEFAULT_VALUE,
        vegan = false,
        vegetarian = false,
        language = Language.ENGLISH,
        calorieRequirement = CALORIE_REQUIREMENT_DEFAULT_VALUE,
        proteinRequirement = PROTEIN_REQUIREMENT_DEFAULT_VALUE,
        fatRequirement = FAT_REQUIREMENT_DEFAULT_VALUE
    )
}

