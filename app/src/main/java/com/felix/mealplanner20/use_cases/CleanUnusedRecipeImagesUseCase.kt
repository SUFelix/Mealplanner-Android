package com.felix.mealplanner20.use_cases

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.core.net.toUri
import com.felix.mealplanner20.Meals.Data.RecipeRepository
import kotlinx.coroutines.flow.first
import java.io.File

class CleanUnusedRecipeImagesUseCase(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(context: Context) {
        val urisUsedInLocalRecipes = getImageUrisFromRecipes()
        deleteUnusedImages(context,urisUsedInLocalRecipes)
    }

    fun deleteUnusedImages(context: Context, usedUris: List<Uri>) {
        val imagesFolder = File(context.filesDir, "recipe_images")
        var deleteCount = 0
        if (imagesFolder.exists()) {
            val imageFiles = imagesFolder.listFiles()
            imageFiles?.forEach { file ->
                if (!usedUris.contains(file.toUri())) {
                    val deleteSuccess = file.delete()
                    if(deleteSuccess) {
                        deleteCount++
                    }
                }
            }
        }
        Toast.makeText(context,"$deleteCount images deleted",Toast.LENGTH_LONG).show()
    }

    suspend fun getImageUrisFromRecipes(): List<Uri> {
        val imageUris = mutableListOf<Uri>()

        val recipes = repository.getAllRecipes().first()

        for (recipe in recipes) {
            recipe.imgUri?.let { uri ->
                imageUris.add(uri)
            }
        }
        return imageUris
    }

}