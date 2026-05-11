package com.felix.mealplanner20.apiService

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.HeaderMap
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import kotlinx.serialization.Serializable


interface WizardApiService {

    @Multipart
    @POST("wizard/analyze")
    suspend fun analyzeRecipeImage(
        @Part image: MultipartBody.Part,
        @HeaderMap headers: Map<String, String>
    ): Response<WizardIngredientList>
}


@Serializable
data class WizardIngredientList(
    val ingredients: List<WizardIngredient>,
    val recipeTitle: String? = null
)

@Serializable
data class WizardIngredient(
    val name: String,
    val amount: Double? = null,
    val unit: String? = null,
    val originalText: String,
    val matchedIngredientId: Long? = null
)