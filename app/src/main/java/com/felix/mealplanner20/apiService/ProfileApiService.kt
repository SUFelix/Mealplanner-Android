package com.felix.mealplanner20.apiService

import com.felix.mealplanner20.Meals.Data.DTO.RecipeWithIngredientsWithoutRecipeIdDTO
import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.POST

interface ProfileApiService {
    @POST("profile/description")
    suspend fun postDescription(
        @Body newDescription: String,
        @HeaderMap headers: Map<String, String>
    ): Response<Unit>

    @POST("profile/uri")
    suspend fun postNewImageUri(
        @Body newUri: ImageUriRequest,
        @HeaderMap headers: Map<String, String>
    ): Response<Unit>

    @GET("profile/description")
    suspend fun getOwnDescription(
        @HeaderMap headers: Map<String, String>
    ): String?

    @GET("profile/email")
    suspend fun getOwnEmail(
        @HeaderMap headers: Map<String, String>
    ): EmailResponse?

}
@Serializable
data class ImageUriRequest(val uri: String)

@Serializable
data class EmailResponse(val email: String)
