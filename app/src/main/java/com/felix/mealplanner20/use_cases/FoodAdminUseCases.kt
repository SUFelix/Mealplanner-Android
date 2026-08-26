package com.felix.mealplanner20.use_cases

import com.felix.mealplanner20.Meals.Data.DTO.IngredientAllowedUnitReviewDTO
import com.felix.mealplanner20.Meals.Data.DTO.IngredientDTO
import com.felix.mealplanner20.Meals.Data.IngredientRepository
import com.felix.mealplanner20.apiService.IngredientAllowedUnitApiService
import com.mealplanner20.jwtauthktorandroid.auth.AuthRepository

class GetPendingIngredientsUseCase(
    private val ingredientRepository: IngredientRepository,
    private val authRepository: AuthRepository
) {
    suspend fun execute(): List<IngredientDTO> {
        val token = authRepository.getToken() ?: return emptyList()
        return ingredientRepository.getPendingIngredientsFromApi(token)
    }
}

class ApproveIngredientUseCase(
    private val ingredientRepository: IngredientRepository,
    private val authRepository: AuthRepository
) {
    suspend fun execute(id: Long): Boolean {
        val token = authRepository.getToken() ?: return false
        return ingredientRepository.approveIngredient(id, token)
    }
}

class RejectIngredientUseCase(
    private val ingredientRepository: IngredientRepository,
    private val authRepository: AuthRepository
) {
    suspend fun execute(id: Long): Boolean {
        val token = authRepository.getToken() ?: return false
        return ingredientRepository.rejectIngredient(id, token)
    }
}

class GetIngredientByIdUseCase(
    private val ingredientRepository: IngredientRepository
) {
    suspend fun execute(id: Long): IngredientDTO? {
        return ingredientRepository.getIngredientByIdFromApi(id)
    }
}

class GetPendingAllowedUnitsUseCase(
    private val ingredientAllowedUnitApiService: IngredientAllowedUnitApiService,
    private val authRepository: AuthRepository
) {
    suspend fun execute(): List<IngredientAllowedUnitReviewDTO> {
        val token = authRepository.getToken() ?: return emptyList()
        val headers = mapOf("Authorization" to "Bearer $token")
        return ingredientAllowedUnitApiService.getPendingAllowedUnits(headers)
    }
}

class ApproveAllowedUnitUseCase(
    private val ingredientAllowedUnitApiService: IngredientAllowedUnitApiService,
    private val authRepository: AuthRepository
) {
    suspend fun execute(ingredientId: Long, unit: String): Boolean {
        val token = authRepository.getToken() ?: return false
        val headers = mapOf("Authorization" to "Bearer $token")
        return try {
            ingredientAllowedUnitApiService.approveAllowedUnit(ingredientId, unit, headers).isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}

class RejectAllowedUnitUseCase(
    private val ingredientAllowedUnitApiService: IngredientAllowedUnitApiService,
    private val authRepository: AuthRepository
) {
    suspend fun execute(ingredientId: Long, unit: String): Boolean {
        val token = authRepository.getToken() ?: return false
        val headers = mapOf("Authorization" to "Bearer $token")
        return try {
            ingredientAllowedUnitApiService.rejectAllowedUnit(ingredientId, unit, headers).isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}
