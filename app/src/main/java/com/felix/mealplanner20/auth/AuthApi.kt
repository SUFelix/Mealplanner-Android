package com.mealplanner20.jwtauthktorandroid.auth

import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApi {

    @POST("signup")
    suspend fun signUp(
        @Body request: SignUpRequest
    )

    @POST("signin")
    suspend fun signIn(
        @Body request: AuthRequest
    ): TokenResponse

    @GET("authenticate")
    suspend fun authenticate(
        @Header("Authorization") token: String
    )

    @POST("request-password-reset")
    suspend fun requestPasswordReset( @Body request: PasswordResetRequestEmail)

    @POST("reset-password")
    suspend fun confirmPasswordReset( @Body request: PasswordResetRequest)
}

@Serializable
data class PasswordResetRequestEmail(val email: String)

@Serializable
data class PasswordResetRequest(
    val token: String,
    val newPassword: String
)