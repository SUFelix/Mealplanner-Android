package com.felix.mealplanner20.Meals.Data

import android.net.Uri
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addRecipe(recipeEntity: Recipe):Long

    @Delete
    suspend fun deleteRecipe(recipeEntity:Recipe)
    @Query("DELETE FROM recipe_table WHERE id = :id")
    suspend fun deleteRecipeById(id:Long)

    @Query("SELECT * FROM recipe_table WHERE (:isVegan = 0 OR isVegan = 1) AND (:isVegetarian = 0 OR isVegetarian = 1)")
    fun getAllRecipes(isVegan: Boolean = false, isVegetarian: Boolean = false): Flow<List<Recipe>>
    @Query("select * from `recipe_table` WHERE isBreakfast=1")
    fun getAllBreakfasts(): Flow<List<Recipe>>

    @Query("select * from `recipe_table` WHERE isSnack=1")
    fun getAllSnacks(): Flow<List<Recipe>>

    @Query("select * from `recipe_table` WHERE isBeverage=1")
    fun getAllBeverages(): Flow<List<Recipe>>
    @Query("select * from `recipe_table` WHERE isMeal=1")
    fun getAllMeals(): Flow<List<Recipe>>

    @Query("SELECT COUNT(*) FROM recipe_table WHERE id = :recipeId")
    suspend fun isRecipeExist(recipeId: Long): Int

    @Query("select * from `recipe_table` where isFavorit=1 and ((:isVegan = 0 OR isVegan = 1) AND (:isVegetarian = 0 OR isVegetarian = 1))")
    fun getAllFavoriteRecipes(isVegan: Boolean = false, isVegetarian: Boolean = false): Flow<List<Recipe>>

    @Query("select * from `recipe_table` where id=:id")
    fun getRecipeById(id:Long): Flow<Recipe>
    @Query("UPDATE recipe_table SET probability = :newProbability WHERE id = :recipeId")
    suspend fun updateProbability(recipeId: Long, newProbability: Float)


    @Query("""
    SELECT r.id AS recipe_id, 
           r.`recipe-name` AS recipe_name,
           r.caloriesPerServing AS total_calories
    FROM recipe_table r
""")
    fun getRecipeCaloriesPerServing(): Flow<List<RecipeCalories>>



    @Query("SELECT * FROM recipe_table WHERE id IN (:ids)")
     fun getRecipesByIds(ids: List<Long>): Flow<List<Recipe>>

    @Query("""
    UPDATE recipe_table
    SET `recipe-name` = :newTitle,
        isMeal = :newIsMeal,
        isBreakfast = :newIsBreakfast,
        isSnack = :newIsSnack,
        isBeverage = :newIsBeverage,
        isDessert = :newIsDessert,
        isVegan = :isVegan,
        isVegetarian = :isVegetarian,
        imgUri = :newImgUri,
        createdBy = :newCreatedBy,
        servings = :newServings,
        cpfRatio = :newCPFratio,
        caloriesPerServing = :newCaloriesPerserving
    WHERE id = :recipeId
""")
    suspend fun updateRecipeTitleAndType(
        recipeId: Long,
        newTitle: String,
        newIsMeal: Boolean,
        newIsBreakfast: Boolean,
        newIsSnack: Boolean,
        newIsBeverage:Boolean,
        newIsDessert:Boolean,
        isVegan:Boolean,
        isVegetarian:Boolean,
        newImgUri: Uri?,
        newCreatedBy:String?,
        newServings:Float,
        newCPFratio:MacronutrientRatio,
        newCaloriesPerserving:Float
    )

@Query("""UPDATE recipe_table SET remote_id = :remoteId WHERE id = :recipeId""")
suspend fun setRemoteId(recipeId: Long, remoteId: Long)
}