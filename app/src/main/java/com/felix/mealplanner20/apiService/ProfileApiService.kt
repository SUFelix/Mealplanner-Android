package com.felix.mealplanner20.apiService

import com.felix.mealplanner20.Meals.Data.DTO.RecipeWithIngredientsWithoutRecipeIdDTO
import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.Path

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

    @GET("users/{username}")
    suspend fun getPublicProfile(
        @Path("username") username: String
    ): PublicProfileDTO?

}
@Serializable
data class ImageUriRequest(val uri: String)

@Serializable
data class EmailResponse(val email: String)

@Serializable
data class PublicProfileDTO(
    val username: String,
    val pictureUri: String?,
    val description: String?,
    val recipeIds: List<Long>,
    val recipeCount: Int
)
