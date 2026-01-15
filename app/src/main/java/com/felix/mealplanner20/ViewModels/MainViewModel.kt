package com.felix.mealplanner20.ViewModels

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felix.mealplanner20.Screen
import com.felix.mealplanner20.use_cases.CleanUnusedRecipeImagesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(context: Context,private val cleanUnusedRecipeImagesUseCase: CleanUnusedRecipeImagesUseCase) : ViewModel() {
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
