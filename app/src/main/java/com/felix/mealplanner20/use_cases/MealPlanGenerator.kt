package com.felix.mealplanner20.use_cases

import com.felix.mealplanner20.Meals.Data.MealPlanDay
import com.felix.mealplanner20.Meals.Data.MealPlanRepository
import com.felix.mealplanner20.Meals.Data.Recipe
import com.felix.mealplanner20.Meals.Data.RecipeCalories
import com.felix.mealplanner20.Meals.Data.RecipeRepository
import com.felix.mealplanner20.Meals.Data.SettingsRepository
import com.felix.mealplanner20.Meals.Data.defaultSettings
import com.felix.mealplanner20.Meals.Data.helpers.MealData
import com.felix.mealplanner20.Meals.Data.helpers.Mealtype
import com.felix.mealplanner20.Meals.Data.helpers.RecipeQuantity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlin.math.abs
import kotlin.random.Random

const val INIT_MEALPLANDAY_QUANTITY = 1f
const val CALORIE_GENERATE_TOLERANCE = 100f
class MealPlanGenerator(
    private val mealPlanRepository: MealPlanRepository,
    private val recipeRepository: RecipeRepository,
    private val settingsRepository: SettingsRepository,
    private val nutritionBasicUseCases: NutritionBasicUseCases
) {suspend fun generateMealPlanDays(): Boolean {
    return initializeMealData()?.let {
        val (allRecipes, allBreakfasts, allSnacks, allMeals, settings) = it

        mealPlanRepository.deleteAllMealPlanDays()

        var nonUsedMeals = allMeals

        for (i in 1..settings.planningHorizonInDays) {
            // Gewichte je Mealtype holen (aktuellster Stand)
            val mealWeights = recipeRepository
                .observeWeightsByMealTypeAsMap(Mealtype.MEAL)
                .first()
            val breakfastWeights = recipeRepository
                .observeWeightsByMealTypeAsMap(Mealtype.BREAKFAST)
                .first()
            val snackWeights = recipeRepository
                .observeWeightsByMealTypeAsMap(Mealtype.SNACK)
                .first()

            val (meals, updatedNonUsedMeals) = generateRandomMeals(
                allMeals = allMeals,
                mealsPerDay = settings.mealsPerDay,
                nonUsedMeals = nonUsedMeals,
                weights = mealWeights
            )

            val (breakfasts, snacks) = generateRandomBreakfastsAndSnacks(
                allBreakfasts = allBreakfasts,
                allSnacks = allSnacks,
                breakfastsPerDay = settings.breakfastsPerDay,
                snacksPerDay = settings.snacksPerDay,
                breakfastWeights = breakfastWeights,
                snackWeights = snackWeights
            )

            val (unscaledMeals, unscaledBreakfasts, unscaledSnacks) =
                resolveDuplicates(meals, breakfasts, snacks)

            val mealPlanDayId =
                createMealPlanDay(unscaledMeals, unscaledBreakfasts, unscaledSnacks)

            mealPlanRepository.getMealPlanDay(mealPlanDayId)?.let { mealPlanDay ->
                adjustMealsToCaloricGoal(
                    mealPlanDay,
                    unscaledMeals,
                    unscaledBreakfasts,
                    unscaledSnacks,
                    settings.calorieRequirement
                )
                adjustQuantitiesForDuplicates(mealPlanDay)
            }

            nonUsedMeals = updatedNonUsedMeals
        }

        true
    } ?: false
}

    private suspend fun adjustMealsToCaloricGoal(
        mealPlanDay: MealPlanDay,
        unscaledMeals: List<Recipe>,
        unscaledBreakfasts: List<Recipe>,
        unscaledSnacks: List<Recipe>,
        calorieGoal: Int
    ): Boolean {
        val recipeCaloriesMap =
            calculateRecipeCaloriesMap(unscaledMeals, unscaledBreakfasts, unscaledSnacks)

        return adjustRecipeQuantitiesRecursively(mealPlanDay, recipeCaloriesMap, calorieGoal, 0)
    }

    private suspend fun adjustRecipeQuantitiesRecursively(
        mealPlanDay: MealPlanDay,
        recipeCaloriesMap: Map<Long, Float>,
        calorieGoal: Int,
        count: Int
    ): Boolean {
        val totalCalories = calculateTotalCalories(mealPlanDay, recipeCaloriesMap)
        val calorieGap = calorieGoal - totalCalories

        if (abs(calorieGap) < CALORIE_GENERATE_TOLERANCE) {
            return true
        }

        val calorieGapPositiv = calorieGap > 0
        val newCount = count + 1
        if (newCount > 50) {
            return false
        }

        if (calorieGapPositiv) {
            increaseSmallestRecipeQuantityByQuarter(mealPlanDay, recipeCaloriesMap)
            return adjustRecipeQuantitiesRecursively(
                mealPlanDay,
                recipeCaloriesMap,
                calorieGoal,
                newCount
            )
        } else {
            reduceLargestRecipeQuantityByQuarter(mealPlanDay, recipeCaloriesMap)
            return adjustRecipeQuantitiesRecursively(
                mealPlanDay,
                recipeCaloriesMap,
                calorieGoal,
                newCount
            )
        }
    }

    private suspend fun reduceLargestRecipeQuantityByQuarter(
        mealPlanDay: MealPlanDay,
        recipeCaloriesMap: Map<Long, Float>
    ): Boolean {
        val recipeWithMostCalories = getRecipeWithMostCalories(mealPlanDay, recipeCaloriesMap)

        recipeWithMostCalories?.let {
            val newQuantity = it.quantity - 0.25f
            return if (newQuantity > 0f) {
                mealPlanRepository.updateRecipeQuantity(mealPlanDay.id, it.recipeId, newQuantity)
                true
            } else {
                mealPlanRepository.deleteRecipeFromDay(mealPlanDay.id, it.recipeId)
                true
            }
        }
        return false
    }

    private suspend fun increaseSmallestRecipeQuantityByQuarter(
        mealPlanDay: MealPlanDay,
        recipeCaloriesMap: Map<Long, Float>
    ): Boolean {
        val recipeWithLeastCalories = getRecipeWithLeastCalories(mealPlanDay, recipeCaloriesMap)
        recipeWithLeastCalories?.let {
            val newQuantity = it.quantity + 0.25f
            mealPlanRepository.updateRecipeQuantity(mealPlanDay.id, it.recipeId, newQuantity)
            return true
        }
        return false
    }

    private suspend fun getRecipeWithMostCalories(
        mealPlanDay: MealPlanDay,
        recipeCaloriesMap: Map<Long, Float>
    ): RecipeQuantity? {
        val recipesWithQuantity = mealPlanRepository.getRecipeQuantityCombined(mealPlanDay.id)
        return recipesWithQuantity.maxByOrNull {
            val recipeCalories = recipeCaloriesMap[it.recipeId] ?: 0f
            recipeCalories * it.quantity
        }
    }

    private suspend fun getRecipeWithLeastCalories(
        mealPlanDay: MealPlanDay,
        recipeCaloriesMap: Map<Long, Float>
    ): RecipeQuantity? {
        val recipesWithQuantity = mealPlanRepository.getRecipeQuantityCombined(mealPlanDay.id)
        return recipesWithQuantity.minByOrNull {
            val recipeCalories = recipeCaloriesMap[it.recipeId] ?: 0f
            recipeCalories * it.quantity
        }
    }

    private fun calculateRecipeCaloriesMap(
        meals: List<Recipe>,
        breakfasts: List<Recipe>,
        snacks: List<Recipe>
    ): Map<Long, Float> {
        val allRecipes = meals + breakfasts + snacks
        val recipeCalories1 = allRecipes.map { recipe ->
            RecipeCalories(recipe.id, recipe.title, recipe.caloriesPerServing)
        }
        return recipeCalories1
            .filter { rc -> allRecipes.any { it.id == rc.recipeId } }
            .associate { rc -> rc.recipeId to rc.totalCalories }
    }

    private suspend fun calculateTotalCalories(
        mealPlanDay: MealPlanDay,
        recipeCaloriesMap: Map<Long, Float>
    ): Float {
        val recipesWithQuantity = mealPlanRepository.getRecipeQuantityCombined(mealPlanDay.id)
        return recipesWithQuantity.fold(0f) { acc, recipeInMealPlan ->
            val recipeCalories = recipeCaloriesMap[recipeInMealPlan.recipeId] ?: 0f
            acc + (recipeCalories * recipeInMealPlan.quantity)
        }
    }

    private suspend fun initializeMealData(): MealData? {
        val allRecipes = mealPlanRepository.getAllRecipes()
        return if (checkIfAtLeastOneRecipeIsAvailable(allRecipes)) {
            MealData(
                allRecipes,
                mealPlanRepository.getAllBreakfasts(),
                mealPlanRepository.getAllSnacks(),
                mealPlanRepository.getAllMeals(),
                settingsRepository.getSettings() ?: defaultSettings()
            )
        } else null
    }

    // Neu: gewichtete Auswahl für Meals
    private fun generateRandomMeals(
        allMeals: List<Recipe>,
        mealsPerDay: Int,
        nonUsedMeals: List<Recipe>
    ): Pair<List<Recipe>, List<Recipe>> {
        // Fallback: gleichverteilte Auswahl, falls keine Gewichte übergeben würden (sollten wir hier nicht nutzen)
        val selected = nonUsedMeals.shuffled().take(mealsPerDay).distinct()
        val updatedMeals = if (nonUsedMeals.size >= 2 * mealsPerDay) {
            nonUsedMeals - selected.toSet()
        } else {
            allMeals
        }
        return Pair(selected, updatedMeals)
    }

    // Überladene Variante mit Gewichten (nutzen wir)
    private fun generateRandomMeals(
        allMeals: List<Recipe>,
        mealsPerDay: Int,
        nonUsedMeals: List<Recipe>,
        weights: Map<Long, Float>,
        defaultWeight: Float = 0.5f
    ): Pair<List<Recipe>, List<Recipe>> {
        val selectedMeals = selectWeightedWithoutReplacement(
            candidates = nonUsedMeals,
            count = mealsPerDay,
            weights = weights,
            defaultWeight = defaultWeight
        )

        val updatedMeals = if (nonUsedMeals.size >= 2 * mealsPerDay) {
            nonUsedMeals - selectedMeals.toSet()
        } else {
            allMeals
        }

        return Pair(selectedMeals, updatedMeals)
    }

    private fun generateRandomBreakfastsAndSnacks(
        allBreakfasts: List<Recipe>,
        allSnacks: List<Recipe>,
        breakfastsPerDay: Int,
        snacksPerDay: Int
    ): Pair<List<Recipe>, List<Recipe>> {
        // Fallback (gleichverteilt), sollte mit neuen Gewichten nicht aufgerufen werden
        return Pair(
            allBreakfasts.shuffled().take(breakfastsPerDay),
            allSnacks.shuffled().take(snacksPerDay)
        )
    }

    // Überladene Variante mit Gewichten (nutzen wir)
    private fun generateRandomBreakfastsAndSnacks(
        allBreakfasts: List<Recipe>,
        allSnacks: List<Recipe>,
        breakfastsPerDay: Int,
        snacksPerDay: Int,
        breakfastWeights: Map<Long, Float>,
        snackWeights: Map<Long, Float>,
        defaultWeight: Float = 0.5f
    ): Pair<List<Recipe>, List<Recipe>> {
        val selectedBreakfasts = selectWeightedWithoutReplacement(
            candidates = allBreakfasts,
            count = breakfastsPerDay,
            weights = breakfastWeights,
            defaultWeight = defaultWeight
        )
        val selectedSnacks = selectWeightedWithoutReplacement(
            candidates = allSnacks,
            count = snacksPerDay,
            weights = snackWeights,
            defaultWeight = defaultWeight
        )
        return Pair(selectedBreakfasts, selectedSnacks)
    }

    // Gewichtete Ziehung ohne Zurücklegen (O(k*n)), robust und simpel
    private fun selectWeightedWithoutReplacement(
        candidates: List<Recipe>,
        count: Int,
        weights: Map<Long, Float>,
        defaultWeight: Float = 0.5f
    ): List<Recipe> {
        if (candidates.isEmpty() || count <= 0) return emptyList()
        val pool = candidates.toMutableList()
        val result = mutableListOf<Recipe>()

        repeat(minOf(count, pool.size)) {
            // Summe der Gewichte (negative werden als 0 behandelt)
            val total = pool.sumOf { r ->
                val w = weights[r.id] ?: defaultWeight
                maxOf(w.toDouble(), 0.0)
            }

            if (total <= 0.0) {
                // Fallback auf gleichverteilte Auswahl der restlichen
                result += pool.shuffled().take(count - result.size)
                return result
            }

            var r = Random.nextDouble(total)
            var idx = 0
            while (idx < pool.size) {
                val w = maxOf((weights[pool[idx].id] ?: defaultWeight).toDouble(), 0.0)
                r -= w
                if (r <= 0.0) {
                    result += pool.removeAt(idx)
                    break
                }
                idx++
            }
        }

        return result
    }

    private fun resolveDuplicates(
        meals: List<Recipe>,
        breakfasts: List<Recipe>,
        snacks: List<Recipe>,
        maxRetries: Int = 100
    ): Triple<List<Recipe>, List<Recipe>, List<Recipe>> {
        var loopCount = 0
        var updatedMeals = meals
        var updatedBreakfasts = breakfasts
        var updatedSnacks = snacks

        while (loopCount < maxRetries && hasDuplicates(updatedMeals, updatedBreakfasts, updatedSnacks)) {
            updatedMeals = updatedMeals.shuffled()
            updatedBreakfasts = updatedBreakfasts.shuffled()
            updatedSnacks = updatedSnacks.shuffled()
            loopCount++
        }
        return Triple(updatedMeals, updatedBreakfasts, updatedSnacks)
    }

    private fun hasDuplicates(
        meals: List<Recipe>,
        breakfasts: List<Recipe>,
        snacks: List<Recipe>
    ): Boolean {
        val commonMB = meals.intersect(breakfasts)
        val commonMS = meals.intersect(snacks)
        val commonBS = breakfasts.intersect(snacks)
        return commonMB.isNotEmpty() || commonMS.isNotEmpty() || commonBS.isNotEmpty()
    }

    private suspend fun createMealPlanDay(
        meals: List<Recipe>,
        breakfasts: List<Recipe>,
        snacks: List<Recipe>
    ): Long {
        val mealPlanDayId = mealPlanRepository.insertMealPlanDay(MealPlanDay(0L, null))

        meals.forEach { recipe ->
            mealPlanRepository.addRecipeToDay(mealPlanDayId, recipe.id, INIT_MEALPLANDAY_QUANTITY)
        }
        breakfasts.forEach { recipe ->
            mealPlanRepository.addRecipeToDay(mealPlanDayId, recipe.id, INIT_MEALPLANDAY_QUANTITY)
        }
        snacks.forEach { recipe ->
            mealPlanRepository.addRecipeToDay(mealPlanDayId, recipe.id, INIT_MEALPLANDAY_QUANTITY)
        }

        return mealPlanDayId
    }

    fun getAllMealPlanDays(): Flow<List<MealPlanDay>> = mealPlanRepository.getAllMealPlanDays()

    private fun checkIfAtLeastOneRecipeIsAvailable(allRecipes: List<Recipe>): Boolean {
        return allRecipes.isNotEmpty()
    }

    private suspend fun adjustQuantitiesForDuplicates(mealPlanDay: MealPlanDay) {
        val recipesWithQuantity = mealPlanRepository.getRecipeQuantityCombined(mealPlanDay.id)
        val grouped = recipesWithQuantity.groupBy { it.recipeId }

        for ((recipeId, quantities) in grouped) {
            if (quantities.size > 1) {
                val totalQuantity =
                    quantities.sumOf { it.quantity.toDouble() }.toFloat()

                val first = quantities.first()

                quantities.drop(1).forEach { duplicate ->
                    mealPlanRepository.deleteRecipeFromDay(mealPlanDay.id, duplicate.recipeId)
                }

                mealPlanRepository.updateRecipeQuantity(mealPlanDay.id, first.recipeId, totalQuantity)
            }
        }
    }
}