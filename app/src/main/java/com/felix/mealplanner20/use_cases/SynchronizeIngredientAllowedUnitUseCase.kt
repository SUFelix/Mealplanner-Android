package com.felix.mealplanner20.use_cases

import android.util.Log
import com.felix.mealplanner20.Meals.Data.IngredientAllowedUnit
import com.felix.mealplanner20.Meals.Data.IngredientAllowedUnitDao
import com.felix.mealplanner20.apiService.IngredientAllowedUnitApiService
import javax.inject.Inject

class SynchronizeIngredientAllowedUnitUseCase @Inject constructor(
    private val ingredientAllowedUnitDao: IngredientAllowedUnitDao,
    private val ingredientAllowedUnitApiService:IngredientAllowedUnitApiService
) {
    suspend operator fun invoke(): Boolean {
       return synchronizeIngredientAllowedUnits()
    }

    suspend fun synchronizeIngredientAllowedUnits(): Boolean {
        return try {
            val apiAllowedUnits = ingredientAllowedUnitApiService.getAllAllowedUnits()

            val localAllowedUnits = ingredientAllowedUnitDao.getAllAllowedUnits()

            val toInsert = mutableListOf<IngredientAllowedUnit>()
            val toUpdate = mutableListOf<IngredientAllowedUnit>()

            for (apiUnit in apiAllowedUnits) {
                val localUnit = localAllowedUnits.find {
                    it.ingredientId == apiUnit.ingredientId &&
                            it.unitOfMeasure == apiUnit.unitOfMeasure
                }

                if (localUnit == null) toInsert.add(apiUnit) else if (localUnit != apiUnit) {
                    toUpdate.add(apiUnit)
                }
            }

            if (toInsert.isNotEmpty()) {
                ingredientAllowedUnitDao.insertAllowedUnits(toInsert)
            }

            if (toUpdate.isNotEmpty()) {
                toUpdate.forEach { unit ->
                    ingredientAllowedUnitDao.updateAllowedUnit(unit)
                }
            }

            true
        } catch (e: Exception) {
            Log.e("IngredientRepository", "Error syncing allowed units", e)
            false
        }
    }


}