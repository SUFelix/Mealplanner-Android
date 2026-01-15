package com.felix.mealplanner20.Meals.Data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/*@Entity(tableName = "mealplanday_table")
data class MealPlanDay(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo("recipes")
    val recipes: List<RecipeQuantity> = emptyList()
) {
    fun replaceRecipe(replaceRecipeId: Long, replaceWithId: Long): MealPlanDay {
        val updatedRecipes = recipes.map {
            if (it.recipeId == replaceRecipeId) it.copy(recipeId = replaceWithId) else it
        }
        return this.copy(recipes = updatedRecipes)
    }

    fun deleteRecipe(recipeId: Long): MealPlanDay {
        val updatedRecipes = recipes.filter { it.recipeId != recipeId }
        return this.copy(recipes = updatedRecipes)
    }
    fun addRecipe(recipeId: Long): MealPlanDay {
        val updatedRecipes = recipes + RecipeQuantity(recipeId,1f)
        return this.copy(recipes = updatedRecipes)
    }

    fun getRecipeIds(): List<Long> {
        return recipes.map { it.recipeId }
    }
    fun getRecipeIdsAndQuantity(): Map<Long, Float> {
        return recipes.associate { it.recipeId to it.quantity }
    }
    fun updateQuantity(recipeId: Long, newQuantity: Float): MealPlanDay {
        val updatedRecipes = recipes.map {
            if (it.recipeId == recipeId) it.copy(quantity = newQuantity) else it
        }
        return this.copy(recipes = updatedRecipes)
    }

    fun getRecipeQuantity(recipeId: Long): Float {
        return recipes.find { it.recipeId == recipeId }?.quantity ?: 0f
    }

}
*/


@Entity(tableName = "mealplanday_table")
data class MealPlanDay(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo("date")
    val date: Int?
)



