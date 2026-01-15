package com.felix.mealplanner20.apiService

import com.felix.mealplanner20.Meals.Data.IngredientAllowedUnit
import retrofit2.http.GET

interface IngredientAllowedUnitApiService {

    @GET("ingredients/allowed-units")
    suspend fun getAllAllowedUnits(): List<IngredientAllowedUnit>

}

