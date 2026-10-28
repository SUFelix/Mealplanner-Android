package com.felix.mealplanner20.ViewModels

import android.util.Log
import com.felix.mealplanner20.R
import com.felix.mealplanner20.apiService.WizardIngredientList
import com.felix.mealplanner20.apiService.WizardRepository
import com.felix.mealplanner20.apiService.WizardResponse
import com.felix.mealplanner20.apiService.errorText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

const val WIZARD_PROMPT_MAX_LENGTH = 500

fun isValidWizardPrompt(prompt: String): Boolean =
    prompt.trim().length in 1..WIZARD_PROMPT_MAX_LENGTH

/**
 * Zustand und Absenden des KI-Prompt-Dialogs (POST /wizard/generate).
 * Wird vom Ersteinstieg (MyOwnRecipes) und von "Mit KI anpassen" (AddEditRecipe) geteilt.
 */
class WizardPromptController(
    private val wizardRepository: WizardRepository,
    private val scope: CoroutineScope,
    private val previous: () -> WizardIngredientList?,
    private val onToast: (String) -> Unit,
    private val onSuccess: suspend (WizardIngredientList) -> Unit
) {
    private val _isOpen = MutableStateFlow(false)
    val isOpen: StateFlow<Boolean> = _isOpen.asStateFlow()

    private val _prompt = MutableStateFlow("")
    val prompt: StateFlow<String> = _prompt.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _errorRes = MutableStateFlow<Int?>(null)
    val errorRes: StateFlow<Int?> = _errorRes.asStateFlow()

    fun open() {
        _errorRes.value = null
        _isOpen.value = true
    }

    fun dismiss() {
        if (!_isGenerating.value) _isOpen.value = false
    }

    fun onPromptChange(value: String) {
        _prompt.value = value
        _errorRes.value = null
    }

    fun submit() {
        val prompt = _prompt.value.trim()
        if (_isGenerating.value || !isValidWizardPrompt(prompt)) return
        _isGenerating.value = true
        _errorRes.value = null
        scope.launch {
            try {
                when (val response = wizardRepository.generate(prompt, previous())) {
                    is WizardResponse.Success -> {
                        _isOpen.value = false
                        _prompt.value = ""
                        onSuccess(response.result)
                    }
                    WizardResponse.NotLoggedIn, is WizardResponse.Unauthorized -> {
                        _isOpen.value = false
                        response.errorText()?.let(onToast)
                    }
                    is WizardResponse.NotARecipe -> _errorRes.value = R.string.wizard_generate_not_a_recipe
                    is WizardResponse.Failure -> {
                        Log.e("Wizard", "Exception while generating recipe", response.cause)
                        _errorRes.value = if (response.isNetwork) {
                            R.string.wizard_generate_network_error
                        } else {
                            R.string.wizard_generate_error
                        }
                    }
                    is WizardResponse.Error, WizardResponse.EmptyBody -> {
                        Log.e("Wizard", "Generate failed: $response")
                        _errorRes.value = R.string.wizard_generate_error
                    }
                }
            } finally {
                _isGenerating.value = false
            }
        }
    }
}
