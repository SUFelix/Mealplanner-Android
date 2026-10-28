package com.felix.mealplanner20.use_cases

import com.felix.mealplanner20.Meals.Data.Ingredient
import com.felix.mealplanner20.Meals.Data.helpers.PlantMetricData
import com.felix.mealplanner20.Meals.Data.helpers.countsTowardsPlantMetric
import com.felix.mealplanner20.Meals.Data.helpers.plantWeight
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CalculatePlantMetricUseCase @Inject constructor() {

    operator fun invoke(ingredientsFlow: Flow<List<Ingredient>>): Flow<PlantMetricData> {
        return ingredientsFlow.map { ingredients ->
            val groups = ingredients.filter { it.countsTowardsPlantMetric() }
                .groupBy { it.plantGroupKey ?: it.id.toString() }
                .values
            val distinctPlants = groups.map { it.first() }
            val groupedIngredients = groups.associate { it.first().id to it }
            val count = distinctPlants.fold(0f) { acc, plant -> acc + plant.plantWeight() }
            PlantMetricData(
                distinctPlants = distinctPlants,
                count = count,
                groupedIngredients = groupedIngredients
            )
        }
    }
}
