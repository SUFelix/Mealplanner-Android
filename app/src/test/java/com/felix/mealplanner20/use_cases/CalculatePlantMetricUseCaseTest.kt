package com.felix.mealplanner20.use_cases

import com.felix.mealplanner20.Meals.Data.Ingredient
import com.felix.mealplanner20.Meals.Data.helpers.dgeGroup
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class CalculatePlantMetricUseCaseTest {

    private lateinit var calculatePlantMetricUseCase: CalculatePlantMetricUseCase

    @Before
    fun setUp() {
        calculatePlantMetricUseCase = CalculatePlantMetricUseCase()
    }

    private fun ingredient(id: Long, name: String, dgeType: dgeGroup) = Ingredient(
        id = id,
        germanName = name,
        englishName = name,
        dgeType = dgeType
    )

    @Test
    fun `filters ingredients down to plant dgeGroups only`() = runBlocking<Unit> {
        val ingredients = listOf(
            ingredient(1, "Apple", dgeGroup.FRUIT),
            ingredient(2, "Banana", dgeGroup.FRUIT),
            ingredient(3, "Milk", dgeGroup.MILK),
            ingredient(4, "Chicken", dgeGroup.MEAT)
        )

        val result = calculatePlantMetricUseCase(flowOf(ingredients))

        val metric = result.first()
        assertThat(metric.count).isEqualTo(2)
        assertThat(metric.distinctPlants.map { it.id }).containsExactly(1L, 2L)
    }

    @Test
    fun `excludes GRAIN but includes WHOLEGRAIN`() = runBlocking<Unit> {
        val ingredients = listOf(
            ingredient(1, "White bread", dgeGroup.GRAIN),
            ingredient(2, "Whole wheat bread", dgeGroup.WHOLEGRAIN)
        )

        val metric = calculatePlantMetricUseCase(flowOf(ingredients)).first()

        assertThat(metric.count).isEqualTo(1)
        assertThat(metric.distinctPlants.single().id).isEqualTo(2L)
    }

    @Test
    fun `excludes OTHERVEGAN`() = runBlocking<Unit> {
        val ingredients = listOf(
            ingredient(1, "Vegan processed snack", dgeGroup.OTHERVEGAN)
        )

        val metric = calculatePlantMetricUseCase(flowOf(ingredients)).first()

        assertThat(metric.count).isEqualTo(0)
    }

    @Test
    fun `dedupes duplicate ingredient ids`() = runBlocking<Unit> {
        val ingredients = listOf(
            ingredient(1, "Apple", dgeGroup.FRUIT),
            ingredient(1, "Apple", dgeGroup.FRUIT)
        )

        val metric = calculatePlantMetricUseCase(flowOf(ingredients)).first()

        assertThat(metric.count).isEqualTo(1)
    }
}
