package com.felix.mealplanner20.apiService

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WizardResultHolder @Inject constructor() {
    private val _pending = MutableStateFlow<WizardIngredientList?>(null)
    val pending: StateFlow<WizardIngredientList?> = _pending.asStateFlow()

    fun set(result: WizardIngredientList) {
        _pending.value = result
    }

    fun consume(): WizardIngredientList? {
        val current = _pending.value
        _pending.value = null
        return current
    }
}
