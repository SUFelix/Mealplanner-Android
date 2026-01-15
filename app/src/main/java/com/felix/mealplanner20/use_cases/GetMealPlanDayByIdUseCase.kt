package com.felix.mealplanner20.use_cases

import com.felix.mealplanner20.Meals.Data.MealPlanDay
import com.felix.mealplanner20.Meals.Data.MealPlanRepository

class GetMealPlanDayByIdUseCase(private val repository: MealPlanRepository)
{
    suspend operator fun invoke(id:Long): MealPlanDay?{
       return  repository.getMealPlanDay(id)
    }
}