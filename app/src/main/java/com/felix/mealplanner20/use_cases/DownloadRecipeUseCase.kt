package com.felix.mealplanner20.use_cases

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.felix.mealplanner20.Meals.Data.RecipeRepository
import com.felix.mealplanner20.Meals.Data.SettingsRepository
import com.felix.mealplanner20.Meals.Data.helpers.UnitOfMeasure
import com.felix.mealplanner20.ViewModels.StepUriMap
import com.felix.mealplanner20.apiService.ImageApiService
import kotlinx.coroutines.flow.first
import java.io.File
import java.io.FileOutputStream
import java.util.Locale
import java.util.UUID

class DownloadRecipeUseCase(
    private val repository: RecipeRepository,
    private val imageApiService: ImageApiService,
    private val settingsRepository: SettingsRepository
) {

    val isGerman = Locale.getDefault().language == "de"

    val showOriginalTitle = settingsRepository.observeShowOriginalTitle()

    suspend operator fun invoke(context: Context, remoteRecipeId: Long): DownloadRecipeResult {
        return try {
            val fullRecipeDTO = repository.getFullRecipeById(remoteRecipeId)
                ?: return DownloadRecipeResult.RecipeNotFound

            val recipeDTO = fullRecipeDTO.recipe
            val recipe = recipeDTO.toRecipe().copy(id = 0L, remoteId =  remoteRecipeId)
            var newUri: Uri? = null

            recipe.imgUri?.let { uri ->
                try {
                    val response = imageApiService.fetchRecipeImageFromOnlineSource(uri.toString())
                    if (response.isSuccessful) {
                        response.body()?.bytes()?.let { imageBytes ->
                            newUri = savePictureLocally(context, imageBytes)
                        } ?: return DownloadRecipeResult.ImageDownloadFailed
                    } else {
                        return DownloadRecipeResult.ImageDownloadFailed
                    }
                } catch (e: Exception) {
                    Log.e("DownloadRecipeUseCase", "Failed to fetch recipe image", e)
                    return DownloadRecipeResult.ImageDownloadFailed
                }
            }

            val newRecipe =if(showOriginalTitle.first()){
                recipe.copy(imgUri = newUri, title = recipe.title, isFavorit = true)
            } else{
                if (isGerman) {
                    recipe.copy(imgUri = newUri, title = recipe.germanTitle, isFavorit = true)
                } else {
                    recipe.copy(imgUri = newUri, title = recipe.englishTitle, isFavorit = true)
                }
            }

            val newRecipeId = repository.addRecipe(newRecipe)

            if (!repository.isRecipeStored(newRecipeId)) {
                return DownloadRecipeResult.UnknownError()
            }

           try{
            fullRecipeDTO.ingredients.forEach {
                repository.addIngredientToRecipe(
                    newRecipeId,
                    it.ingredientId,
                    it.ingredientQuantity,
                    UnitOfMeasure.valueOf(it.unitOfMeasure),
                    it.originalQuantity
                )
            }
           }
           catch(e:IllegalArgumentException){
               Log.e("DownloadRecipeUseCase", "Failed to add Ingredients,Maybe an UnitOfMeasure Problem", e)
           }
           catch(e:Exception){
               Log.e("DownloadRecipeUseCase", "Failed to add Ingredients", e)
           }

            // Step-Bilder herunterladen
            val steps = if (isGerman) fullRecipeDTO.steps.map { it.copy(text = it.germanText, recipeId = newRecipeId.toInt()) } else fullRecipeDTO.steps.map { it.copy(text = it.englishText, recipeId = newRecipeId.toInt()) }
            val uriMap = mutableListOf<StepUriMap>()

            steps.forEach { step ->
                if (step.imgUri.isNullOrBlank()) {
                    return@forEach
                }
                    try {
                        val response = imageApiService.fetchDescriptionImageFromOnlineSource(step.imgUri)
                        if (response.isSuccessful) {
                            response.body()?.bytes()?.let { imageBytes ->
                                savePictureLocally(context, imageBytes)?.let { uri ->
                                    uriMap.add(StepUriMap(step.id, uri))
                                }
                            }
                        } else {
                            Log.w("DownloadRecipeUseCase", "Failed to fetch step image: ${step.imgUri}")
                        }
                    } catch (e: Exception) {
                        Log.e("DownloadRecipeUseCase", "Failed to fetch step image", e)
                    }
            }

            val updatedSteps = steps.map { step ->
                val updatedStep = step.copy(imgUri = null)
                uriMap.find { it.stepId == updatedStep.id }?.let { stepUriMap ->
                    updatedStep.copy(imgUri = stepUriMap.image.toString())
                } ?: updatedStep
            }

            repository.insertRecipeDescriptionSteps(updatedSteps)

            DownloadRecipeResult.Success(newRecipeId)
        } catch (e: Exception) {
            Log.e("DownloadRecipeUseCase", "Unknown error", e)
            DownloadRecipeResult.UnknownError(e)
        }
    }

}

private fun savePictureLocally(context: Context, bytes:ByteArray?): Uri?{
    bytes?.let { byteArray ->
        val file = File(context.filesDir, "${UUID.randomUUID()}.jpg")
        FileOutputStream(file).use { fos ->
            fos.write(byteArray)
        }
        return file.toUri()
    }
    return null
}

sealed class DownloadRecipeResult {
    data class Success(val recipeId: Long) : DownloadRecipeResult()
    object RecipeNotFound : DownloadRecipeResult()
    object ImageDownloadFailed : DownloadRecipeResult()
    object StepImagesDownloadFailed : DownloadRecipeResult()
    data class UnknownError(val throwable: Throwable? = null) : DownloadRecipeResult()
    object Idle: DownloadRecipeResult()
    object Loading: DownloadRecipeResult()
}
