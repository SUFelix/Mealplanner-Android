package com.felix.mealplanner20.Meals.Data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MealPlanDayRecipeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: MealPlanDayRecipeEntity)

    @Update
    suspend fun update(entry: MealPlanDayRecipeEntity)

    @Query("""
    UPDATE mealplanday_recipe_table
    SET recipeId = :newRecipeId
    WHERE mealPlanDayId = :mealPlanDayId AND recipeId = :oldRecipeId
""")
    suspend fun replaceRecipe(mealPlanDayId: Long, oldRecipeId: Long, newRecipeId: Long)


    @Delete
    suspend fun delete(entry: MealPlanDayRecipeEntity)

    @Query("SELECT * FROM mealplanday_recipe_table WHERE mealPlanDayId = :dayId")
    suspend fun getRecipesForDay(dayId: Long): List<MealPlanDayRecipeEntity>

    @Query("SELECT * FROM mealplanday_recipe_table WHERE mealPlanDayId = :dayId")
    fun getRecipesFlowForDay(dayId: Long): Flow<List<MealPlanDayRecipeEntity>>

    @Query("SELECT * FROM mealplanday_recipe_table WHERE mealPlanDayId = :dayId AND recipeId = :recipeId")
    suspend fun getRecipeEntry(dayId: Long, recipeId: Long): MealPlanDayRecipeEntity?

    @Query("SELECT recipeId FROM mealplanday_recipe_table WHERE mealPlanDayId = :dayId")
    fun getRecipeIdsFlowForDay(dayId: Long): Flow<List<Long>>
}

@Dao
interface MealPlanDayDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(day: MealPlanDay): Long

    @Update
    suspend fun update(day: MealPlanDay)

    @Delete



    suspend fun delete(day: MealPlanDay)

    @Query("DELETE FROM mealplanday_table")
    suspend fun deleteAll()


    @Query("SELECT * FROM mealplanday_table WHERE id = :dayId")
    suspend fun getDay(dayId: Long): MealPlanDay

    @Query("SELECT * FROM mealplanday_table")
    fun getAllMealPlanDays(): Flow<List<MealPlanDay>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMealPlanDays(days: List<MealPlanDay>): List<Long>

}
