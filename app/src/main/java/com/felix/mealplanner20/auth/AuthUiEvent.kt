package com.felix.mealplanner20.auth

sealed class AuthUiEvent {
    data class SignUpUsernameChanged(val value: String): AuthUiEvent()
    data class SignUpEmailChanged(val value: String): AuthUiEvent()
    data class SignUpPasswordChanged(val value: String): AuthUiEvent()
    object SignUp: AuthUiEvent()
    data class SignInUsernameChanged(val value: String): AuthUiEvent()
    data class SignInPasswordChanged(val value: String): AuthUiEvent()
    object SignIn: AuthUiEvent()
    object Logout: AuthUiEvent()
    data class ResetPasswordEmailChanged(val value: String) : AuthUiEvent()
    object RequestPasswordReset : AuthUiEvent()

    data class ResetPasswordTokenChanged(val value: String) : AuthUiEvent()
    data class ResetPasswordNewPasswordChanged(val value: String) : AuthUiEvent()
    object ConfirmPasswordReset : AuthUiEvent()
}
