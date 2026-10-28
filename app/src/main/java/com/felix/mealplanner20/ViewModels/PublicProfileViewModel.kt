package com.felix.mealplanner20.ViewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felix.mealplanner20.Meals.Data.EMPTY_STRING
import com.felix.mealplanner20.Meals.Data.ProfileRepository
import com.felix.mealplanner20.Meals.Data.Recipe
import com.felix.mealplanner20.Meals.Data.RecipeRepository
import com.felix.mealplanner20.Meals.Data.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class PublicProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val recipeRepository: RecipeRepository,
    settingsRepository: SettingsRepository
) : ViewModel() {

    val showOriginalTitle = settingsRepository.observeShowOriginalTitle()

    private val _username = MutableStateFlow(EMPTY_STRING)
    val username: StateFlow<String> = _username

    private val _pictureUri = MutableStateFlow<String?>(null)
    val pictureUri: StateFlow<String?> = _pictureUri

    private val _description = MutableStateFlow(EMPTY_STRING)
    val description: StateFlow<String> = _description

    private val _recipeCount = MutableStateFlow(0)
    val recipeCount: StateFlow<Int> = _recipeCount

    private val _isProfileLoading = MutableStateFlow(true)
    val isProfileLoading: StateFlow<Boolean> = _isProfileLoading

    private val _profileNotFound = MutableStateFlow(false)
    val profileNotFound: StateFlow<Boolean> = _profileNotFound

    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private var loadedForUsername: String? = null

    fun loadProfile(username: String) {
        if (loadedForUsername == username) return
        loadedForUsername = username

        _recipes.value = emptyList()
        _profileNotFound.value = false

        viewModelScope.launch {
            _isProfileLoading.value = true
            try {
                val profile = profileRepository.getPublicProfile(username)
                if (profile != null) {
                    Log.i("PublicProfileViewModel", "profile '$username' pictureUri = '${profile.pictureUri}'")
                    _username.value = profile.username
                    _pictureUri.value = profile.pictureUri
                    _description.value = profile.description ?: EMPTY_STRING
                    _recipeCount.value = profile.recipeCount
                } else {
                    _username.value = username
                    _profileNotFound.value = true
                }
            } catch (e: Exception) {
                Log.e("PublicProfileViewModel", "Error loading public profile for $username", e)
                _profileNotFound.value = true
            } finally {
                _isProfileLoading.value = false
            }
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                recipeRepository.getRecipesForUserFromServer(username)?.let {
                    _recipes.value = it
                }
            } catch (e: Exception) {
                Log.e("PublicProfileViewModel", "Error loading recipes for $username", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
