package com.felix.mealplanner20.auth

import com.felix.mealplanner20.Meals.Data.EMPTY_STRING

data class AuthState(
    val isLoading: Boolean = false,
    val signUpEmail: String = EMPTY_STRING,
    val signUpUsername: String = EMPTY_STRING,
    val signUpPassword: String = EMPTY_STRING,
    val signInEmailOrUsername: String = EMPTY_STRING,
    val signInPassword: String = EMPTY_STRING,

    val resetPasswordEmail: String = EMPTY_STRING,

    val resetPasswordToken: String = EMPTY_STRING,
    val resetPasswordNewPassword: String = EMPTY_STRING
)
