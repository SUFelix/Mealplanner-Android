package com.felix.mealplanner20.Meals.Data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface IngredientDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addIngredient(ingredientEntity: Ingredient):Long
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addIngredients(ingredientEntitys: List<Ingredient>):List<Long>
    @Query("SELECT * FROM `ingredient_table` ORDER BY id ASC")
    fun readAllData():LiveData<List<Ingredient>>
    @Update
    suspend fun updateIngredient(ingredientEntity:Ingredient)
    @Delete
    suspend fun deleteIngredient(ingredientEntity:Ingredient)
    @Query("select * from `ingredient_table`")
    fun getAllIngredients(): Flow<List<Ingredient>>

    @Query("select * from `ingredient_table` where id=:id")
    fun getIngredientById(id:Long): Flow<Ingredient>

    @Query("SELECT * FROM ingredient_table WHERE id IN (:id)")
    fun getIngredientListByIdList(id: List<Long>): Flow<List<Ingredient>>
    @Query("SELECT * FROM ingredient_table WHERE id IN (:id)")
    suspend fun getIngredientsByIds(id: List<Long>): List<Ingredient>
}