package com.felix.mealplanner20.apiService

import com.mealplanner20.jwtauthktorandroid.auth.AuthRepository
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CancellationException
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

sealed interface WizardResponse {
    data class Success(val result: WizardIngredientList) : WizardResponse
    data object EmptyBody : WizardResponse
    data object NotLoggedIn : WizardResponse

    sealed interface HttpFailure : WizardResponse {
        val code: Int
        val httpMessage: String
    }
    data class Unauthorized(override val httpMessage: String) : HttpFailure {
        override val code: Int = 401
    }
    data class NotARecipe(override val code: Int, override val httpMessage: String, val message: String?) : HttpFailure
    data class Error(
        override val code: Int,
        override val httpMessage: String,
        val status: String?,
        val message: String?
    ) : HttpFailure

    data class Failure(val cause: Exception) : WizardResponse {
        val isNetwork: Boolean get() = cause is IOException
    }
}

/** Fehlertext, wie ihn der Foto-Wizard schon immer als Toast angezeigt hat. */
fun WizardResponse.errorText(): String? = when (this) {
    is WizardResponse.Success, WizardResponse.EmptyBody -> null
    WizardResponse.NotLoggedIn -> "Not logged in"
    is WizardResponse.HttpFailure -> "Wizard failed: $code $httpMessage"
    is WizardResponse.Failure -> cause.message ?: "Unknown error"
}

data class WizardErrorBody(
    val status: String? = null,
    val message: String? = null
)

@Singleton
class WizardRepository @Inject constructor(
    private val wizardApiService: WizardApiService,
    private val authRepository: AuthRepository,
    moshi: Moshi
) {
    private val errorBodyAdapter = moshi.adapter(WizardErrorBody::class.java)

    suspend fun analyzeImage(jpegBytes: ByteArray): WizardResponse = call { headers ->
        val requestFile = jpegBytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("image", "wizard.jpg", requestFile)
        wizardApiService.analyzeRecipeImage(body, headers)
    }

    private suspend fun call(
        request: suspend (headers: Map<String, String>) -> Response<WizardIngredientList>
    ): WizardResponse {
        val token = authRepository.getToken() ?: return WizardResponse.NotLoggedIn
        return try {
            val response = request(mapOf("Authorization" to "Bearer $token"))
            if (response.isSuccessful) {
                response.body()?.let { WizardResponse.Success(it) } ?: WizardResponse.EmptyBody
            } else {
                mapHttpError(response)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            WizardResponse.Failure(e)
        }
    }

    // Status-Code zuerst; der Body ist nur bei manchen Fehlern JSON (401 liefert Plain Text).
    private fun mapHttpError(response: Response<*>): WizardResponse.HttpFailure {
        val code = response.code()
        val httpMessage = response.message()
        if (code == 401) return WizardResponse.Unauthorized(httpMessage)

        val body = try {
            response.errorBody()?.string()?.let { errorBodyAdapter.fromJson(it) }
        } catch (_: Exception) {
            null
        }
        return if (code == 422) {
            WizardResponse.NotARecipe(code, httpMessage, body?.message)
        } else {
            WizardResponse.Error(code, httpMessage, body?.status, body?.message)
        }
    }
}
