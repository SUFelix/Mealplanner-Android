package com.felix.mealplanner20.ViewModels

import android.util.Log
import com.felix.mealplanner20.apiService.FeedbackApiService
import com.felix.mealplanner20.apiService.FeedbackDTO
import java.time.Instant


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mealplanner20.jwtauthktorandroid.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

val MAX_MESSAGE_LENGTH=2000

@HiltViewModel
class FeedbackViewModel @Inject constructor(
    private val feedbackApiService: FeedbackApiService,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _status = MutableStateFlow<FeedbackStatus>(FeedbackStatus.Idle)
    val status: StateFlow<FeedbackStatus> = _status

    fun sendFeedback(message: String) {
        Log.d("DEBUG", "message: $message")

        // Lokale Validierung
        if (message.isBlank()) {
            _status.value = FeedbackStatus.Error(FeedbackError.Validation)
            return
        }
        if (message.length > MAX_MESSAGE_LENGTH) {
            _status.value = FeedbackStatus.Error(FeedbackError.PayloadTooLarge)
            return
        }

        viewModelScope.launch {
            try {
                // Auth vorher prüfen (nicht in Sending hängen bleiben)
                val token = authRepository.getToken()
                if (token == null) {
                    Log.d("TOKEN", "IST NULL")
                    _status.value = FeedbackStatus.Error(FeedbackError.NotAuthenticated)
                    return@launch
                }

                _status.value = FeedbackStatus.Sending

                val dto = FeedbackDTO(message = message)
                val response = feedbackApiService.sendFeedback(dto, "Bearer $token")

                if (response.isSuccessful) {
                    _status.value = FeedbackStatus.Success
                } else {
                    _status.value = FeedbackStatus.Error(mapHttpError(response.code()))
                }
            } catch (e: Exception) {
                _status.value = FeedbackStatus.Error(mapException(e))
            }
        }
    }

    fun resetStatus() {
        _status.value = FeedbackStatus.Idle
    }
}
private fun mapHttpError(code: Int): FeedbackError {
    return when (code) {
        400, 422 -> FeedbackError.Validation
        401 -> FeedbackError.NotAuthenticated
        403 -> FeedbackError.Forbidden
        404 -> FeedbackError.NotFound
        413 -> FeedbackError.PayloadTooLarge
        429 -> FeedbackError.RateLimited
        in 500..599 -> FeedbackError.ServerError
        else -> FeedbackError.Unknown("HTTP $code")
    }
}
private fun mapException(t: Throwable): FeedbackError {
    return when (t) {
        is SocketTimeoutException -> FeedbackError.Timeout
        is UnknownHostException, is ConnectException -> FeedbackError.NetworkUnavailable
        is IOException -> FeedbackError.NetworkUnavailable
        else -> FeedbackError.Unknown(t.message)
    }
}

sealed class FeedbackStatus {
    object Idle : FeedbackStatus()
    object Sending : FeedbackStatus()
    object Success : FeedbackStatus()
    data class Error(val error: FeedbackError) : FeedbackStatus()
}

sealed class FeedbackError {
    object NotAuthenticated : FeedbackError()
    object Forbidden : FeedbackError()
    object NetworkUnavailable : FeedbackError()
    object Timeout : FeedbackError()
    object RateLimited : FeedbackError()
    object PayloadTooLarge : FeedbackError() // lokal (zu lang) oder 413 vom Server
    object Validation : FeedbackError() // leer/ungültig (400/422)
    object NotFound : FeedbackError()
    object ServerError : FeedbackError()
    data class Unknown(val message: String?) : FeedbackError()
}