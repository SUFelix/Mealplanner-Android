package com.felix.mealplanner20.Meals.Data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface IngredientAllowedUnitDao {

    @Query("SELECT * FROM ingredient_allowed_unit WHERE ingredientId = :ingredientId")
    suspend fun getAllowedUnitsForIngredient(ingredientId: Long): List<IngredientAllowedUnit>


    @Query("SELECT * FROM ingredient_allowed_unit WHERE ingredientId = :ingredientId AND unitOfMeasure = :unitOfMeasure")
    suspend fun getUnitForIngredient(ingredientId: Long, unitOfMeasure: String): IngredientAllowedUnit?

    @Query("SELECT * FROM ingredient_allowed_unit")
    suspend fun getAllAllowedUnits(): List<IngredientAllowedUnit>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllowedUnits(units: List<IngredientAllowedUnit>)

    @Update
    suspend fun updateAllowedUnit(unit: IngredientAllowedUnit)
}

