package com.felix.mealplanner20.Meals.Data.helpers

import com.felix.mealplanner20.Meals.Data.Ingredient

data class PlantMetricData(
    val distinctPlants: List<Ingredient>,
    val count: Int,
    val target: Int = 30
)
