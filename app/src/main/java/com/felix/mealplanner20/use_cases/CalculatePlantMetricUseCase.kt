package com.felix.mealplanner20.use_cases

import com.felix.mealplanner20.Meals.Data.Ingredient
import com.felix.mealplanner20.Meals.Data.helpers.PlantMetricData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CalculatePlantMetricUseCase @Inject constructor() {
    operator fun invoke(ingredientsFlow: Flow<List<Ingredient>>): Flow<PlantMetricData> {
        return ingredientsFlow.map { ingredients ->
            val distinctPlants = ingredients.filter { it.isPlant() }.distinctBy { it.id }
            PlantMetricData(distinctPlants = distinctPlants, count = distinctPlants.size)
        }
    }
}
