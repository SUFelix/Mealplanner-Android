package com.felix.mealplanner20.ViewModels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felix.mealplanner20.Meals.Data.Recipe
import com.felix.mealplanner20.Meals.Data.RecipeCalories
import com.felix.mealplanner20.Meals.Data.RecipeRepository
import com.felix.mealplanner20.Meals.Data.SettingsRepository
import com.felix.mealplanner20.Meals.Data.helpers.uriToByteArray
import com.felix.mealplanner20.apiService.WizardApiService
import com.felix.mealplanner20.apiService.WizardIngredientList
import com.felix.mealplanner20.apiService.WizardResultHolder
import com.felix.mealplanner20.use_cases.NutritionBasicUseCases
import com.mealplanner20.jwtauthktorandroid.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@HiltViewModel
class MyOwnRecipesViewModel @Inject constructor (
    private val recipeRepository: RecipeRepository,
    private val nutritionUseCases: NutritionBasicUseCases,
    private val settingsRepository: SettingsRepository,
    private val wizardApiService: WizardApiService,
    private val authRepository: AuthRepository,
    private val wizardResultHolder: WizardResultHolder
): ViewModel() {

    lateinit var getAllRecipes: Flow<List<Recipe>>
    lateinit var getAllCalories: Flow<List<RecipeCalories>>



    private val _isLoading = MutableStateFlow<Boolean>(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isWizardLoading = MutableStateFlow(false)
    val isWizardLoading: StateFlow<Boolean> = _isWizardLoading

    private val _wizardResult = MutableStateFlow<WizardIngredientList?>(null)
    val wizardResult: StateFlow<WizardIngredientList?> = _wizardResult

    private val _wizardError = MutableStateFlow<String?>(null)
    val wizardError: StateFlow<String?> = _wizardError

    private val _navigateToWizardRecipe = MutableStateFlow(false)
    val navigateToWizardRecipe: StateFlow<Boolean> = _navigateToWizardRecipe

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            try{
                getAllRecipes = recipeRepository.getAllRecipes()
                getAllCalories = nutritionUseCases.getRecipeCaloriesUseCase()
            }catch (e:Exception){
                Log.e("Error loading Data",e.toString())
            }finally {
                _isLoading.value = false
            }
        }
    }

    fun runWizardForImage(context: Context, uri: Uri) {
        viewModelScope.launch {
            _isWizardLoading.value = true
            _wizardError.value = null
            _wizardResult.value = null
            try {
                val token = authRepository.getToken()
                if (token == null) {
                    _wizardError.value = "Not logged in"
                    return@launch
                }
                val bytes = uriToByteArray(context, uri)
                if (bytes == null) {
                    _wizardError.value = "Could not read image"
                    return@launch
                }
                val requestFile = bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("image", "wizard.jpg", requestFile)
                val headers = mapOf("Authorization" to "Bearer $token")

                val response = wizardApiService.analyzeRecipeImage(body, headers)
                if (response.isSuccessful) {
                    val result = response.body()
                    _wizardResult.value = result
                    if (result != null) {
                        wizardResultHolder.set(result)
                        _navigateToWizardRecipe.value = true
                    }
                } else {
                    _wizardError.value = "Wizard failed: ${response.code()} ${response.message()}"
                    Log.e("Wizard", "API error ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                _wizardError.value = e.message ?: "Unknown error"
                Log.e("Wizard", "Exception while analyzing image", e)
            } finally {
                _isWizardLoading.value = false
            }
        }
    }

    fun consumeWizardResult() {
        _wizardResult.value = null
        _wizardError.value = null
    }

    fun consumeNavigateToWizardRecipe() {
        _navigateToWizardRecipe.value = false
    }
}
