package com.mealplanner20.jwtauthktorandroid.auth

import android.content.SharedPreferences
import android.util.Log
import com.google.gson.JsonParseException
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import java.io.IOException

@Serializable
data class ErrorResponse(
    val code: String,
    val message: String
)

class AuthRepositoryImpl(
    private val api: AuthApi,
    private val prefs: SharedPreferences
): AuthRepository {

    override suspend fun signUp(username: String, email: String, password: String): AuthResult<Unit> {
        return try {
            api.signUp(
                request = SignUpRequest(
                    username = username,
                    email = email,
                    password = password
                )
            )

            AuthResult.SuccessfullySignUp()


        } catch (e: HttpException) {
            when (e.code()) {
                401 -> AuthResult.Unauthorized()
                409 -> {
                    val errorBody = e.response()?.errorBody()?.string()
                    val error = try {
                        Json.decodeFromString<ErrorResponse>(errorBody ?: "")
                    } catch (ex: Exception) {
                        null
                    }

                    when (error?.code) {
                        "EMPTY_FIELDS" -> AuthResult.FieldsEmpty()
                        "PASSWORD_TOO_SHORT" -> AuthResult.PasswordToShort()
                        "USERNAME_TAKEN" -> AuthResult.UsernameTaken()
                        "EMAIL_TAKEN" -> AuthResult.EmailTaken()
                        else -> AuthResult.UnknownError()
                    }
                }
                else -> AuthResult.UnknownError()
            }
        } catch (e: Exception) {
            AuthResult.UnknownError()
        }
    }


    override suspend fun signIn(usernameOrEmail: String, password: String): AuthResult<Unit> {
        return try {
            val response = api.signIn(
                request = AuthRequest(
                    username = usernameOrEmail,
                    password = password
                )
            )
            prefs.edit()
                .putString("jwt", response.token)
                .apply()
            prefs.edit()
                .putString("usernameOrEmail", usernameOrEmail)
                .apply()
            prefs.edit()
                .putString("role", response.role.toString())
                .apply()
            AuthResult.Authorized()
        } catch(e: HttpException) {
            when (e.code()) {
                401 -> AuthResult.Unauthorized()
                409 -> {
                    val errorBody = e.response()?.errorBody()?.string()
                    val error = try {
                        Json.decodeFromString<ErrorResponse>(errorBody ?: "")
                    } catch (ex: Exception) {
                        null
                    }

                    when (error?.code) {
                        "INCORRECT_USERNAME_OR_PASSWORD" -> AuthResult.IncorrectPasswordOrUsername()

                        else -> AuthResult.UnknownError()
                    }
                }
                else -> AuthResult.UnknownError()
            }
        }catch(e: JsonParseException) {
            AuthResult.IOError()
        }catch(e: IOException) {
            AuthResult.IOError()
        } catch (e: Exception) {
            AuthResult.UnknownError()
        }
    }

    override suspend fun logout(): AuthResult<Unit> {
        return try {
            prefs.edit().remove("jwt").apply()
            prefs.edit().remove("username").apply()
            prefs.edit().remove("role").apply()


            AuthResult.SuccessFullLogout()
        } catch (e: Exception) {
            AuthResult.LogoutFailed()
        }
    }

    override suspend fun getToken(): String? {
        return prefs.getString("jwt", null)
    }

    override suspend fun requestPasswordReset(email: String): AuthResult<Unit> {
        return try {
            val response = api.requestPasswordReset(
                request = PasswordResetRequestEmail(email = email)
            )
            AuthResult.SuccessfulPasswordResetRequest()
        } catch(e: IOException) {
            AuthResult.IOError()
        } catch(e: HttpException) {
            AuthResult.UnknownError()
        } catch(e: Exception) {
            AuthResult.UnknownError()
        }
    }


    override suspend fun confirmPasswordReset(token: String, newPassword: String): AuthResult<Unit> {
        return try {
            api.confirmPasswordReset(
                request = PasswordResetRequest(
                    token = token,
                    newPassword = newPassword
                )
            )
            AuthResult.PasswordResetConfirmed()
        } catch(e: HttpException) {
            when(e.code()) {
                400 -> AuthResult.InvalidTokenOrExpired()
                401 -> AuthResult.InvalidTokenOrExpired()
                409->AuthResult.PasswordToShort()
                else -> AuthResult.UnknownError()
            }
        } catch(e: IOException) {
            AuthResult.IOError()
        } catch(e: Exception) {
            AuthResult.UnknownError()
        }
    }


    override suspend fun authenticate(): AuthResult<Unit> {
        return try {
            val token = prefs.getString("jwt", null) ?: return AuthResult.Unauthorized()
            api.authenticate("Bearer $token")
            AuthResult.Authorized()
        } catch(e: HttpException) {
            if(e.code() == 401) {
                AuthResult.Unauthorized()
            } else {
                AuthResult.UnknownError()
            }
        } catch (e: Exception) {
            AuthResult.UnknownError()
        }
    }


    override suspend fun getUsernameClaim(): String? {
        val raw = getToken() ?: return null
        val token = raw.removePrefix("Bearer ").trim()
        val parts = token.split(".")
        if (parts.size < 2) return null
        return try {
            val payload = android.util.Base64.decode(
                parts[1],
                android.util.Base64.URL_SAFE or android.util.Base64.NO_WRAP or android.util.Base64.NO_PADDING
            )
            val json = org.json.JSONObject(String(payload, Charsets.UTF_8))
            json.optString("username", null)?.takeIf { it.isNotBlank() }
        } catch (e: Exception) {
            null
        }
    }
}