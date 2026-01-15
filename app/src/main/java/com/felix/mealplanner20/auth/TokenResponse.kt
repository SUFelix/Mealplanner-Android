package com.mealplanner20.jwtauthktorandroid.auth

import com.felix.mealplanner20.Meals.Data.helpers.UserRoles

data class TokenResponse(
    val token: String,
    val role: UserRoles
)
