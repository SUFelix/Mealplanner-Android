package com.felix.mealplanner20

import android.content.Context
import androidx.annotation.DrawableRes

const val ADD_SCREEN_ARGUMENT_KEY = "id"
const val ADD_INGREDIENT_TO_RECIPE_SCREEN_ARGUMENT_KEY = "recipeId"
const val ADD_EDIT_RECIPE_KEY = "recipeId"
const val ADD_SCREEN_CORE_ROUTE = "add_screen/"
const val REPLACE_RECIPE_ARGUMENT_KEY = "recipeId"
const val SHOW_RECIPE_ARGUMENT_KEY = "recipeId"
const val MEALPLANDAYID_ARGUMENT_KEY = "mealPlanDayId"

const val NUTRITION_COCKPIT_TEST_TAG = "nutritioncockpit_screen"
const val MEALPLAN_TEST_TAG = "mealplan_screen"
const val SHOPPINGLIST_TEST_TAG = "shoppinglist_screen"
const val PROFILE_TEST_TAG = "profile_screen"

sealed class Screen(var title:String,val route:String){
    class AddUpdateIngredientScreen(context: Context) : Screen(
        title = context.getString(R.string.add_ingredient),
        "$ADD_SCREEN_CORE_ROUTE{$ADD_SCREEN_ARGUMENT_KEY}"
    ){
        companion object
        fun passId(id:Long):String{
            return "$ADD_SCREEN_CORE_ROUTE$id"
        }
    }
    class AddUpdateRecipeScreen(context: Context) : Screen(
        title = context.getString(R.string.add_update_recipe),
        route = "add_recipe_screen/{$ADD_EDIT_RECIPE_KEY}"){
        companion object
        fun passId(id:Long):String{
            return "add_recipe_screen/$id"
        }
    }
    class AddIngredientToRecipeScreen(context: Context):Screen(
        title = context.getString(R.string.add_ingredient),
        route = "add_ingredient_to_recipe/{$ADD_INGREDIENT_TO_RECIPE_SCREEN_ARGUMENT_KEY}"){
        companion object
        fun passId(id:Long):String{
            return "add_ingredient_to_recipe/$id"
        }
    }
    class ConfigProbabilitiesScreen(context: Context):Screen(context.getString(R.string.config_probabilities),"configurate_probabilities_screen"){
        companion object
    }
    class ReplaceMealPlanRecipeScreen(context: Context) : Screen(
        title = context.getString(R.string.replacerecipe),
        route = "replace_recipe/{$REPLACE_RECIPE_ARGUMENT_KEY}/{$MEALPLANDAYID_ARGUMENT_KEY}"
    ) {
        companion object
        fun passIds(recipeId: Long, mealPlanDayId: Long): String {
            return "replace_recipe/$recipeId/$mealPlanDayId"
        }
    }
    class AddMealPlanRecipeScreen(context: Context) : Screen(
        title = context.getString(R.string.add_meal),
        route = "add_recipe/{$MEALPLANDAYID_ARGUMENT_KEY}"
    ) {
        companion object
        fun passId(mealPlanDayId: Long): String {
            return "add_recipe/$mealPlanDayId"
        }
    }
    class RecipeViewOnlyScreen(context: Context):Screen(
        title = context.getString(R.string.recipe),
        route = "recipe_view_only_screen/{$SHOW_RECIPE_ARGUMENT_KEY}/{$MEALPLANDAYID_ARGUMENT_KEY}"
    ){
        companion object
        fun passIds(recipeId:Long, mealPlanDayId: Long):String{
            return "recipe_view_only_screen/$recipeId/$mealPlanDayId"
        }
    }
    class DiscoverRecipesSingleViewScreen(context: Context,title: String? = null):Screen(
        title = title ?: context.getString(R.string.recipe),
        route = "discover_recipes_single_view_screen/{$SHOW_RECIPE_ARGUMENT_KEY}"
    ){
        companion object
        fun passId(recipeId:Long):String{
            return "discover_recipes_single_view_screen/$recipeId"
        }
    }
    class RecipeViewOnlyScreenWithOutMealplan(context: Context):Screen(
        title = context.getString(R.string.recipe),
        route = "recipe_view_only_screen/{$SHOW_RECIPE_ARGUMENT_KEY}"
    ){
        companion object
        fun passId(recipeId:Long):String{
            return "recipe_view_only_screen/$recipeId"
        }
    }
    class IngredientHomeScreen(context: Context) : Screen(
        title = context.getString(R.string.ingredient_home),
        route ="ingredienthome_screen"
    ){
        companion object
    }
    class Login(context: Context):Screen("Login","login_screen"){
        companion object
    }
    class SignUp(context: Context):Screen("SignUp","signup_screen"){
        companion object
    }
    class RequestReset(context: Context):Screen("RequestReset","request_reset_screen"){
        companion object
    }
    class SetNewPassword(context: Context):Screen("SetNewPassword","set_new_password_screen"){
        companion object
    }





    sealed class TopAppBarScreen(title: String,route: String):Screen(title, route){
        abstract fun create():TopAppBarScreen
        open fun shortTitle(context: Context): String {
            return this.title
        }

        class DiscoverRecipesScreen(context: Context) : TopAppBarScreen(
            title = "discover",
            route = "discoverRecipes_screen"
        ){
            override fun create() = this
        }
        class MyOwnRecipesScreen(context: Context) : TopAppBarScreen(
            title = "My Own",
            route = "MyOwnRecipes_screen"
        ){
            override fun create() = this
        }

        class SendFeedback(context: Context): TopAppBarScreen("SendFeedback","send_feedback_screen"){
            override fun create() = this
        }
    }
    sealed class BottomScreen(title:String,route:String, @DrawableRes val icon:Int):Screen(title,route){
        abstract fun create(): BottomScreen
        open fun shortTitle(context: Context): String {
            return this.title
        }
        class NutritionCockpitScreen(context: Context) : BottomScreen(
            title = context.getString(R.string.nutri_stats),
            route = "nutritioncockpit_screen",
            icon = R.drawable.nutristats_menu_item
        ){
            override fun create() = this
        }
        class MealPlanScreen(context: Context):BottomScreen(
            title = context.getString(R.string.mealplan),
            route = "mealplan_screen",
            icon = R.drawable.mealplan_menu_item){
            override fun create() = this
        }
        class CatalogScreen(context: Context):BottomScreen(
            title  = context.getString(R.string.recipes),
            route = "recipecatalog_screen",
            icon = R.drawable.frame
        ){
            override fun create() = this
        }


        class ShoppingList(context: Context):BottomScreen(
            title = context.getString(R.string.shopping_list),
            route = "shoppinglist_screen",
            icon  = R.drawable.shopping_list_item
        ){
            override fun create() = this
            override fun shortTitle(context: Context):String{
                return context.getString(R.string.list2shop)
            }
        }
        class Profile(context: Context):BottomScreen("Profile","profile_screen",R.drawable.profile_menu_item){
            override fun create() = this
            override fun shortTitle(context: Context):String{
                return "Profile"
            }
        }

        fun updateTitle(newTitle: String): BottomScreen {
            this.title = newTitle
            return this
        }

    }
}


