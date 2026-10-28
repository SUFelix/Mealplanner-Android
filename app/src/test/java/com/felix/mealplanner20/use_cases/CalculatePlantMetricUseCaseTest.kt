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

    private fun ingredient(id: Long, name: String, dgeType: dgeGroup, plantGroupKey: String? = null) = Ingredient(
        id = id,
        germanName = name,
        englishName = name,
        dgeType = dgeType,
        plantGroupKey = plantGroupKey
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
        assertThat(metric.count).isEqualTo(2f)
        assertThat(metric.distinctPlants.map { it.id }).containsExactly(1L, 2L)
    }

    @Test
    fun `excludes GRAIN but includes WHOLEGRAIN`() = runBlocking<Unit> {
        val ingredients = listOf(
            ingredient(1, "White bread", dgeGroup.GRAIN),
            ingredient(2, "Whole wheat bread", dgeGroup.WHOLEGRAIN)
        )

        val metric = calculatePlantMetricUseCase(flowOf(ingredients)).first()

        assertThat(metric.count).isEqualTo(1f)
        assertThat(metric.distinctPlants.single().id).isEqualTo(2L)
    }

    @Test
    fun `excludes OTHERVEGAN`() = runBlocking<Unit> {
        val ingredients = listOf(
            ingredient(1, "Vegan processed snack", dgeGroup.OTHERVEGAN)
        )

        val metric = calculatePlantMetricUseCase(flowOf(ingredients)).first()

        assertThat(metric.count).isEqualTo(0f)
    }

    @Test
    fun `dedupes duplicate ingredient ids`() = runBlocking<Unit> {
        val ingredients = listOf(
            ingredient(1, "Apple", dgeGroup.FRUIT),
            ingredient(1, "Apple", dgeGroup.FRUIT)
        )

        val metric = calculatePlantMetricUseCase(flowOf(ingredients)).first()

        assertThat(metric.count).isEqualTo(1f)
    }

    @Test
    fun `dedupes ingredients sharing a plantGroupKey`() = runBlocking<Unit> {
        val ingredients = listOf(
            ingredient(1, "Tomaten", dgeGroup.VEGETABLE, plantGroupKey = "tomato"),
            ingredient(2, "Dosentomaten", dgeGroup.VEGETABLE, plantGroupKey = "tomato"),
            ingredient(3, "Tomatenmark", dgeGroup.VEGETABLE, plantGroupKey = "tomato"),
            ingredient(4, "Gurke", dgeGroup.VEGETABLE)
        )

        val metric = calculatePlantMetricUseCase(flowOf(ingredients)).first()

        assertThat(metric.count).isEqualTo(2f)
        assertThat(metric.distinctPlants.map { it.id }).containsExactly(1L, 4L)
        assertThat(metric.groupedIngredients[1L]?.map { it.id }).containsExactly(1L, 2L, 3L)
        assertThat(metric.groupedIngredients[4L]?.map { it.id }).containsExactly(4L)
    }

    @Test
    fun `untagged ingredients of the same category still count separately`() = runBlocking<Unit> {
        val ingredients = listOf(
            ingredient(1, "Apple", dgeGroup.FRUIT),
            ingredient(2, "Banana", dgeGroup.FRUIT)
        )

        val metric = calculatePlantMetricUseCase(flowOf(ingredients)).first()

        assertThat(metric.count).isEqualTo(2f)
    }

    @Test
    fun `spices only count a quarter of a plant`() = runBlocking<Unit> {
        val ingredients = listOf(
            ingredient(1, "Basilikum", dgeGroup.SPICE),
            ingredient(2, "Oregano", dgeGroup.SPICE),
            ingredient(3, "Apfel", dgeGroup.FRUIT)
        )

        val metric = calculatePlantMetricUseCase(flowOf(ingredients)).first()

        // 2 distinct spices * 0.25 + 1 full plant = 1.5
        assertThat(metric.count).isEqualTo(1.5f)
        assertThat(metric.distinctPlants.map { it.id }).containsExactly(1L, 2L, 3L)
    }

    @Test
    fun `spices sharing a plantGroupKey still count only one 0_25 share`() = runBlocking<Unit> {
        val ingredients = listOf(
            ingredient(1, "Getrocknetes Basilikum", dgeGroup.SPICE, plantGroupKey = "basil"),
            ingredient(2, "Frisches Basilikum", dgeGroup.SPICE, plantGroupKey = "basil")
        )

        val metric = calculatePlantMetricUseCase(flowOf(ingredients)).first()

        assertThat(metric.count).isEqualTo(0.25f)
    }

    @Test
    fun `ingredients tagged zero don't count towards the metric`() = runBlocking<Unit> {
        val ingredients = listOf(
            ingredient(1, "Apfelsaft", dgeGroup.FRUIT, plantGroupKey = "zero"),
            ingredient(2, "Apfel", dgeGroup.FRUIT)
        )

        val metric = calculatePlantMetricUseCase(flowOf(ingredients)).first()

        assertThat(metric.count).isEqualTo(1f)
        assertThat(metric.distinctPlants.map { it.id }).containsExactly(2L)
    }

    @Test
    fun `zero-tagged ingredients are not merged into a single group`() = runBlocking<Unit> {
        val ingredients = listOf(
            ingredient(1, "Apfelsaft", dgeGroup.FRUIT, plantGroupKey = "zero"),
            ingredient(2, "Orangensaft", dgeGroup.FRUIT, plantGroupKey = "zero")
        )

        val metric = calculatePlantMetricUseCase(flowOf(ingredients)).first()

        assertThat(metric.count).isEqualTo(0f)
        assertThat(metric.distinctPlants).isEmpty()
    }

    @Test
    fun `zero tag is matched case-insensitively`() = runBlocking<Unit> {
        val ingredients = listOf(
            ingredient(1, "Apfelsaft", dgeGroup.FRUIT, plantGroupKey = "Zero")
        )

        val metric = calculatePlantMetricUseCase(flowOf(ingredients)).first()

        assertThat(metric.count).isEqualTo(0f)
    }
}
