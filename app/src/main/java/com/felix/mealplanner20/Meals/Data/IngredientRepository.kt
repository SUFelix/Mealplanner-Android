package com.felix.mealplanner20.Meals.Data

import android.util.Log
import com.felix.mealplanner20.Meals.Data.DTO.IngredientDTO
import com.felix.mealplanner20.Meals.Data.DTO.IngredientMatchReviewDTO
import com.felix.mealplanner20.Meals.Data.helpers.UnitOfMeasure
import com.felix.mealplanner20.Meals.Data.helpers.dgeGroup
import com.felix.mealplanner20.apiService.IngredientApiService
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class IngredientRepository @Inject constructor(
    private val ingredientDao: IngredientDao,
    private val ingredientApiService: IngredientApiService
) {

    suspend fun syncronizeIngredients():Boolean {
       try {
           //TODO hier kann die ganze Response Logik verbessert werden?!
           val apiIngredients = ingredientApiService.getAllIngredients()

           val localIngredients = ingredientDao.getIngredientsByIds(apiIngredients.map { it.id })

           val ingredientsToInsert = mutableListOf<Ingredient>()
           val ingredientsToUpdate = mutableListOf<Ingredient>()

           for (apiIngredient in apiIngredients) {
               val localIngredient = localIngredients.find { it.id == apiIngredient.id }

               if (localIngredient == null) {
                   ingredientsToInsert.add(apiIngredient.toIngredient())
               } else {
                   // Server is now authoritative for plantGroupKey, so sync overwrites any local value.
                   val merged = apiIngredient.toIngredient()
                   if (localIngredient != merged) {
                       ingredientsToUpdate.add(merged)
                   }
               }
           }

           if (ingredientsToInsert.isNotEmpty()) {
               ingredientDao.addIngredients(ingredientsToInsert)
           }

           if (ingredientsToUpdate.isNotEmpty()) {
               for (ingredient in ingredientsToUpdate) {
                   ingredientDao.updateIngredient(ingredient)
               }
           }
           return true
       }
        catch (e: Exception) {
            Log.e("IngredientRepository", "Error syncing ingredients", e)
            return false
        }

    }
    suspend fun uploadIngredientToServer(ingredients: Ingredient,token:String) {
        try {
            val ingredientDTO = ingredients.toIngredientDTO()
            val headers = mapOf("Authorization" to "Bearer $token")
                val response =ingredientApiService.postIngredients(ingredientDTO,headers)
        } catch (e: Exception) {
            Log.e("Fehler beim Hochladen:"," ${e.message}")
        }
    }

    suspend fun uploadUpdateIngredientToServer(ingredients: Ingredient,token:String) {
        try {
            val ingredientDTO = ingredients.toIngredientDTO()
            val headers = mapOf("Authorization" to "Bearer $token")
            val response =ingredientApiService.putIngredients(ingredientDTO.id,ingredientDTO,headers)
        } catch (e: Exception) {
            Log.e("Fehler beim Hochladen:"," ${e.message}")
        }
    }
    fun IngredientDTO.toIngredient(): Ingredient {
        return Ingredient(
            id = this.id,
            germanName = this.germanName,
            englishName = this.englishName,
            calories = this.calories,
            fat = this.fat,
            saturatedFat = this.saturatedFat,
            carbs = this.carbs,
            sugar = this.sugar,
            protein = this.protein,
            fibre = this.fibre ?: 0.0f,
            dgeType = this.dgeType?.let { dgeGroup.valueOf(it) } ?: dgeGroup.OTHER,
            alcohol = this.alcohol ,
            isFavorit = this.isFavorit,
            unitOfMeasure = this.unitOfMeasure?.let { UnitOfMeasure.valueOf(it) } ?: UnitOfMeasure.GRAM,
            plantGroupKey = this.plantGroupKey
        )
    }
    fun Ingredient.toIngredientDTO(): IngredientDTO {
        return IngredientDTO(
            id = this.id,
            germanName = this.germanName,
            englishName = this.englishName,
            calories = this.calories,
            fat = this.fat,
            saturatedFat = this.saturatedFat,
            carbs = this.carbs,
            sugar = this.sugar,
            protein = this.protein,
            fibre = this.fibre,
            dgeType = this.dgeType.name,
            alcohol = this.alcohol,
            isFavorit = this.isFavorit,
            unitOfMeasure = this.unitOfMeasure.name,
            plantGroupKey = this.plantGroupKey
        )
    }

    suspend fun getAllIngredientsFromApi(): List<IngredientDTO> {
        return withContext(Dispatchers.IO) {
            ingredientApiService.getAllIngredients()
        }
    }

    suspend fun getIngredientByIdFromApi(id: Long): IngredientDTO? {
        return try {
            withContext(Dispatchers.IO) {
                ingredientApiService.getIngredientById(id)
            }
        } catch (e: Exception) {
            // 404 is expected here for a freshly created, not-yet-approved ingredient - the
            // public endpoint only returns APPROVED ingredients.
            null
        }
    }

    suspend fun getPendingIngredientsFromApi(token: String): List<IngredientDTO> {
        return withContext(Dispatchers.IO) {
            val headers = mapOf("Authorization" to "Bearer $token")
            ingredientApiService.getPendingIngredients(headers)
        }
    }

    suspend fun approveIngredient(id: Long, token: String): Boolean {
        return try {
            val headers = mapOf("Authorization" to "Bearer $token")
            ingredientApiService.approveIngredient(id, headers).isSuccessful
        } catch (e: Exception) {
            Log.e("IngredientRepository", "Error approving ingredient", e)
            false
        }
    }

    suspend fun rejectIngredient(id: Long, token: String): Boolean {
        return try {
            val headers = mapOf("Authorization" to "Bearer $token")
            ingredientApiService.rejectIngredient(id, headers).isSuccessful
        } catch (e: Exception) {
            Log.e("IngredientRepository", "Error rejecting ingredient", e)
            false
        }
    }

    suspend fun getPendingMatchesFromApi(token: String): List<IngredientMatchReviewDTO> {
        return withContext(Dispatchers.IO) {
            val headers = mapOf("Authorization" to "Bearer $token")
            ingredientApiService.getPendingMatches(headers)
        }
    }

    suspend fun confirmMatch(taskId: Long, token: String): Boolean {
        return try {
            val headers = mapOf("Authorization" to "Bearer $token")
            ingredientApiService.confirmMatch(taskId, headers).isSuccessful
        } catch (e: Exception) {
            Log.e("IngredientRepository", "Error confirming match", e)
            false
        }
    }

    suspend fun rejectMatch(taskId: Long, token: String): Boolean {
        return try {
            val headers = mapOf("Authorization" to "Bearer $token")
            ingredientApiService.rejectMatch(taskId, headers).isSuccessful
        } catch (e: Exception) {
            Log.e("IngredientRepository", "Error rejecting match", e)
            false
        }
    }

   suspend fun addIngredient(ingredient: Ingredient):Long{
       return  ingredientDao.addIngredient(ingredient)
    }
    fun getAllIngredients(): Flow<List<Ingredient>> = ingredientDao.getAllIngredients()

    fun getIngredientById(id:Long):Flow<Ingredient>{
        return  ingredientDao.getIngredientById(id)
    }

    suspend fun updateIngredient(ingredient:Ingredient){
        ingredientDao.updateIngredient(ingredient)
    }

    suspend fun deleteIngredient(ingredient:Ingredient){
        ingredientDao.deleteIngredient(ingredient)
    }
    suspend fun getIngredientListByIdList(ingredientIds:List<Long>):List<Ingredient>{
        return ingredientDao.getIngredientListByIdList(ingredientIds).first()
    }
    suspend fun getIngredientListFlowByIdList(ingredientIds:List<Long>):Flow<List<Ingredient>>{
        return ingredientDao.getIngredientListByIdList(ingredientIds)
    }
}