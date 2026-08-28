package com.felix.mealplanner20.apiService

import com.felix.mealplanner20.Meals.Data.DTO.IngredientDTO
import com.felix.mealplanner20.Meals.Data.DTO.IngredientMatchReviewDTO
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

    @GET("ingredients/{id}")
    suspend fun getIngredientById(@Path("id") id: Long): IngredientDTO

    @GET("ingredients/review")
    suspend fun getPendingIngredients(@HeaderMap headers: Map<String, String>): List<IngredientDTO>

    @POST("ingredients/review/{id}/approve")
    suspend fun approveIngredient(
        @Path("id") id: Long,
        @HeaderMap headers: Map<String, String>
    ): Response<Unit>

    @POST("ingredients/review/{id}/reject")
    suspend fun rejectIngredient(
        @Path("id") id: Long,
        @HeaderMap headers: Map<String, String>
    ): Response<Unit>

    @GET("ingredients/review/matches")
    suspend fun getPendingMatches(@HeaderMap headers: Map<String, String>): List<IngredientMatchReviewDTO>

    @POST("ingredients/review/matches/{taskId}/confirm")
    suspend fun confirmMatch(
        @Path("taskId") taskId: Long,
        @HeaderMap headers: Map<String, String>
    ): Response<Unit>

    @POST("ingredients/review/matches/{taskId}/reject")
    suspend fun rejectMatch(
        @Path("taskId") taskId: Long,
        @HeaderMap headers: Map<String, String>
    ): Response<Unit>
}