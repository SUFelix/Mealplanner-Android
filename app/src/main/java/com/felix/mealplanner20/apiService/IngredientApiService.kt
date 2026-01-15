package com.felix.mealplanner20.apiService

import com.felix.mealplanner20.Meals.Data.DTO.IngredientDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface IngredientApiService {
    @GET("ingredients")
    suspend fun getAllIngredients(): List<IngredientDTO>

    @POST("ingredients")
    suspend fun postIngredients(
        @Body ingredient: IngredientDTO,
        @HeaderMap headers: Map<String, String>
    ): Response<Unit>

    @PUT("ingredients/{id}")
    suspend fun putIngredients(
        @Path("id") id: Long,
        @Body ingredient: IngredientDTO,
        @HeaderMap headers: Map<String, String>
    ): Response<Unit>
}