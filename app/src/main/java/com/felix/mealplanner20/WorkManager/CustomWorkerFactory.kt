package com.felix.mealplanner20.WorkManager

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.felix.mealplanner20.Meals.Data.IngredientRepository
import com.felix.mealplanner20.use_cases.SynchronizeIngredientAllowedUnitUseCase
import javax.inject.Inject

class CustomWorkerFactory @Inject constructor(
    private val ingredientRepository: IngredientRepository,
    private val synchronizeIngredientAllowedUnitUseCase:SynchronizeIngredientAllowedUnitUseCase
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParams: WorkerParameters
    ): ListenableWorker = SynchronizeIngredientsWorkManager(appContext,workerParams,ingredientRepository, synchronizeIngredientAllowedUnitUseCase)
}
