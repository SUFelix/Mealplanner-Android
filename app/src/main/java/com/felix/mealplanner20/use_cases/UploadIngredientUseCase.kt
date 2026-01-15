package com.felix.mealplanner20.use_cases

import android.content.Context
import android.net.Uri
import android.util.Log
import com.felix.mealplanner20.Meals.Data.Ingredient
import com.felix.mealplanner20.Meals.Data.IngredientRepository
import com.felix.mealplanner20.Meals.Data.ProfileRepository
import com.mealplanner20.jwtauthktorandroid.auth.AuthRepository
import retrofit2.Response

class UploadIngredientUseCase (
    private val ingredientRepository: IngredientRepository,
    private val authRepository: AuthRepository
) {
    suspend fun execute(ingredient: Ingredient) {
        authRepository.getToken()?.let {token ->
            ingredientRepository.uploadIngredientToServer(ingredient, token)
        }
    }
}

class UploadUpdateIngredientUseCase (
    private val ingredientRepository: IngredientRepository,
    private val authRepository: AuthRepository
) {
    suspend fun execute(ingredient: Ingredient) {
        authRepository.getToken()?.let {token ->
            ingredientRepository.uploadUpdateIngredientToServer(ingredient, token)
        }
    }
}

class UploadNewProfileDescriptionUseCase (
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository
) {
    suspend fun execute(newDescription:String) {
        authRepository.getToken()?.let {token ->
            profileRepository.uploadNewDescription(newDescription = newDescription, token)
        }
    }
}

class GetOwnProfileDescriptionUseCase (
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository
) {
    suspend fun execute():String? {
        authRepository.getToken()?.let {token ->
           return profileRepository.getOwnProfileDescription(token)
        }
        return null
    }
}

class GetOwnEmailUseCase (
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository
) {
    suspend fun execute():String? {
        authRepository.getToken()?.let {token ->
            return profileRepository.getOwnEmail(token)
        }
        return null
    }
}

class GetOwnProfilePictureUseCase (
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository
) {
    suspend fun execute():ByteArray? {
        authRepository.getToken()?.let {token ->
            return profileRepository.getOwnProfilePicture(token)
        }
        return null
    }
}

class UploadNewProfilePictureUseCase (
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository
) {
    suspend fun execute(context: Context,uri: Uri,code:String): Response<Unit>? {
        return authRepository.getToken()?.let {token ->
            profileRepository.uploadProfileImageAndUpdateUri(context,uri, token,code)
        }
    }
}
