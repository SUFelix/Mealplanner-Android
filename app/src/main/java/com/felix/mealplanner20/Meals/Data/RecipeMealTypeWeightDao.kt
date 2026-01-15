package com.felix.mealplanner20.Meals.Data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.felix.mealplanner20.Meals.Data.helpers.Mealtype
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeMealTypeWeightDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: RecipeMealTypeWeight)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(entities: List<RecipeMealTypeWeight>)

    @Query("SELECT * FROM recipe_mealtype_weight WHERE recipe_id = :recipeId AND meal_type = :mealType LIMIT 1")
    fun observe(recipeId: Long, mealType: Mealtype): Flow<RecipeMealTypeWeight?>

    @Query("SELECT weight FROM recipe_mealtype_weight WHERE recipe_id = :recipeId AND meal_type = :mealType LIMIT 1")
    suspend fun getWeight(recipeId: Long, mealType: Mealtype): Float?

    @Query("SELECT * FROM recipe_mealtype_weight WHERE meal_type = :mealType")
    fun observeByMealType(mealType: Mealtype): Flow<List<RecipeMealTypeWeight>>

    @Query("DELETE FROM recipe_mealtype_weight WHERE recipe_id = :recipeId")
    suspend fun deleteByRecipeId(recipeId: Long)

}