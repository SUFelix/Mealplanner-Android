package com.felix.mealplanner20.Meals.Data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.felix.mealplanner20.Meals.Data.helpers.UnitOfMeasure
import kotlinx.coroutines.flow.Flow

@Dao
interface IngredientRecipeJoinDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ingredientRecipeJoin: IngredientWithRecipe)

   /*
    @Query("SELECT * FROM ingredient_recipe_join_table WHERE ingredientId = :ingredientId")
    suspend fun getRecipesForIngredient(ingredientId: Long): List<IngredientRecipeJoinTable>*/

    // Neue Funktion, um alle Zutaten für ein bestimmtes Rezept abzurufen
    @Query("SELECT * FROM ingredient_recipe_join_table WHERE recipeId = :recipeId")
    suspend fun getIngredientsForRecipe(recipeId: Long): List<IngredientWithRecipe>

    @Transaction
    suspend fun addIngredientToRecipe(recipeId: Long, ingredientId: Long, quantityInGrams: Float, unitOfMeasure: UnitOfMeasure,originalQuantity: Float) {
        val ingredientRecipeJoin = IngredientWithRecipe(
            recipeId = recipeId,
            ingredientId = ingredientId,
            ingredientQuantity = quantityInGrams,
            unitOfMeasure = unitOfMeasure,
            originalQuantity = originalQuantity,
        )
        insert(ingredientRecipeJoin)
    }
    @Query("UPDATE ingredient_recipe_join_table SET ingredientQuantity = :quantity WHERE recipeId = :recipeId AND ingredientId = :ingredientId")
    suspend fun updateIngredientQuantity(recipeId: Long, ingredientId: Long, quantity: Float)


    @Transaction
    @Query("SELECT * FROM ingredient_recipe_join_table WHERE recipeId = :recipeId")
    fun getRecipeWithIngredientsById(recipeId: Long): Flow<List<IngredientWithRecipe>>

    @Query("select * from `ingredient_recipe_join_table`")
    fun getAllIngredientWithRecipe(): Flow<List<IngredientWithRecipe>>

    @Query("select * from `ingredient_recipe_join_table` where recipeId = :id")
    fun getAllIngredientWithRecipe(id:Long): Flow<List<IngredientWithRecipe>>
    @Query("select * from `ingredient_recipe_join_table` where recipeId = :id")
    fun getAllIngredientWithRecipeNF(id:Long): List<IngredientWithRecipe>
    @Query("DELETE FROM ingredient_recipe_join_table WHERE recipeId = :recipeId AND ingredientId = :ingredientId")
    suspend fun deleteIngredientFromRecipe(recipeId: Long, ingredientId: Long)
}







