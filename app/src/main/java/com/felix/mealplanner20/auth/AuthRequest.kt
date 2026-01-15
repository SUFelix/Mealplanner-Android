package com.mealplanner20.jwtauthktorandroid.auth

import com.squareup.moshi.Json

data class AuthRequest(
    @Json(name = "usernameOrEmail") val username: String,
    @Json(name = "password") val password: String
)

