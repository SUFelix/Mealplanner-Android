package com.felix.mealplanner20.Meals.Data.helpers

data class DayDetailData(
    val dayName: String,
    val nutrients: Map<String, Float>,
    val recommendations: Map<String, Float?>,
    val compliancePercentage: Float,
    val dgeData: List<DgeData>,
    val dgeMapping: List<Pair<DgeData, DgeData?>>
)

