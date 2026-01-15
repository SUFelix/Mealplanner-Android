package com.felix.mealplanner20.WorkManager

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.felix.mealplanner20.Meals.Data.IngredientRepository
import com.felix.mealplanner20.use_cases.SynchronizeIngredientAllowedUnitUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SynchronizeIngredientsWorkManager @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val workerParameters: WorkerParameters,
    private val ingredientRepository: IngredientRepository,
    private val synchronizeIngredientAllowedUnitUseCase: SynchronizeIngredientAllowedUnitUseCase
): CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        val successfulResult =  ingredientRepository.syncronizeIngredients()
        val allowedUnitsSuccess = synchronizeIngredientAllowedUnitUseCase()
        if(successfulResult && allowedUnitsSuccess){
            return Result.success()
        }
        else{
            return Result.failure()
        }
    }
}
