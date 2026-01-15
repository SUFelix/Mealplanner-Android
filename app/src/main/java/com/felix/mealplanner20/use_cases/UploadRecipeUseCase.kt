package com.felix.mealplanner20.use_cases

import android.content.Context
import android.util.Log
import com.felix.mealplanner20.Meals.Data.PublishRecipeResult
import com.felix.mealplanner20.Meals.Data.RecipeRepository
import com.felix.mealplanner20.di.BASE_URL
import com.mealplanner20.jwtauthktorandroid.auth.AuthRepository
import kotlinx.coroutines.flow.firstOrNull
import java.util.UUID

const val IMAGE_METADATA_CODE ="IMGCODE"

class UploadRecipeUseCase (
    private val recipeRepository: RecipeRepository,
    private val authRepository: AuthRepository
) {
    suspend fun execute(context: Context,recipeId:Long): PublishRecipeResult {
        val code = generateRandomCode(recipeId)
        val codes:Array<String?> = generateRandomCodes(recipeId,20)


        val token = authRepository.getToken() ?: return PublishRecipeResult.LoginRequired

        recipeRepository.uploadRecipeImage(context,recipeId, token,code)
        recipeRepository.uploadDescriptionImages(context,recipeId, token,codes)

        val response = recipeRepository.postRecipeToServer(recipeId, token,code,codes)

        return response
    }

    fun generateRandomCode(recipeId:Long): String {
        return UUID.randomUUID().toString()+ "###" +recipeId.toString()
    }

    fun generateRandomCodes(recipeId: Long, count: Int): Array<String?> {
        return Array(count) {
            UUID.randomUUID().toString() + "###" + recipeId.toString()
        }
    }
}

class UpdateRecipeUseCase(
    private val recipeRepository: RecipeRepository,
    private val authRepository: AuthRepository
) {
    enum class ImageAction { Unchanged, Replace, Delete }
    data class UpdateChanges(
        val recipeImage: ImageAction,
        val stepCodesArray: Array<String?>
    )

    private fun generateRandomCode(recipeId: Long): String =
        UUID.randomUUID().toString() + "###" + recipeId.toString()


    suspend fun execute(
        context: Context,
        localRecipeId: Long,
        changes: UpdateChanges
    ): PublishRecipeResult {
        val token = authRepository.getToken() ?: return PublishRecipeResult.LoginRequired

        val recipeCode = when (changes.recipeImage) {
            ImageAction.Unchanged -> ""
            ImageAction.Delete -> "-"
            ImageAction.Replace -> generateRandomCode(localRecipeId)
        }
        val remoteId = recipeRepository.getRecipeById(localRecipeId).firstOrNull()?.remoteId
            ?: return PublishRecipeResult.PostToServerFailed()

        // Nur neue Bilder hochladen (Replace)
        recipeRepository.uploadRecipeImage(context, localRecipeId, token, recipeCode)
        recipeRepository.uploadDescriptionImages(context, localRecipeId, token, changes.stepCodesArray)

        // PUT analog zu POST
        return recipeRepository.putRecipeToServer(
            localRecipeId = localRecipeId,
            remoteRecipeId = remoteId,
            token = token,
            recipeImageCode = recipeCode,
            descriptionStepImageCodes = changes.stepCodesArray
        )
    }

    data class TokenEnv( val audience: String)

    private fun extractUsernameFromToken(rawToken: String, env: TokenEnv = TokenEnv(audience = "users")): String? {
        val token = rawToken.removePrefix("Bearer ").trim()
        Log.d("Token: ","${token}")

        val parts = token.split(".")
        if (parts.size < 2) return null
        return try {
            val payloadJson = String(android.util.Base64.decode(parts[1], android.util.Base64.URL_SAFE or android.util.Base64.NO_WRAP))
            val json = org.json.JSONObject(payloadJson)// Optional: leichte Plausibilitätschecks (keine Verifikation!)
            val audOk = json.opt("aud")?.let { aud ->
                when (aud) {
                    is String -> aud.trim() == env.audience
                    is org.json.JSONArray -> (0 until aud.length()).any { aud.getString(it).trim() == env.audience }
                    else -> false
                }
            } ?: false
            if (!audOk) return null

            json.optString("username", null)?.takeIf { it.isNotBlank() }
        } catch (_: Exception) {
            null
        }
    }

    private suspend fun currentUsername(): String? {
        val token = authRepository.getToken() ?: return null
        Log.d("Token: ","${token}")
        val username =  extractUsernameFromToken(token)
        Log.d("Extractedusername: ","${username}")
        return username
    }

    suspend fun checkUpdatePermission(recipeId: Long): UpdatePermission {
        val me = authRepository.getUsernameClaim() ?: return UpdatePermission.NotLoggedIn
        val recipe = recipeRepository.getRecipeById(recipeId).firstOrNull() ?: return UpdatePermission.RecipeNotFound
        if (recipe.remoteId == null) return UpdatePermission.NotRemote
        val owner = recipe.createdBy.norm() ?: return UpdatePermission.OwnerUnknown
        return if (owner == me.norm()) UpdatePermission.Allowed else UpdatePermission.Forbidden
    }

    private fun String?.norm() = this?.trim()?.lowercase()

    sealed class UpdatePermission {
        object Allowed : UpdatePermission()
        object NotLoggedIn : UpdatePermission()
        object NotRemote : UpdatePermission()
        object OwnerUnknown : UpdatePermission()
        object Forbidden : UpdatePermission()
        object RecipeNotFound : UpdatePermission()
    }
}