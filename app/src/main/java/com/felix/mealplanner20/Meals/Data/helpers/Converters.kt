package com.felix.mealplanner20.Meals.Data.helpers

import android.net.Uri
import androidx.room.TypeConverter
import com.felix.mealplanner20.Meals.Data.MacronutrientRatio
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromUnitOfMeasure(unit: UnitOfMeasure): String {
        return unit.name
    }

    @TypeConverter
    fun toUnitOfMeasure(unit: String): UnitOfMeasure {
        return UnitOfMeasure.valueOf(unit)
    }

    @TypeConverter
    fun fromListOfLongs(value: List<Long>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toListOfLongs(value: String): List<Long> {
        val listType = object : TypeToken<List<Long>>() {}.type
        return Gson().fromJson(value, listType)
    }
    @TypeConverter
    fun fromRecipeQuantityList(value: List<RecipeQuantity>): String {
        val gson = Gson()
        val type = object : TypeToken<List<RecipeQuantity>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun fromMealtype(type: Mealtype): String = type.name
    @TypeConverter
    fun toMealtype(value: String): Mealtype = Mealtype.valueOf(value)

    @TypeConverter
    fun toRecipeQuantityList(value: String): List<RecipeQuantity> {
        val gson = Gson()
        val type = object : TypeToken<List<RecipeQuantity>>() {}.type
        return gson.fromJson(value, type)
    }
    @TypeConverter
    fun fromLanguage(language: Language): String {
        return language.name
    }

    @TypeConverter
    fun toLanguage(language: String): Language {
        return Language.valueOf(language)
    }
    @TypeConverter
    fun fromUri(uri: Uri?): String? {
        return uri?.toString()
    }

    @TypeConverter
    fun toUri(uriString: String?): Uri? {
        return uriString?.let { Uri.parse(it) }
    }

    @TypeConverter
    fun fromMacronutrientRatio(value: MacronutrientRatio?): String? {
        val gson = Gson()
        return gson.toJson(value)
    }

    @TypeConverter
    fun toMacronutrientRatio(value: String?): MacronutrientRatio? {
        val gson = Gson()
        val type = object : TypeToken<MacronutrientRatio>() {}.type
        return gson.fromJson(value, type)
    }
    }



