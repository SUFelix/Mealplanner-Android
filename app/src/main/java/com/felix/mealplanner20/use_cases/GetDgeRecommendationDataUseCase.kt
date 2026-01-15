package com.felix.mealplanner20.use_cases

import com.felix.mealplanner20.Meals.Data.helpers.DgeData
import com.felix.mealplanner20.Meals.Data.helpers.dgeGroup

class GetDgeRecommendationDataUseCase() {
    operator fun invoke(): List<DgeData> {
        return listOf(
            DgeData(dgeGroup.FRUIT, 0.073046241f),
            DgeData(dgeGroup.VEGETABLE, 0.06f),
            DgeData(dgeGroup.OTHER, 0.011962199f),
            DgeData(dgeGroup.LEGUME, 0.016142324f),
            DgeData(dgeGroup.NUTSANDSEEDS, 0.075594455f),
            DgeData(dgeGroup.POTATO, 0.011629916f),
            DgeData(dgeGroup.GRAIN, 0.0f),
            DgeData(dgeGroup.WHOLEGRAIN, 0.502412377f),
            DgeData(dgeGroup.OIL, 0.079548626f),
            DgeData(dgeGroup.MILK, 0.097691296f),
            DgeData(dgeGroup.FISH, 0.023924399f),
            DgeData(dgeGroup.MEAT, 0.041867698f),
            DgeData(dgeGroup.EGG, 0.00618047f)
        )
    }
}
