package com.felix.mealplanner20.Meals.Data

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.felix.mealplanner20.BUCKET
import com.felix.mealplanner20.ImageUpDownLoad
import com.felix.mealplanner20.Meals.Data.DTO.IngredientWithRecipeDTO
import com.felix.mealplanner20.Meals.Data.DTO.IngredientWithoutRecipeIdDTO
import com.felix.mealplanner20.Meals.Data.DTO.RecipeFullDTO
import com.felix.mealplanner20.Meals.Data.helpers.Mealtype
import com.felix.mealplanner20.Meals.Data.helpers.UnitOfMeasure
import com.felix.mealplanner20.ViewModels.RecipeResponse
import com.felix.mealplanner20.apiService.FullRecipeDTO
import com.felix.mealplanner20.apiService.ImageApiService
import com.felix.mealplanner20.apiService.RecipeApiService
import com.felix.mealplanner20.use_cases.IMAGE_METADATA_CODE
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import java.io.ByteArrayOutputStream
import java.io.InputStream
import javax.inject.Inject

class RecipeRepository @Inject constructor(
    private val recipeDao: RecipeDao,
    private val ingredientRecipeJoinDao:IngredientRecipeJoinDao,
    private val ingredientDao:IngredientDao,
    private val recipeDescriptionDao: RecipeDescriptionDao,
    private val ingredientAllowedUnitDao: IngredientAllowedUnitDao,
    private val recipeApiService: RecipeApiService,
    private val imageApiService: ImageApiService,
    private val updownload: ImageUpDownLoad,
    private val weightDao: RecipeMealTypeWeightDao
) {
    suspend fun addRecipe(recipe: Recipe):Long{
        return recipeDao.addRecipe(recipe)
    }
    fun getAllRecipes(isVegan: Boolean = false,isVegetarian: Boolean = false): Flow<List<Recipe>> = recipeDao.getAllRecipes(isVegan,isVegetarian)
    fun getAllMeals(): Flow<List<Recipe>> = recipeDao.getAllMeals()
    fun getAllBreakfasts(): Flow<List<Recipe>> = recipeDao.getAllBreakfasts()
    fun getAllSnacks(): Flow<List<Recipe>> = recipeDao.getAllSnacks()
    fun getAllBeverages(): Flow<List<Recipe>> = recipeDao.getAllBeverages()
    fun getAllFavoriteRecipes(isVegan: Boolean = false,isVegetarian: Boolean = false): Flow<List<Recipe>> = recipeDao.getAllFavoriteRecipes(isVegan,isVegetarian)

    fun getAllIngredientWithRecipe():Flow<List<IngredientWithRecipe>> = ingredientRecipeJoinDao.getAllIngredientWithRecipe()
    fun getAllIngredientsForOneRecipe(id:Long):Flow<List<IngredientWithRecipe>> = ingredientRecipeJoinDao.getAllIngredientWithRecipe(id)
    suspend fun getIngredientsForRecipe(recipeId: Long): List<IngredientWithRecipe> = ingredientRecipeJoinDao.getIngredientsForRecipe(recipeId)


    fun getRecipeById(id:Long): Flow<Recipe> {
        return  recipeDao.getRecipeById(id)
    }
    suspend fun getRecipeDescriptionStepsByRecipeId(recipeId:Long):List<RecipeDescription> {
        return  recipeDescriptionDao.getStepsForRecipe(recipeId)
    }

    suspend fun insertRecipeDescriptionSteps(recipeDescriptionSteps:List<RecipeDescription> ){
        return  recipeDescriptionDao.insertRecipeSteps(recipeDescriptionSteps)
    }
    suspend fun updateRecipeDescriptionSteps(recipeDescriptionSteps:List<RecipeDescription> ){
        return  recipeDescriptionDao.updateRecipeSteps(recipeDescriptionSteps)
    }

    suspend fun getAllowedUnitsForIngredient(ingredientId:Long): List<IngredientAllowedUnit> {
        return ingredientAllowedUnitDao.getAllowedUnitsForIngredient(ingredientId)
    }

    fun getRecipesByIds(ids:List<Long>): Flow<List<Recipe>> {
        return  recipeDao.getRecipesByIds(ids)
    }

   fun getRecipeCalories(): Flow<List<RecipeCalories>> {
        return recipeDao.getRecipeCaloriesPerServing()
    }

    suspend fun deleteRecipeById(id:Long){
        recipeDao.deleteRecipeById(id)
    }
    suspend fun deleteDescriptionStep(stepId:String){
        recipeDescriptionDao.deleteRecipeStepById(stepId)
    }
    suspend fun addIngredientToRecipe(recipeId: Long, ingredientId: Long, quantity: Float, unitOfMeasure: UnitOfMeasure,originalQuantity:Float) {
        ingredientRecipeJoinDao.addIngredientToRecipe(recipeId, ingredientId, quantity, unitOfMeasure,originalQuantity)
    }
    fun getRecipeWithIngredientsById(recipeId: Long): Flow<List<IngredientWithRecipe>> {
        return ingredientRecipeJoinDao.getRecipeWithIngredientsById(recipeId)
    }

     suspend fun updateRecipeMainTable(
         recipeId: Long,
         newTitle: String,
         newIsMeal: Boolean,
         newIsBreakfast: Boolean,
         newIsSnack: Boolean,
         newIsBeverage:Boolean,
         newIsDessert:Boolean,
         isVegan: Boolean,
         isVegetarian: Boolean,
         newUri: Uri?,
         newCreatedBy: String?,
         newServings: Float,
         newCPFratio:MacronutrientRatio,
         newCaloriesPerserving:Float
     ) {
        return recipeDao.updateRecipeTitleAndType(recipeId,newTitle,newIsMeal,newIsBreakfast,newIsSnack,newIsBeverage,newIsDessert,isVegan,isVegetarian,newUri,newCreatedBy,newServings,newCPFratio,newCaloriesPerserving)
    }

    suspend fun deleteIngredientFromRecipe(recipeId: Long, ingredientId: Long){
        ingredientRecipeJoinDao.deleteIngredientFromRecipe(recipeId, ingredientId)
    }

    suspend fun getRecipesFromServer(currentPage: Int, pageSize: Int): RecipeResponse? {
        try {
            val recipeResponse = recipeApiService.fetchRecipesWithIngredientsFromOnlineSource(currentPage,pageSize)

            if (recipeResponse != null) {
                return RecipeResponse(
                    recipes = recipeResponse.recipes,
                    hasNextPage = recipeResponse.hasNextPage
                )
            }

            else return null

        } catch (e: Exception){
            Log.e("RecipeRepository", "Error fetching recipes from server", e)
            return null
        }
    }
    suspend fun getFullRecipeById(recipeId: Long): FullRecipeDTO? {
        return try {
            recipeApiService.fetchFullRecipe(recipeId)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getTypeRecipesFromServer(type:Mealtype = Mealtype.DESSERT, currentPage: Int, pageSize: Int): RecipeResponse? {
        try {
            var recipeResponse:RecipeResponse

            when(type){
                Mealtype.DESSERT ->  recipeResponse = recipeApiService.fetchRecipesForType(currentPage,pageSize,mealType = Mealtype.DESSERT.toString())
                Mealtype.BREAKFAST ->  recipeResponse = recipeApiService.fetchRecipesForType(currentPage,pageSize,mealType = Mealtype.BREAKFAST.toString())
                Mealtype.MEAL ->  recipeResponse = recipeApiService.fetchRecipesForType(currentPage,pageSize,mealType = Mealtype.MEAL.toString())
                Mealtype.SNACK ->  recipeResponse = recipeApiService.fetchRecipesForType(currentPage,pageSize,mealType = Mealtype.SNACK.toString())
                Mealtype.BEVERAGE ->  recipeResponse = recipeApiService.fetchRecipesForType(currentPage,pageSize,mealType = Mealtype.BEVERAGE.toString())
            }

            if (recipeResponse != null) {
                return RecipeResponse(
                    recipes = recipeResponse.recipes,
                    hasNextPage = recipeResponse.hasNextPage
                )
            }

            else return null

        } catch (e: Exception){
            Log.e("RecipeRepository", "Error fetching recipes from server", e)
            return null
        }
    }

    suspend fun getIngredientsForOneRecipeFromServer(recipeId: Long):List<IngredientWithRecipe>?{
        try {
            val recipeComponents = recipeApiService.fetchIngredientsByRecipeId(recipeId)
            return recipeComponents?.ingredients?.map {
                    ingredientWithRecipeDTO ->
                ingredientWithRecipeDTO.toIngredientWithRecipe()
            }

        }catch (e: Exception){
            Log.e("RecipeRepository", "Error fetching ingredients for recipe nr $recipeId from server", e)
            return null
        }
    }

    suspend fun getRecipeComponentsFromServer(recipeId: Long): Pair<List<IngredientWithRecipe>?, List<RecipeDescription>?>? {
        try {
            val recipeComponents = recipeApiService.fetchIngredientsByRecipeId(recipeId)
            val ingredientList =  recipeComponents?.ingredients?.map {
                    ingredientWithRecipeDTO ->
                ingredientWithRecipeDTO.toIngredientWithRecipe()
            }
            val stepList = recipeComponents?.steps

            return Pair(ingredientList,stepList)

        }catch (e: Exception){
            Log.e("RecipeRepository", "Error fetching ingredients for recipe nr $recipeId from server", e)
            return null
        }
    }
    suspend fun postRecipeToServer(
        recipeId: Long,
        token: String,
        recipeImageCode: String,
        descriptionStepImageCodes: Array<String?>
    ): PublishRecipeResult {
        val body = createPostObjectFromRecipeId(recipeId)
        val imageHeader = buildImageHeader(recipeImageCode, descriptionStepImageCodes.map { it as String? }.toTypedArray())
        val headers = buildPublishHeaders(token, imageHeader)
        Log.d("DEBUG", "headers: $headers")

        return executePublish("POST /recipes") {
            recipeApiService.postRecipe(body, headers)
        }
    }

    suspend fun putRecipeToServer(
        localRecipeId: Long,
        remoteRecipeId: Long,
        token: String,
        recipeImageCode: String?,              // "" = unchanged, "-" = delete, sonst neuer Code
        descriptionStepImageCodes: Array<String?>, // null/"" = unchanged; "-" = delete
        ifMatch: String? = null
    ): PublishRecipeResult {
        val body = createPostObjectFromRecipeId(localRecipeId)
        val imageHeader = buildImageHeader(recipeImageCode, descriptionStepImageCodes)
        val headers = buildPublishHeaders(token, imageHeader, ifMatch)
        Log.d("DEBUG", "headers: $headers")

        return executePublish("PUT /recipes/$remoteRecipeId") {
            recipeApiService.putRecipe(remoteRecipeId, body, headers)
        }
    }
    private fun buildImageHeader(
        recipeImageCode: String?,
        stepImageCodes: Array<String?>
    ): String {
        val all = listOf(recipeImageCode ?: "") + stepImageCodes.map { it ?: "" }
        return all.joinToString(";")
    }
    private fun buildPublishHeaders(
        token: String,
        imageHeader: String,
        ifMatch: String? = null
    ): Map<String, String> {
        val headers = mutableMapOf(
            "Authorization" to "Bearer $token",
            IMAGE_METADATA_CODE to imageHeader
        )
        if (ifMatch != null) headers["If-Match"] = ifMatch
        return headers
    }
    private fun mapPublishError(statusCode: Int, errorBody: String?): PublishRecipeResult {
        val publishResp = try { Gson().fromJson(errorBody, PublishRecipeResponse::class.java) } catch (_: Exception) { null }
        val code = publishResp?.code ?: when (statusCode) {
            401 -> "LOGIN_REQUIRED"
            403 -> "USER_UNVERIFIED"
            404 -> "NOT_FOUND"
            409 -> "CONFLICT"
            412 -> "PRECONDITION_FAILED"
            413 -> "IMAGE_TOO_LARGE"
            415 -> "UNSUPPORTED_MEDIA_TYPE"
            422 -> "VALIDATION_FAILED"
            500 -> "POST_TO_SERVER_FAILED"
            else -> null
        }
        val traceId = publishResp?.traceId
        return when (code) {
            "LOGIN_REQUIRED" -> PublishRecipeResult.LoginRequired
            "USER_UNVERIFIED" -> PublishRecipeResult.UserUnverified
            "IMAGE_UPLOAD_FAILED", "RECIPE_IMAGE_UPLOAD_FAILED" -> PublishRecipeResult.RecipeImageUploadFailed(traceId)
            "DESCRIPTION_IMAGE_UPLOAD_FAILED" -> PublishRecipeResult.DescriptionImagesUploadFailed(traceId)
            "MISSING_IMAGE_METADATA" -> PublishRecipeResult.MissingImageMetadata
            "IMAGE_TOO_LARGE" -> PublishRecipeResult.ImageTooLarge()
            "UNSUPPORTED_MEDIA_TYPE" -> PublishRecipeResult.ImageFormatNotSupported()
            "VALIDATION_FAILED" -> PublishRecipeResult.ValidationFailed(publishResp?.details ?: emptyList(), traceId)
            "CONFLICT" -> PublishRecipeResult.Conflict
            "PRECONDITION_FAILED" -> PublishRecipeResult.Conflict
            "POST_TO_SERVER_FAILED" -> PublishRecipeResult.PostToServerFailed(traceId)
            else -> PublishRecipeResult.UnknownError(traceId)
        }
    }
    private suspend fun executePublish(
        actionLabel: String,
        call: suspend () -> retrofit2.Response<okhttp3.ResponseBody>
    ): PublishRecipeResult {
        return try {
            val response = call()
            Log.d("PublishRecipe", "$actionLabel status=${response.code()} isSuccessful=${response.isSuccessful}")
            if (response.isSuccessful) {
                val bodyStr = try { response.body()?.string() } catch (_: Exception) { null }
                val publishResp = try {
                    bodyStr?.let { Gson().fromJson(it, PublishRecipeResponse::class.java) }
                } catch (_: Exception) { null }
                Log.d("Responsecode:","${response.code()}")

                val remoteId = publishResp?.recipeId
                Log.d("Remoteid:","${remoteId}")

                val traceId = publishResp?.traceId

                if (remoteId == null || remoteId <= 0L) {
                    // Kein echter Erfolg, wenn der Server keine recipeId liefert
                    return PublishRecipeResult.PostToServerFailed(traceId)
                }
                PublishRecipeResult.Success(remoteId)
            } else {
                val errorBody = try { response.errorBody()?.string() } catch (_: Exception) { null }
                mapPublishError(response.code(), errorBody)
            }
        } catch (e: Exception) {
            Log.e("RecipeRepository", "Error during $actionLabel", e)
            PublishRecipeResult.UnknownError()
        }
    }
    @Serializable
    data class PublishRecipeResponse(
        val code: String,
        val message: String,
        val details: List<String>? = null,
        val recipeId: Long? = null,
        val traceId: String? = null
    )


    suspend fun uploadRecipeImage(context: Context, recipeId: Long, token: String,code:String): Result<String>? {
      return try{
          if (code.isBlank() || code == "-") return null
          getRecipeById(recipeId).first().let { recipe ->
              recipe.imgUri?.let {uri->
                  uriToByteArray(context,uri)?.let {bytes->
                      updownload.uploadImage(bytes, token, code, BUCKET.RECIPE)
                  }
              }
          }
       }catch (e:Exception){
           Log.e("ERROR",e.stackTraceToString())
          return null
       }
    }

    suspend fun uploadDescriptionImages(context: Context, recipeId: Long, token: String,codes:Array<String?>): Result<String>? {
         try{
             var result: Result<String>? = null
             val steps = getRecipeDescriptionStepsByRecipeId(recipeId)
             steps.forEach { step ->
                 val idx = (step.stepNr - 1).coerceAtLeast(0)
                 val code = codes.getOrNull(idx) ?: ""

                 // Skip bei unchanged ("") oder delete ("-")
                 if (code.isBlank() || code == "-") return@forEach

                 val uriStr = step.imgUri ?: return@forEach
                 uriToByteArray(context, uriStr.toUri())?.let { bytes ->
                     result = updownload.uploadImage(bytes, token, code, BUCKET.DESCRIPTION)
                 }
             }
             return result
        }catch (e:Exception){
            Log.e("ERROR",e.stackTraceToString())
            return null
        }
    }

    fun uriToByteArray(context: Context, uri: Uri): ByteArray? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)

            inputStream?.use {
                val byteArrayOutputStream = ByteArrayOutputStream()
                val buffer = ByteArray(1024)
                var length: Int
                while (it.read(buffer).also { len -> length = len } != -1) {
                    byteArrayOutputStream.write(buffer, 0, length)
                }
                byteArrayOutputStream.toByteArray()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

     private suspend fun createPostObjectFromRecipeId(recipeId: Long): RecipeFullDTO {
         val recipeDTO = getRecipeById(recipeId).first().toRecipeDTO()
         val steps = getRecipeDescriptionStepsByRecipeId(recipeId)
         val ingredients = getIngredientsForRecipe(recipeId)

        val fullDTO =  RecipeFullDTO(
            recipe = recipeDTO,
            ingredients = ingredients.map { ingredient ->
                IngredientWithoutRecipeIdDTO(
                    ingredientId = ingredient.ingredientId,
                    ingredientQuantity = ingredient.ingredientQuantity,
                    unitOfMeasure = ingredient.unitOfMeasure.name,
                    originalQuantity = ingredient.originalQuantity
                )
            },
            steps = steps
        )
        return fullDTO
    }

    fun IngredientWithRecipeDTO.toIngredientWithRecipe(): IngredientWithRecipe {
        return IngredientWithRecipe(
            recipeId = this.recipeId,
            ingredientId = this.ingredientId,
            ingredientQuantity = this.ingredientQuantity,
            unitOfMeasure = UnitOfMeasure.valueOf(this.unitOfMeasure),
            originalQuantity = this.originalQuantity
        )
    }

    suspend fun  isRecipeStored(recipeId:Long): Boolean {
        val count = recipeDao.isRecipeExist(recipeId)
        return count > 0
    }

    suspend fun updateWeight(recipeId: Long, mealType: Mealtype, weight: Float) {
        weightDao.upsert(RecipeMealTypeWeight(recipeId, mealType, weight.coerceIn(0f, 1f)))
    }

    suspend fun searchRecipesFromServer(
        q: String,
        lang: String = "de",
        page: Int = 1,
        pageSize: Int = 20
    ): RecipeResponse? {
        try {
            if (q.isBlank()) {
                return RecipeResponse(recipes = emptyList(), hasNextPage = false)
            }
            val resp = recipeApiService.searchRecipes(
                q = q.trim(),
                lang = if (lang == "en") "en" else "de",
                page = page,
                pageSize = pageSize
            )

            return RecipeResponse(
                recipes = resp.recipes,
                hasNextPage = resp.hasNextPage
            )
        } catch (e: Exception) {
            Log.e("RecipeRepository", "Error searching recipes (q=$$q, lang=$$lang, page=$page)", e)
            return null
        }

        }


    private fun normalizeProbabilities(recipes: List<Recipe>): List<Recipe> {
        val totalProbability = recipes.fold(0f) { acc, recipe -> acc + recipe.probability }

        if (totalProbability == 0f) return recipes

        return recipes.map { recipe ->
            val normalizedProbability = ((recipe.probability / totalProbability) * 100).toInt() / 100f
            recipe.copy(probability = normalizedProbability)
        }
    }

    suspend fun getWeightOrDefault(
        recipeId: Long,
        mealType: Mealtype,
        default: Float = 0.5f
    ): Float = weightDao.getWeight(recipeId, mealType) ?: default

    fun observeWeightsByMealTypeAsMap(mealType: Mealtype): Flow<Map<Long, Float>> =
        weightDao.observeByMealType(mealType).map { list ->
            list.associate { it.recipeId to it.weight }
        }

    suspend fun getWeightsForRecipes(
        mealType: Mealtype,
        recipeIds: List<Long>,
        default: Float = 0.5f
    ): Map<Long, Float> {
        if (recipeIds.isEmpty()) return emptyMap()
// Optional: für Performance eine DAO-Funktion mit IN (:ids) ergänzen
        return recipeIds.associateWith { id ->
            weightDao.getWeight(id, mealType) ?: default
        }
    }

    private fun normalizeWeights(weights: Map<Long, Float>): Map<Long, Double> {
        if (weights.isEmpty()) return emptyMap()
        val nonNegative: Map<Long, Double> = weights.mapValues { (_, value) ->
            value.coerceAtLeast(0f).toDouble()
        }

        val sum = nonNegative.values.sumOf { it }

        return if (sum <= 0.0) {
            val uniform = 1.0 / nonNegative.size
            nonNegative.mapValues { uniform }
        } else {
            nonNegative.mapValues { (_, value) ->
                value / sum
            }
        }
    }

    suspend fun getNormalizedWeightsFor(
        mealType: Mealtype,
        candidateIds: List<Long>,
        default: Float = 0.5f
    ): Map<Long, Double> {
        val weights = getWeightsForRecipes(mealType, candidateIds, default)
        return normalizeWeights(weights)
    }

    suspend fun ensureWeightsForRecipeTypes(
        recipeId: Long,
        default: Float = 0.5f
    ) {
        val recipe = getRecipeById(recipeId).first()
        val types = buildList {
            if (recipe.isMeal) add(Mealtype.MEAL)
            if (recipe.isBreakfast) add(Mealtype.BREAKFAST)
            if (recipe.isSnack) add(Mealtype.SNACK)
            if (recipe.isBeverage) add(Mealtype.BEVERAGE)
            if (recipe.isDessert) add(Mealtype.DESSERT)
        }
        for (t in types) {
            if (weightDao.getWeight(recipeId, t) == null) {
                weightDao.upsert(RecipeMealTypeWeight(recipeId, t, default))
            }
        }
    }

    suspend fun deleteWeightsForRecipe(recipeId: Long) {
        weightDao.deleteByRecipeId(recipeId)
    }

    suspend fun setRemoteId(localId: Long, remoteId: Long) {
        recipeDao.setRemoteId(localId, remoteId)
        Log.d("setting remote id to :","${remoteId}")
    }
}


