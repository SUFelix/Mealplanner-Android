package com.felix.mealplanner20.use_cases

import com.felix.mealplanner20.Meals.Data.Ingredient
import com.felix.mealplanner20.Meals.Data.IngredientRepository
import com.felix.mealplanner20.Meals.Data.MealPlanDay
import com.felix.mealplanner20.Meals.Data.MealPlanRepository
import com.felix.mealplanner20.Meals.Data.RecipeRepository
import com.felix.mealplanner20.Meals.Data.helpers.AllDayDetailsWithGlobalDge
import com.felix.mealplanner20.Meals.Data.helpers.DayDetailData
import com.felix.mealplanner20.Meals.Data.helpers.DgeData
import com.felix.mealplanner20.Meals.Data.helpers.dgeGroup
import com.felix.mealplanner20.Meals.Data.helpers.getAdjustedQuantity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

const val PROTEIN_CALORIES = 4
const val CARBS_CALORIES = 4
const val FAT_CALORIES = 9
const val ALCOHOL_CALORIES = 7
const val PROTEIN = "Protein"
const val CARBS = "Carbs"
const val FAT = "Fat"
const val SATURATED_FAT = "Sat. Fat"
const val SUGAR = "Sugar"
const val ALCOHOL = "Alcohol"
const val FIBRE = "Fibre"
class GetDayDetailsUseCase(
    private val nutritionUseCases: NutritionBasicUseCases,
    private val mealPlanRepository: MealPlanRepository,
    private val recipeRepository: RecipeRepository,
    private val ingredientRepository: IngredientRepository
) {

    suspend operator fun invoke(): Flow<AllDayDetailsWithGlobalDge> {
        return flow {
            emit(buildAllDayDetailsWithGlobalDge())
        }
    }

    suspend fun buildAllDayDetailsWithGlobalDge(): AllDayDetailsWithGlobalDge {
        val mpDays = mealPlanRepository.getAllMealPlanDays().first()
        val globalDge = calcGlobalDgeCompliance(mpDays)
        val dayDetails = buildAllDayDetails(mpDays)

        return AllDayDetailsWithGlobalDge(
            dayDetails = dayDetails,
            globalDgeCompliance = globalDge
        )
    }

    suspend fun buildAllDayDetails(mealPlanDays: List<MealPlanDay>): List<DayDetailData> {
        var i = 1
        return mealPlanDays.map { mealPlanDay ->
            val dayDetail = buildSingleDay(mealPlanDay, "Day $i")
            i++
            dayDetail
        }
    }

    suspend fun calcGlobalDgeCompliance(mealPlanDays: List<MealPlanDay>): Float {
        val globalDge = buildSummedDgeData(mealPlanDays)
        return 1f - calculateDgeDeviationViaLack(globalDge)
    }
    suspend fun buildSingleDay(mealPlanDay: MealPlanDay,dayName:String): DayDetailData {
        return DayDetailData(
            dayName = dayName,
            nutrients = getActualAbsolutNutrientDistribution(mealPlanDay),//TODO servings
            recommendations = nutritionUseCases.getMacroNutrientRecommendationsUseCase(),//TODO servings
            compliancePercentage = calculateSingleDayDgeCompliance(mealPlanDay),//TODO servings
            dgeData = getActualDgeDistribution(mealPlanDay),//TODO servings
            dgeMapping = getDGEactualAndRecommendedPairsForOneDay(mealPlanDay)//TODO servings
        )
    }
    suspend fun buildSummedDgeData(mealPlanDays: List<MealPlanDay>): List<DgeData> {
        val summedDgeData = mutableMapOf<dgeGroup, Float>()

        mealPlanDays.forEach { mealPlanDay ->
            val dgeDataList = getActualDgeDistribution(mealPlanDay)

            dgeDataList.forEach { dgeData ->
                summedDgeData[dgeData.group] = (summedDgeData[dgeData.group] ?: 0f) + dgeData.percentage
            }
        }
        val averagedDgeData = summedDgeData.map { (group, totalPercentage) ->
            DgeData(group, totalPercentage / mealPlanDays.size)
        }
        return averagedDgeData
    }

    fun calculateDgeDeviationViaLack(actualDgeList: List<DgeData>): Float {
        val recommendedDgeList = nutritionUseCases.getDgeRecommendationDataUseCase()
        var totalLack = 0f

        for (dgeData in recommendedDgeList) {
            val recommendedValue = dgeData.percentage
            val actualValue = actualDgeList.find { it.group == dgeData.group }?.percentage ?: 0f

            val lack = if (recommendedValue > actualValue) {
                Math.abs(recommendedValue - actualValue)
            } else {
                0f
            }
            totalLack += lack
        }
        return totalLack
    }

    suspend fun calculateSingleDayDgeCompliance(mealPlanDay: MealPlanDay): Float {
        val actualDgeList = getActualDgeDistribution(mealPlanDay)
        val totalLack = calculateDgeDeviationViaLack(actualDgeList)
        val compliance = (1 - totalLack) * 100
        return compliance.coerceIn(0f, 100f)
    }
    suspend fun getDGEactualAndRecommendedPairsForOneDay(mealPlanDay: MealPlanDay): List<Pair<DgeData, DgeData?>> {
        val actualDgeList = getActualDgeDistribution(mealPlanDay)
        val recommendedDgeList = nutritionUseCases.getDgeRecommendationDataUseCase()

        return actualDgeList.map { actual ->
            val recommended = recommendedDgeList.find { it.group == actual.group }
            actual to recommended
        }
    }

    private suspend fun getActualAbsolutNutrientDistribution(mealPlanDay: MealPlanDay): Map<String, Float> {
        val nutrientMap = mutableMapOf<String, Float>()
        val ingredientsWithQuantities = getIngredientsWithQuantities(mealPlanDay)

        for ((ingredient, quantity) in ingredientsWithQuantities) {
            val adjustedQuantity = getAdjustedQuantity(ingredient,quantity)

            nutrientMap[PROTEIN] = (nutrientMap[PROTEIN] ?: 0f) + ingredient.protein * adjustedQuantity
            nutrientMap[CARBS] = (nutrientMap[CARBS] ?: 0f) + ingredient.carbs * adjustedQuantity
            nutrientMap[FAT] = (nutrientMap[FAT] ?: 0f) + ingredient.fat * adjustedQuantity
            nutrientMap[SATURATED_FAT] = (nutrientMap[SATURATED_FAT] ?: 0f) + ingredient.saturatedFat * adjustedQuantity
            nutrientMap[SUGAR] = (nutrientMap[SUGAR] ?: 0f) + ingredient.sugar * adjustedQuantity
            nutrientMap[ALCOHOL] = (nutrientMap[ALCOHOL] ?: 0f) + ingredient.alcohol * adjustedQuantity
            nutrientMap[FIBRE] = (nutrientMap[FIBRE] ?: 0f) + ingredient.fibre * adjustedQuantity
        }
        return nutrientMap
    }

    private suspend fun getActualDgeDistribution(mealPlanDay: MealPlanDay): List<DgeData> {
        val dgeMap = mutableMapOf<dgeGroup, Float>()
        var totalCalories = 0f

        val ingredientsWithQuantities = getIngredientsWithQuantities(mealPlanDay)

        for ((ingredient, quantity) in ingredientsWithQuantities) {
            val calories = nutritionUseCases.calculateCaloriesUseCase.calculateCaloriesForIngredientQtyPair(Pair(ingredient, quantity))
            totalCalories += calories

            val dgeType = ingredient.dgeType
            dgeMap[dgeType] = (dgeMap[dgeType] ?: 0f) + calories
        }

        val dgeRecommendation = nutritionUseCases.getDgeRecommendationDataUseCase()

        val dgeDataList = mutableListOf<DgeData>()
        for (dgeData in dgeRecommendation) {
            val dgeType = dgeData.group
            val caloriesForGroup = dgeMap[dgeType] ?: 0f
            val percentage = if (totalCalories > 0) (caloriesForGroup / totalCalories) else 0f
            dgeDataList.add(DgeData(group = dgeType, percentage = percentage))
        }
        return dgeDataList
    }

    private suspend fun getIngredientsWithQuantities(mealPlanDay: MealPlanDay): List<Pair<Ingredient, Float>> {
        val ingredientsWithQuantities = mutableListOf<Pair<Ingredient, Float>>()

        val recipeQuantities = mealPlanRepository
            .getRecipeQuantitiesFlowForDay(mealPlanDay.id)
            .first()

        for (recipeQuantity in recipeQuantities) {
            val recipeId = recipeQuantity.recipeId
            val quantity = recipeQuantity.quantity

            val ingredientsWithRecipe = recipeRepository.getIngredientsForRecipe(recipeId)

            val servings = recipeRepository.getRecipeById(recipeId).first().servings

            val ingredientIds = ingredientsWithRecipe.map { it.ingredientId }
            val ingredientQuantities = ingredientsWithRecipe.associate {
                it.ingredientId to (it.ingredientQuantity / servings)
            }

            val ingredients = ingredientRepository.getIngredientListByIdList(ingredientIds)

            for (ingredient in ingredients) {
                val baseQuantity = ingredientQuantities[ingredient.id] ?: 1f
                val totalQuantity = baseQuantity * quantity
                ingredientsWithQuantities.add(ingredient to totalQuantity)
            }
        }
        return ingredientsWithQuantities
    }
}
