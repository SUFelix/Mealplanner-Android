package com.felix.mealplanner20.use_cases

import com.felix.mealplanner20.Meals.Data.RecipeRepository
import com.felix.mealplanner20.Meals.Data.helpers.DayDetailData
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class CalculateNutritionQualityUseCaseTest {

    private lateinit var calculateNutritionQualityUseCase: CalculateNutritionQualityUseCase
    private lateinit var useCases: NutritionBasicUseCases

    private lateinit var getRecipeCaloriesUseCase: GetRecipeCaloriesUseCase
    private lateinit var getMacroNutrientRecommendationsUseCase: GetMacroNutrientRecomendationsUseCase
    private lateinit var getDgeRecommendationDataUseCase: GetDgeRecommendationDataUseCase
    private lateinit var calculateCaloriesUseCase: CalculateCaloriesUseCase

    @Mock
    lateinit var recipeRepository: RecipeRepository

    @Before
    fun setUp() {
        getRecipeCaloriesUseCase = mock(GetRecipeCaloriesUseCase::class.java)
        getMacroNutrientRecommendationsUseCase = mock(GetMacroNutrientRecomendationsUseCase::class.java)
        getDgeRecommendationDataUseCase = mock(GetDgeRecommendationDataUseCase::class.java)
        calculateCaloriesUseCase = mock(CalculateCaloriesUseCase::class.java)

        useCases = NutritionBasicUseCases(
            getRecipeCaloriesUseCase,
            getMacroNutrientRecommendationsUseCase,
            getDgeRecommendationDataUseCase,
            calculateCaloriesUseCase
        )

        calculateNutritionQualityUseCase = CalculateNutritionQualityUseCase(useCases)

        // Mock recipe repository method
       // whenever(recipeRepository.getRecipeCalories()).thenReturn(flowOf(listOf()))
    }


    @Test
    fun calculateOverallNutrientCompliance() = runBlocking{
        val day1DetailData = DayDetailData(
            dayName = "Day 1",
            nutrients = mapOf(PROTEIN to 50f, FAT to 70f, CARBS to 300f),
            recommendations = mapOf(PROTEIN to 75f, FAT to 60f, CARBS to 250f),
            compliancePercentage = 0f,
            dgeData = listOf(),
            dgeMapping = listOf()
        )
        val day2DetailData = DayDetailData(
            dayName = "Day 1",
            nutrients = mapOf(PROTEIN to 0f, FAT to 0f, CARBS to 0f),
            recommendations = mapOf(PROTEIN to 75f, FAT to 60f, CARBS to 250f),
            compliancePercentage = 0f,
            dgeData = listOf(),
            dgeMapping = listOf()
        )
        val dayDetailData = listOf(day1DetailData,day2DetailData)

        val result = calculateNutritionQualityUseCase.calculateOverallNutrientCompliance(getDayDetailDataFlow(dayDetailData))

        val resultIsNonNegative = result.first() >= 0
        val resultIsMax100 = result.first() <= 100

        assertThat(resultIsNonNegative).isTrue()
        assertThat(resultIsMax100).isTrue()
    }
    fun getDayDetailDataFlow(dayDetailData: List<DayDetailData>): Flow<List<DayDetailData>> {
        return flow {
            emit(dayDetailData) // Gibt die Liste als Flow aus
        }
    }
}
