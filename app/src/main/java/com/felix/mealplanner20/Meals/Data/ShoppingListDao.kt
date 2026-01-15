package com.felix.mealplanner20.Shopping.Data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingListDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addShoppingListItem(item: ShoppingListItem)

    @Query("SELECT * FROM shopping_list_table")
    fun getAllShoppingListItems(): Flow<List<ShoppingListItem>>

    @Query("DELETE FROM shopping_list_table WHERE id = :id")
    suspend fun deleteShoppingListItem(id: Long)

    @Query("DELETE FROM shopping_list_table")
    suspend fun clearShoppingList()
}
