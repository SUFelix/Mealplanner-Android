package com.felix.mealplanner20.ViewModels

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felix.mealplanner20.Meals.Data.Settings
import com.felix.mealplanner20.Meals.Data.SettingsRepository
import com.felix.mealplanner20.Screen
import com.felix.mealplanner20.use_cases.CleanUnusedRecipeImagesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    context: Context,
    private val cleanUnusedRecipeImagesUseCase: CleanUnusedRecipeImagesUseCase,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _showOnboarding = MutableStateFlow<Boolean?>(null)
    val showOnboarding: StateFlow<Boolean?> = _showOnboarding

    init {
        viewModelScope.launch {
            val settings = settingsRepository.getSettings()
            _showOnboarding.value = settings == null || !settings.hasSeenOnboarding
        }
    }

    fun markOnboardingSeen() {
        viewModelScope.launch {
            withContext(NonCancellable) {
                val s = settingsRepository.getSettings() ?: Settings()
                settingsRepository.saveSettings(s.copy(hasSeenOnboarding = true))
            }
            _showOnboarding.value = false
        }
    }

    fun showOnboardingAgain() {
        _showOnboarding.value = true
    }
    private val _currentScreen: MutableState<Screen> = mutableStateOf(Screen.BottomScreen.CatalogScreen(context))

    val currentScreen: MutableState<Screen>
        get() = _currentScreen


    fun setCurrentScreen(screen: Screen) {
        _currentScreen.value = screen
    }

    private val _currentTopAppBarTitle: MutableState<String> = mutableStateOf("Home")

    val currentTopAppBarTitle: MutableState<String>
        get() = _currentTopAppBarTitle


    fun setCurrentTopAppBarTitle(title: String) {
        _currentTopAppBarTitle.value = title
    }

    private val _showExitWithoutSaveAlertDialog = mutableStateOf(false)
    val showExitWithoutSaveAlertDialog: State<Boolean> = _showExitWithoutSaveAlertDialog

    private val _changesMade = MutableStateFlow<Boolean>(false)
    val changesMade: StateFlow<Boolean> = _changesMade

    private val _isEditingRecipe = MutableStateFlow(false)
    val isEditingRecipe: StateFlow<Boolean> = _isEditingRecipe

    fun setRecipeEditMode(isEditing: Boolean) { _isEditingRecipe.value = isEditing }
    fun toggleRecipeEditMode() { _isEditingRecipe.value = !_isEditingRecipe.value }

    private val _isDirtyRecipe = MutableStateFlow(false)
    val isDirtyRecipe: StateFlow<Boolean> = _isDirtyRecipe

    fun setRecipeDirty(dirty: Boolean) { _isDirtyRecipe.value = dirty }

    fun cleanUnusedRecipeImages(context: Context){
        viewModelScope.launch {
            cleanUnusedRecipeImagesUseCase.invoke(context)
        }
    }

    fun setShowExitWithoutSaveAlertDialog(newValue:Boolean){
        _showExitWithoutSaveAlertDialog.value = newValue
    }
    fun setChangesMade(newValue: Boolean){
        _changesMade.value = newValue
    }
}
