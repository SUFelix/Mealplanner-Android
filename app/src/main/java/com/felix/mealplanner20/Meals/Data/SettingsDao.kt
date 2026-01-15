package com.felix.mealplanner20.Meals.Data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface SettingsDao {

    @Query("SELECT * FROM settings_table WHERE id = :id")
    suspend fun getSettings(id: Int = 0): Settings?
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: Settings)

    @Update
    suspend fun updateSettings(settings: Settings)

    @Query("DELETE FROM settings_table")
    suspend fun deleteAllSettings()

    @Query("SELECT * FROM settings_table WHERE id = :id LIMIT 1")
    fun observeSettings(id: Int = 0): Flow<Settings?>
}
