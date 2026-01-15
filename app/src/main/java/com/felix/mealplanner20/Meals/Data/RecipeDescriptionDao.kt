package com.felix.mealplanner20.Meals.Data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface RecipeDescriptionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipeStep(recipeStep: RecipeDescription)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipeSteps(recipeSteps: List<RecipeDescription>)

    @Query("SELECT * FROM recipe_description WHERE recipeId = :recipeId ORDER BY stepNr ASC")
    suspend fun getStepsForRecipe(recipeId: Long): List<RecipeDescription>

    @Query("DELETE FROM recipe_description WHERE recipeId = :recipeId")
    suspend fun deleteStepsForRecipe(recipeId: Int)

    @Update
    suspend fun updateRecipeStep(recipeStep: RecipeDescription)

    @Update
    suspend fun updateRecipeSteps(recipeSteps: List<RecipeDescription>)

    @Delete
    suspend fun deleteRecipeStep(recipeStep: RecipeDescription)

    @Query("DELETE FROM recipe_description WHERE id = :stepId")
    suspend fun deleteRecipeStepById(stepId: String)
}
