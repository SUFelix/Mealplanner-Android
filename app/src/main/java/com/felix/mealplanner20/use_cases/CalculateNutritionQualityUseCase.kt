package com.felix.mealplanner20.use_cases

import android.util.Log
import com.felix.mealplanner20.Meals.Data.helpers.AllDayDetailsWithGlobalDge
import com.felix.mealplanner20.Meals.Data.helpers.DayDetailData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CalculateNutritionQualityUseCase @Inject constructor(private val nutritionBasicUseCases: NutritionBasicUseCases) {
    operator fun invoke(allDayDetailsWithGlobalDgeFlow:Flow<AllDayDetailsWithGlobalDge>): Flow<Int> {
        return calculateNutritionQuality(allDayDetailsWithGlobalDgeFlow)
    }
    private fun calculateNutritionQuality(allDayDetailsWithGlobalDgeFlow: Flow<AllDayDetailsWithGlobalDge>): Flow<Int> {
        //TODO: welche Logik will ich verwenden? Kombi aus Macro global, dge global? dge daily??, macro daily?
        return allDayDetailsWithGlobalDgeFlow.map { allDayDetailsWithGlobalDge ->
            val details = allDayDetailsWithGlobalDge.dayDetails
            if (details.isEmpty()) {
                return@map 0
            }
            val totalCompliance = details.fold(0f) { acc, detail ->
                acc + calculateDetailCompliance(detail)
            }
            val dgeGlobal = allDayDetailsWithGlobalDge.globalDgeCompliance *100
            val averageMacroCompliance = totalCompliance / details.size
            ((averageMacroCompliance+dgeGlobal)/2).toInt()
        }
    }

//TODO hier muss nach calorien gewichtet werden, bzw die macro nährwsoffe addiert und dann als ein tag betrachtet
    fun calculateOverallNutrientCompliance(dayDetailsFlow: Flow<List<DayDetailData>>): Flow<Int> {
        return dayDetailsFlow.map { details ->
            if (details.isEmpty()) {
                return@map 0
            }

            val totalCompliance = details.fold(0f) { acc, detail ->
                acc + calculateDetailCompliance(detail)
            }

            val averageCompliance = totalCompliance / details.size
            averageCompliance.toInt()
        }
    }


    private fun calculateDetailCompliance(detail: DayDetailData): Float {
        val averageNutrientCompliance = calculateAverageNutrientCompliance(detail)
        return (detail.compliancePercentage + averageNutrientCompliance) / 2
    }

    private fun calculateAverageNutrientCompliance(detail: DayDetailData): Float {
        var nutrientComplianceSum = 0f
        var nutrientCount = 0


        detail.nutrients.forEach { (nutrient, actualValue) ->
            val recommendedValue = detail.recommendations[nutrient]
            if (recommendedValue != null && recommendedValue > 0) {

                var complianceRatioSingleNutrient = 0f
                if (nutritionBasicUseCases.getMacroNutrientRecommendationsUseCase.isSugarOrAlcohol(nutrient) && actualValue<recommendedValue ) {
                    complianceRatioSingleNutrient = 100f
                }
                else{
                complianceRatioSingleNutrient =
                    calculateComplianceRatio(actualValue, recommendedValue)
            }
                //TODO gewichtung der verschiedenen nutrients zB Protein wichtiger als carbs, oder sugar...
                nutrientComplianceSum += complianceRatioSingleNutrient
                nutrientCount++
            }
        }

        return if (nutrientCount > 0) {
            nutrientComplianceSum / nutrientCount
        } else {
            0f
        }
    }

    private fun calculateComplianceRatio(actualValue: Float, recommendedValue: Float): Float {
        val (smallerValue, largerValue) = if (actualValue < recommendedValue) {
            actualValue to recommendedValue
        } else {
            recommendedValue to actualValue
        }

        if (largerValue == 0f) return 100f //wenn der größere wert Null ist sollten beide null sein, und dann ergibt compliance 100% sinn

        return (smallerValue / largerValue) * 100
    }
}