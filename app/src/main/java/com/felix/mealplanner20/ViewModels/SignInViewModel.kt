package com.felix.mealplanner20.ViewModels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felix.mealplanner20.Meals.Data.EMPTY_STRING
import com.felix.mealplanner20.auth.AuthState
import com.felix.mealplanner20.auth.AuthUiEvent
import com.mealplanner20.jwtauthktorandroid.auth.AuthRepository
import com.mealplanner20.jwtauthktorandroid.auth.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel@Inject constructor(
    private val repository: AuthRepository
): ViewModel()  {

    var state by mutableStateOf(AuthState())

    /*private val resultChannel = Channel<AuthResult<Unit>>()
    val authResults = resultChannel.receiveAsFlow()*/

    private val _authResults = MutableStateFlow<AuthResult<Unit>>(AuthResult.Unauthorized())
    val authResults: StateFlow<AuthResult<Unit>> = _authResults.asStateFlow()

    init {
        authenticate()
        Log.i("SignInVM", "init: $this / hash=${hashCode()}")
    }

    fun onEvent(event: AuthUiEvent) {
        when(event) {
            is AuthUiEvent.SignInUsernameChanged -> {
                state = state.copy(signInEmailOrUsername = event.value)
            }
            is AuthUiEvent.SignInPasswordChanged -> {
                state = state.copy(signInPassword = event.value)
            }
            is AuthUiEvent.SignIn -> {
                signIn()
            }
            is AuthUiEvent.SignUpUsernameChanged -> {
                state = state.copy(signUpUsername = event.value)
            }
            is AuthUiEvent.SignUpEmailChanged -> {
                state = state.copy(signUpEmail = event.value)
            }
            is AuthUiEvent.SignUpPasswordChanged -> {
                state = state.copy(signUpPassword = event.value)
            }
            is AuthUiEvent.SignUp -> {
                signUp()
            }
            is AuthUiEvent.Logout -> {
                logout()
            }

            is AuthUiEvent.ResetPasswordEmailChanged -> {
                state = state.copy(resetPasswordEmail = event.value)
            }
            is AuthUiEvent.ResetPasswordTokenChanged -> {
                state = state.copy(resetPasswordToken = event.value)
            }
            is AuthUiEvent.ResetPasswordNewPasswordChanged -> {
                state = state.copy(resetPasswordNewPassword = event.value)
            }
            is AuthUiEvent.RequestPasswordReset -> {
                requestPasswordReset()
            }
            is AuthUiEvent.ConfirmPasswordReset -> {
                confirmPasswordReset()
            }
        }
    }

    private fun confirmPasswordReset() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            try {
                val result = repository.confirmPasswordReset(token = state.resetPasswordToken, newPassword = state.resetPasswordNewPassword)
                _authResults.value = result
            } catch (e: Exception) {
                _authResults.value = AuthResult.IOError()
            } finally {
                state = state.copy(isLoading = false)
            }
        }
    }

    private fun requestPasswordReset() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            try {
                val result = repository.requestPasswordReset(email = state.resetPasswordEmail)
                _authResults.value = result
            } catch (e: Exception) {
                _authResults.value = AuthResult.IOError()
            } finally {
                state = state.copy(isLoading = false)
            }
        }
    }


    private fun signUp() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            val result = repository.signUp(
                username = state.signUpUsername,
                email =  state.signUpEmail,
                password = state.signUpPassword
            )
            _authResults.value = result
            state = state.copy(isLoading = false)
        }
    }

    private fun signIn() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)

            val result = repository.signIn(
                usernameOrEmail =  state.signInEmailOrUsername,
                password = state.signInPassword
            )
            _authResults.value = result
            state = state.copy(isLoading = false)
        }
    }

    private fun logout() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            val result = repository.logout()
            _authResults.value = result
            state = state.copy(isLoading = false)
        }
    }


    private fun authenticate() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            val result = repository.authenticate()
            _authResults.value = result
            state = state.copy(isLoading = false)
        }
    }

    private val  _email = mutableStateOf<String>( EMPTY_STRING)
    val email: State<String> = _email

    private val  _password = mutableStateOf<String>( EMPTY_STRING)
    val password: State<String> = _password

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }
    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }
}