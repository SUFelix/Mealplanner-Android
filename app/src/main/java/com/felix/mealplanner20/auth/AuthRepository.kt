package com.mealplanner20.jwtauthktorandroid.auth

interface AuthRepository {
    suspend fun signUp(username: String,email:String, password: String): AuthResult<Unit>
    suspend fun signIn(usernameOrEmail: String, password: String): AuthResult<Unit>
    suspend fun authenticate(): AuthResult<Unit>
    suspend fun logout(): AuthResult<Unit>

    suspend fun getToken(): String?

    suspend fun requestPasswordReset(email: String): AuthResult<Unit>
    suspend fun confirmPasswordReset(token: String, newPassword: String): AuthResult<Unit>

    suspend fun getUsernameClaim(): String?
}