package com.mealplanner20.jwtauthktorandroid.auth

import com.squareup.moshi.Json

data class SignUpRequest(
    @Json(name = "username") val username: String,
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String
)