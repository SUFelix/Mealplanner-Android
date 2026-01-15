package com.felix.mealplanner20.Meals.Data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.felix.mealplanner20.Meals.Data.helpers.Converters
import com.felix.mealplanner20.Shopping.Data.ShoppingListDao
import com.felix.mealplanner20.Shopping.Data.ShoppingListItem


@Database(
    entities = [
        Ingredient::class,
        Recipe::class,
        IngredientWithRecipe::class,
        IngredientAllowedUnit::class,
        Settings::class,
        MealPlanDay::class,
        MealPlanDayRecipeEntity::class,
        ShoppingListItem::class,
        RecipeDescription::class,
        RecipeMealTypeWeight::class
    ],
    version = 38,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun ingredientDao(): IngredientDao
    abstract fun recipeDao(): RecipeDao
    abstract fun settingsDao(): SettingsDao
    abstract fun ingredientRecipeJoinDao(): IngredientRecipeJoinDao
    abstract fun ingredientAllowedUnitDao(): IngredientAllowedUnitDao
    abstract fun mealPlanDayRecipeDao(): MealPlanDayRecipeDao
    abstract fun mealPlanDayDao(): MealPlanDayDao

    abstract fun shoppingListDao(): ShoppingListDao
    abstract fun recipeDescriptionDao(): RecipeDescriptionDao

    abstract fun recipeMealTypeWeightDao():RecipeMealTypeWeightDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
    }
}



