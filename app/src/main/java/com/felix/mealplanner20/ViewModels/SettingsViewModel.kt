package com.felix.mealplanner20.ViewModels

import android.net.Uri
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felix.mealplanner20.Meals.Data.BREAKFASTS_PER_DAY_DEFAULT_VALUE
import com.felix.mealplanner20.Meals.Data.CALORIE_REQUIREMENT_DEFAULT_VALUE
import com.felix.mealplanner20.Meals.Data.FAT_REQUIREMENT_DEFAULT_VALUE
import com.felix.mealplanner20.Meals.Data.MEALS_PER_DAY_DEFAULT_VALUE
import com.felix.mealplanner20.Meals.Data.PLANNING_HORIZON_IN_DAYS_DEFAULT_VALUE
import com.felix.mealplanner20.Meals.Data.PROTEIN_REQUIREMENT_DEFAULT_VALUE
import com.felix.mealplanner20.Meals.Data.SNACKS_PER_DAY_DEFAULT_VALUE
import com.felix.mealplanner20.Meals.Data.Settings
import com.felix.mealplanner20.Meals.Data.SettingsRepository
import com.felix.mealplanner20.Meals.Data.helpers.Language
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor (private val repository: SettingsRepository) : ViewModel() {
    var mealsPerDay by mutableStateOf(MEALS_PER_DAY_DEFAULT_VALUE)
        private set

    var breakfastsPerDay by mutableStateOf(BREAKFASTS_PER_DAY_DEFAULT_VALUE)
        private set

    var snacksPerDay by mutableStateOf(SNACKS_PER_DAY_DEFAULT_VALUE)
        private set

    var planningHorizonInDays by mutableStateOf(PLANNING_HORIZON_IN_DAYS_DEFAULT_VALUE)
        private set
    var vegan by mutableStateOf(false)
        private set
    var vegetarian by mutableStateOf(false)
        private set
    var language by mutableStateOf(Language.ENGLISH)
        private set
    var calorieRequirement by mutableStateOf(CALORIE_REQUIREMENT_DEFAULT_VALUE)
        private set
    var proteinRequirement by mutableStateOf(PROTEIN_REQUIREMENT_DEFAULT_VALUE)
        private set
    var fatRequirement by mutableStateOf(FAT_REQUIREMENT_DEFAULT_VALUE)
        private set
    var showOriginalTitle by mutableStateOf(false)
        private set

   // private val _localProfilPictureimgUri = mutableStateOf<Uri?>(null)
   // val localProfilPictureimgUri: State<Uri?> = _localProfilPictureimgUri

    private val _showResetSettingsAlertDialog = MutableStateFlow(false)
    val showResetSettingsAlertDialog: StateFlow<Boolean> = _showResetSettingsAlertDialog

    fun toggleResetSettingsDialog(show: Boolean) {
        _showResetSettingsAlertDialog.value = show
    }

init {
    loadSettings()
}
    fun loadSettings() = viewModelScope.launch {
        val settings = repository.getSettings()
        if (settings != null) {
            mealsPerDay = settings.mealsPerDay
            breakfastsPerDay = settings.breakfastsPerDay
            snacksPerDay = settings.snacksPerDay
            planningHorizonInDays = settings.planningHorizonInDays
            vegan = settings.vegan
            vegetarian = settings.vegetarian
            language = settings.language
            calorieRequirement = settings.calorieRequirement
            proteinRequirement = settings.proteinRequirement
            fatRequirement = settings.fatRequirement
            showOriginalTitle = settings.showOriginalTitle
        } else {
            saveSettings()
        }
    }

    fun saveSettings() = viewModelScope.launch {
        val settings = Settings(
            vegan = vegan,
            vegetarian = vegetarian,
            language = language,
            calorieRequirement = calorieRequirement,
            proteinRequirement = proteinRequirement,
            fatRequirement = fatRequirement,
            mealsPerDay = mealsPerDay,
            breakfastsPerDay = breakfastsPerDay,
            snacksPerDay = snacksPerDay,
            planningHorizonInDays = planningHorizonInDays,
            showOriginalTitle = showOriginalTitle
        )
        repository.saveSettings(settings)
    }

    fun updateVegan(value: Boolean) {
        vegan = value
    }

    fun updateMealsPerDay(value: Int) {
        mealsPerDay = value
    }

    fun updateBreakfastsPerDay(value: Int) {
        breakfastsPerDay = value
    }
    fun updateSnacksPerDay(value: Int) {
        snacksPerDay = value
    }

    fun updatePlanningHorizonInDays(value: Int) {
        planningHorizonInDays = value
    }

    fun updateVegetarian(value: Boolean) {
        vegetarian = value
    }

    fun updateLanguage(value: Language) {
        language = value
    }

    fun updateCalorieRequirement(value: Int) {
        calorieRequirement = value
        updateFatRequirement(value/30) //30% der energie 9kcal pro g fat, 30 = 9 * 3,33
        updateProteinRequirement(value/20) //20% der erergie 4kcal pro protein, 20 = 4*5
    }

    fun updateProteinRequirement(value: Int) {
        proteinRequirement = value
    }

    fun updateFatRequirement(value: Int) {
        fatRequirement = value
    }

    fun updateShowOriginalTitle(value: Boolean) {
        showOriginalTitle = value
    }

    fun resetSettings() = viewModelScope.launch {
        repository.deleteAllSettings()
        vegan = false
        vegetarian = false
        mealsPerDay = MEALS_PER_DAY_DEFAULT_VALUE
        breakfastsPerDay = BREAKFASTS_PER_DAY_DEFAULT_VALUE
        snacksPerDay = SNACKS_PER_DAY_DEFAULT_VALUE
        planningHorizonInDays = PLANNING_HORIZON_IN_DAYS_DEFAULT_VALUE
        calorieRequirement = CALORIE_REQUIREMENT_DEFAULT_VALUE
        proteinRequirement = PROTEIN_REQUIREMENT_DEFAULT_VALUE
        fatRequirement = FAT_REQUIREMENT_DEFAULT_VALUE
        showOriginalTitle = false
        language = Language.ENGLISH
        saveSettings()
    }

    fun onSnacksperDayValueChange(newValue: Int) {
        snacksPerDay = newValue
    }
    fun onMealssperDayValueChange(newValue: Int) {
        mealsPerDay = newValue
    }
    fun onBreakfastsperDayValueChange(newValue: Int) {
        breakfastsPerDay = newValue
    }
}
