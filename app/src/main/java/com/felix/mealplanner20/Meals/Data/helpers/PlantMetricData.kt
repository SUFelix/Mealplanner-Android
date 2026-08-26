package com.felix.mealplanner20.Meals.Data.helpers

import com.felix.mealplanner20.Meals.Data.Ingredient

data class PlantMetricData(
    val distinctPlants: List<Ingredient>,
    // Weighted score: 1.0 per distinct plant, but only 0.25 per distinct spice, since a pinch of
    // spice doesn't provide the fibre/diversity benefit a full plant does.
    val count: Float,
    val target: Int = 30,
    // Keyed by the representative ingredient's id (the one kept in distinctPlants) -> every
    // ingredient sharing its plantGroupKey, so the UI can show what got merged into one plant.
    val groupedIngredients: Map<Long, List<Ingredient>> = emptyMap()
)
