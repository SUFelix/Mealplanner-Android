package com.felix.mealplanner20.Meals.Data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SettingsRepository(private val settingsDao: SettingsDao) {

    suspend fun getSettings(): Settings? {
        return withContext(Dispatchers.IO) {
            settingsDao.getSettings(0)
        }
    }

    suspend fun saveSettings(settings: Settings) {
        withContext(Dispatchers.IO) {
            if (settingsDao.getSettings(0) == null) {
                settingsDao.insertSettings(settings)
            } else {
                settingsDao.updateSettings(settings)
            }
        }
    }

    suspend fun deleteAllSettings() {
        withContext(Dispatchers.IO) {
            settingsDao.deleteAllSettings()
        }
    }

    fun observeSettings(): Flow<Settings?> = settingsDao.observeSettings()

    fun observeShowOriginalTitle(): Flow<Boolean> =
        settingsDao.observeSettings()
            .map { it?.showOriginalTitle ?: false }
            .distinctUntilChanged()
}
