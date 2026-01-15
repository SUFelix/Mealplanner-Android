package com.felix.mealplanner20.Meals.Data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.felix.mealplanner20.Meals.Data.helpers.UnitOfMeasure
import com.felix.mealplanner20.Meals.Data.helpers.dgeGroup

@Entity(tableName = "ingredient_table")
data class Ingredient(
    @PrimaryKey(autoGenerate = true)
    val id:Long = 0L,
    @ColumnInfo(name = "ingredient-name")
    val germanName: String,
    @ColumnInfo(name = "englishName")
    val englishName: String?,
    @ColumnInfo(name = "ingredient-calories")
    val calories: Float =65.0f,
    @ColumnInfo(name = "ingredient-fat")
    val fat: Float = 3.5f,
    @ColumnInfo(name = "ingredient-saturatedFat")
    val saturatedFat: Float = 2.3f,
    @ColumnInfo(name = "ingredient-carbs")
    val carbs: Float= 4.7f,
    @ColumnInfo(name = "ingredient-sugar")
    val sugar: Float = 4.7f,
    @ColumnInfo(name = "ingredient-protein")
    val protein: Float = 3.0f,
    @ColumnInfo(name = "ingredient-fibre")
    val fibre: Float = 0.0f,
    @ColumnInfo(name = "ingredient-dgeType")
    val dgeType: dgeGroup = dgeGroup.MILK,
    @ColumnInfo(name = "ingredient-alcohol")
    val alcohol:Float = 0.0f,
    @ColumnInfo(name = "isFavorit")
    val isFavorit: Boolean = false,
    @ColumnInfo(name = "ingredient_unitOfMeasure")
    val unitOfMeasure: UnitOfMeasure = UnitOfMeasure.GRAM
    ){

   fun isVegan():Boolean{
       return when(dgeType){
           dgeGroup.MILK ->  false
           dgeGroup.EGG -> false
           dgeGroup.FISH -> false
           dgeGroup.MEAT -> false
           dgeGroup.OTHER -> false
           dgeGroup.OTHERVEGETARIAN -> false
           else -> true
       }
    }
    fun isVegetarian():Boolean{
        return when(dgeType){
            dgeGroup.FISH -> false
            dgeGroup.MEAT -> false
            dgeGroup.OTHER -> false
            else -> true
        }
    }
}


