package com.felix.mealplanner20.apiService

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import java.util.UUID

interface FeedbackApiService {
    @POST("feedback")
    suspend fun sendFeedback(
        @Body feedback: FeedbackDTO,
        @Header("Authorization") authHeader: String
    ): Response<Unit>
}


@Serializable
data class FeedbackDTO(
    val message: String
)
