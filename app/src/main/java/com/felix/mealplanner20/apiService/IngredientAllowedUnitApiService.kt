package com.felix.mealplanner20.apiService

import com.felix.mealplanner20.Meals.Data.DTO.IngredientAllowedUnitReviewDTO
import com.felix.mealplanner20.Meals.Data.IngredientAllowedUnit
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.Path

interface IngredientAllowedUnitApiService {

    @GET("ingredients/allowed-units")
    suspend fun getAllAllowedUnits(): List<IngredientAllowedUnit>

    @GET("ingredients/allowed-units/review")
    suspend fun getPendingAllowedUnits(@HeaderMap headers: Map<String, String>): List<IngredientAllowedUnitReviewDTO>

    @POST("ingredients/allowed-units/review/{ingredientId}/{unit}/approve")
    suspend fun approveAllowedUnit(
        @Path("ingredientId") ingredientId: Long,
        @Path("unit") unit: String,
        @HeaderMap headers: Map<String, String>
    ): Response<Unit>

    @POST("ingredients/allowed-units/review/{ingredientId}/{unit}/reject")
    suspend fun rejectAllowedUnit(
        @Path("ingredientId") ingredientId: Long,
        @Path("unit") unit: String,
        @HeaderMap headers: Map<String, String>
    ): Response<Unit>

}
