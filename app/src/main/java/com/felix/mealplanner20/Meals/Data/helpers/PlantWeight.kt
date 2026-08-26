package com.felix.mealplanner20.Meals.Data.helpers

import com.felix.mealplanner20.Meals.Data.Ingredient

// A pinch of spice doesn't provide the fibre/diversity benefit a full plant does, so it only
// contributes a quarter of a point towards the 30-plants-a-week target.
const val SPICE_PLANT_WEIGHT = 0.25f

// Reserved plantGroupKey for heavily processed plant products (juice, purees, ...) that no
// longer provide the fibre/diversity benefit of a whole plant, so they don't count at all.
// This is a marker, not a real grouping key: unrelated ingredients (apple juice, orange juice,
// ...) can share it without being deduped together as if they were the same plant.
const val ZERO_PLANT_GROUP_KEY = "zero"

fun Ingredient.countsTowardsPlantMetric(): Boolean {
    val isZeroTagged = plantGroupKey?.equals(ZERO_PLANT_GROUP_KEY, ignoreCase = true) == true
    return isPlant() && !isZeroTagged
}

fun Ingredient.plantWeight(): Float = if (dgeType == dgeGroup.SPICE) SPICE_PLANT_WEIGHT else 1f
