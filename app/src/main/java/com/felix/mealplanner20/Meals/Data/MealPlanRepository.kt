package com.felix.mealplanner20.Meals.Data

import android.util.Log
import com.felix.mealplanner20.Meals.Data.helpers.RecipeQuantity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class MealPlanRepository(
    private val mealPlanDayDao: MealPlanDayDao,
    private val recipeDao: RecipeDao,
    private val mealPlanDayRecipeDao: MealPlanDayRecipeDao
) {

    /** MealPlanDay CRUD **/
    suspend fun insertMealPlanDay(mealPlanDay: MealPlanDay): Long {
        return mealPlanDayDao.insert(mealPlanDay)
    }

    suspend fun insertMealPlanDays(mealPlanDays: List<MealPlanDay>): List<Long> {
        return mealPlanDayDao.insertMealPlanDays(mealPlanDays)
    }

    suspend fun updateMealPlanDay(mealPlanDay: MealPlanDay) {
        mealPlanDayDao.update(mealPlanDay)
    }

    suspend fun getMealPlanDay(id: Long): MealPlanDay? {
        return mealPlanDayDao.getDay(id)
    }

    suspend fun deleteAllMealPlanDays() {
        mealPlanDayDao.deleteAll()
    }

    fun getAllMealPlanDays(): Flow<List<MealPlanDay>> = mealPlanDayDao.getAllMealPlanDays()



    /** Recipes CRUD **/
    suspend fun getAllRecipes(): List<Recipe> = recipeDao.getAllRecipes().first()
    suspend fun getAllBreakfasts(): List<Recipe> = recipeDao.getAllBreakfasts().first()
    suspend fun getAllMeals(): List<Recipe> = recipeDao.getAllMeals().first()
    suspend fun getAllSnacks(): List<Recipe> = recipeDao.getAllSnacks().first()

    /** Rezepte pro Tag (Join-Tabelle) **/

    suspend fun addRecipeToDay(mealPlanDayId: Long, recipeId: Long, quantity: Float = 1f) {
        val entry = MealPlanDayRecipeEntity(mealPlanDayId, recipeId, quantity)
        mealPlanDayRecipeDao.insert(entry)
    }

    suspend fun deleteRecipeFromDay(mealPlanDayId: Long, recipeId: Long) {
        val entry = mealPlanDayRecipeDao.getRecipeEntry(mealPlanDayId, recipeId) ?: return
        mealPlanDayRecipeDao.delete(entry)
    }

    suspend fun updateRecipeQuantity(mealPlanDayId: Long, recipeId: Long, quantity: Float) {
        val entry = mealPlanDayRecipeDao.getRecipeEntry(mealPlanDayId, recipeId) ?: return
        mealPlanDayRecipeDao.update(entry.copy(quantity = quantity))
    }

    suspend fun replaceRecipe(mealPlanDayId: Long, oldRecipeId: Long, newRecipeId: Long) {
        //TODO check auf schon vorhandenenes neues recipe im gleichen TAG?
        mealPlanDayRecipeDao.replaceRecipe(mealPlanDayId,oldRecipeId,newRecipeId)
    }

    suspend fun getRecipesForDay(mealPlanDayId: Long): List<MealPlanDayRecipeEntity> {
        return mealPlanDayRecipeDao.getRecipesForDay(mealPlanDayId)
    }

     fun getRecipeFlowForDay(mealPlanDayId: Long): Flow<List<MealPlanDayRecipeEntity>> {
        return mealPlanDayRecipeDao.getRecipesFlowForDay(mealPlanDayId)
    }

    suspend fun getRecipeQuantity(mealPlanDayId: Long, recipeId: Long): Float {
        return mealPlanDayRecipeDao.getRecipeEntry(mealPlanDayId, recipeId)?.quantity ?: 0f
    }
    suspend fun getRecipeQuantityCombined(mealPlanDayId: Long): List<RecipeQuantity> {
        val recipes = getRecipesForDay(mealPlanDayId)
        val list = recipes.map{it->
           RecipeQuantity(it.recipeId,getRecipeQuantity(mealPlanDayId,it.recipeId))
        }
        return list
    }

    suspend fun getRecipeIdsAndQuantities(mealPlanDayId: Long): Map<Long, Float> {
        return mealPlanDayRecipeDao.getRecipesForDay(mealPlanDayId)
            .associate { it.recipeId to it.quantity }
    }

    // 1) rohe Entities als Flow weiterreichen
    fun getRecipeEntitiesFlowForDay(mealPlanDayId: Long): Flow<List<MealPlanDayRecipeEntity>> =
        mealPlanDayRecipeDao.getRecipesFlowForDay(mealPlanDayId)


    fun getRecipeIdsFlowForDay(mealPlanDayId: Long): Flow<List<Long>> {
        return mealPlanDayRecipeDao.getRecipeIdsFlowForDay(mealPlanDayId)
    }

    // 2) Flow<List<RecipeQuantity>>
    fun getRecipeQuantitiesFlowForDay(mealPlanDayId: Long): Flow<List<RecipeQuantity>> =
        getRecipeEntitiesFlowForDay(mealPlanDayId)
            .map { entities -> entities.map { RecipeQuantity(it.recipeId, it.quantity) } }

    // 4) Flow für einzelne Rezept-Menge (optional, praktisch)
    fun getRecipeQuantityFlow(mealPlanDayId: Long, recipeId: Long): Flow<Float> =
        getRecipeEntitiesFlowForDay(mealPlanDayId)
            .map { entities -> entities.find { it.recipeId == recipeId }?.quantity ?: 0f }
}