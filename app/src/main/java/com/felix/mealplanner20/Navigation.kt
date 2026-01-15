package com.felix.mealplanner20

import ConfigureMyRecipesProbabilitiesView
import android.app.Activity
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.felix.mealplanner20.Meals.Data.EMPTY_STRING
import com.felix.mealplanner20.Shopping.ShoppingListView
import com.felix.mealplanner20.Shopping.ShoppingListViewModel
import com.felix.mealplanner20.ViewModels.AddEditRecipeViewModel
import com.felix.mealplanner20.ViewModels.DiscoverRecipesViewModel
import com.felix.mealplanner20.ViewModels.FeedbackViewModel
import com.felix.mealplanner20.ViewModels.IngredientViewModel
import com.felix.mealplanner20.ViewModels.MainViewModel
import com.felix.mealplanner20.ViewModels.MealPlanViewModel
import com.felix.mealplanner20.ViewModels.MyOwnRecipesViewModel
import com.felix.mealplanner20.ViewModels.NutritionViewModel
import com.felix.mealplanner20.ViewModels.ProfileViewModel
import com.felix.mealplanner20.ViewModels.RecipeCatalogViewModel
import com.felix.mealplanner20.ViewModels.SettingsViewModel
import com.felix.mealplanner20.ViewModels.SignInViewModel
import com.felix.mealplanner20.Views.AddEditIngredientView
import com.felix.mealplanner20.Views.IngredientHomeView
import com.felix.mealplanner20.Views.Mealplan.AddMealPlanRecipeView
import com.felix.mealplanner20.Views.Mealplan.MealPlan
import com.felix.mealplanner20.Views.Mealplan.ReplaceMealPlanRecipeView
import com.felix.mealplanner20.Views.NutritionDashboard
import com.felix.mealplanner20.Views.ProfileSettingsLogin.LoginView
import com.felix.mealplanner20.Views.ProfileSettingsLogin.ProfileView
import com.felix.mealplanner20.Views.ProfileSettingsLogin.ResetPasswordConfirmView
import com.felix.mealplanner20.Views.ProfileSettingsLogin.ResetPasswordRequestView
import com.felix.mealplanner20.Views.ProfileSettingsLogin.SignUpView
import com.felix.mealplanner20.Views.Recipes.AddEditRecipeView
import com.felix.mealplanner20.Views.Recipes.AddIngredientToRecipeView
import com.felix.mealplanner20.Views.Recipes.DiscoverRecipesView
import com.felix.mealplanner20.Views.Recipes.DiscoverViewSingleRecipe
import com.felix.mealplanner20.Views.Recipes.MyOwnRecipesView
import com.felix.mealplanner20.Views.Recipes.RecipeViewOnlyView
import com.felix.mealplanner20.Views.SendFeedbackView
import kotlinx.coroutines.channels.Channel


const val ADD_EDIT_RECIPE_NAVIGATION = "AddEditRecipe"
const val RECIPE_CATALOG_NAVIGATION = "catalog"
const val MEALPLAN_NAVIGATION = "mealplan"
const val LOGIN_NAVIGATION = "login"
@Composable
fun Navigation(
    mainViewModel : MainViewModel,
    ingredientViewModel: IngredientViewModel,
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    discoverRecipesViewModel : DiscoverRecipesViewModel = hiltViewModel(),
    myOwnRecipesViewModel : MyOwnRecipesViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel(),
    nutritionViewModel: NutritionViewModel = hiltViewModel(),
    mealPlanViewModel: MealPlanViewModel = hiltViewModel(),
    signInViewModel: SignInViewModel = hiltViewModel(),
    navController: NavHostController
){
    val activity: Activity = LocalContext.current as Activity
    val context = LocalContext.current

    val currentScreen = remember { mutableStateOf<Screen>(Screen.BottomScreen.CatalogScreen(context)) }
    val navigationEventChannel = remember { Channel<NavigationEvent>(Channel.BUFFERED) }

    LaunchedEffect(navigationEventChannel) {
        for (event in navigationEventChannel) {
            when (event) {
                is NavigationEvent.NavigateTo -> {
                    navController.navigate(event.screen.route)
                    currentScreen.value = event.screen
                    mainViewModel.setCurrentScreen(event.screen)
                }
            }
        }
    }

    NavHost(
        navController = navController as NavHostController,
        startDestination = RECIPE_CATALOG_NAVIGATION
    ){

        composable(Screen.IngredientHomeScreen(context).route){
            IngredientHomeView(navController,ingredientViewModel,mainViewModel)
            mainViewModel.setCurrentScreen(Screen.IngredientHomeScreen(context))
        }
        composable(
            route = Screen.AddUpdateIngredientScreen(context).route ,
            arguments = listOf(navArgument(ADD_SCREEN_ARGUMENT_KEY){
                type = NavType.LongType
                nullable = false
                defaultValue = 0L})
        )
        {backStackEntry->
            val id = if(backStackEntry.arguments != null) backStackEntry.arguments!!.getLong("id") else 0L
            AddEditIngredientView(id = id, ingredientViewModel =ingredientViewModel , navController = navController,mainViewModel)
            mainViewModel.setCurrentScreen(Screen.AddUpdateIngredientScreen(context))
        }

        navigation(
            startDestination = Screen.TopAppBarScreen.MyOwnRecipesScreen(context).route,
            route=RECIPE_CATALOG_NAVIGATION
        ){
             composable(route  = Screen.BottomScreen.CatalogScreen(context).route){
                 MyOwnRecipesView(
                     navController = navController,
                     myOwnRecipesViewModel = myOwnRecipesViewModel,
                     mainViewModel = mainViewModel
                 )
                 mainViewModel.setCurrentScreen(Screen.TopAppBarScreen.MyOwnRecipesScreen(context))
            }

            composable(route = Screen.TopAppBarScreen.DiscoverRecipesScreen(context).route){backStackEntry->

                DiscoverRecipesView(
                    navController = navController,
                    discoverRecipesViewModel = discoverRecipesViewModel
                )
                mainViewModel.setCurrentScreen(Screen.TopAppBarScreen.DiscoverRecipesScreen(context))
            }
            composable(route = Screen.TopAppBarScreen.MyOwnRecipesScreen(context).route){

                MyOwnRecipesView(
                    navController = navController,
                    myOwnRecipesViewModel = myOwnRecipesViewModel,
                    mainViewModel = mainViewModel
                )
                mainViewModel.setCurrentScreen(Screen.TopAppBarScreen.MyOwnRecipesScreen(context))
            }
            composable(
                route = Screen.DiscoverRecipesSingleViewScreen(context,discoverRecipesViewModel.isLoading.value.toString()).route,
                arguments = listOf(
                    navArgument(SHOW_RECIPE_ARGUMENT_KEY) {
                        type = NavType.LongType
                        nullable = false
                        defaultValue = 0L
                    }
                )
            ){ backStackEntry ->
                val recipeId = backStackEntry.arguments?.getLong(SHOW_RECIPE_ARGUMENT_KEY) ?: 0L

                mainViewModel.setCurrentTopAppBarTitle(EMPTY_STRING)
                mainViewModel.setCurrentScreen(Screen.DiscoverRecipesSingleViewScreen(context))

                DiscoverViewSingleRecipe(
                    recipeId =recipeId,
                    discoverRecipesViewModel = discoverRecipesViewModel,
                    mainViewModel = mainViewModel
                )
            }
            navigation(
                startDestination = Screen.BottomScreen.Profile(context).route,
                route = LOGIN_NAVIGATION
            ){
                composable(route = Screen.BottomScreen.Profile(context).route){
                    ProfileView(
                        onLogoutSuccess = {
                            navController.navigate(Screen.Login(context).route)
                        },
                        settingsViewModel = settingsViewModel,
                        onSignUpClick = {
                            navController.navigate(Screen.SignUp(context).route)
                        },
                        onSignInClick = {
                            navController.navigate(Screen.Login(context).route)
                        },
                        onAdvancedSettingsClick = {
                            navController.navigate(Screen.ConfigProbabilitiesScreen(context).route)
                        },
                        onDreiPunkteClick = {
                            navController.navigate(Screen.TopAppBarScreen.SendFeedback(context).route)
                        }  ,
                        profileViewModel = profileViewModel,
                        signInViewModel = signInViewModel
                    )
                    mainViewModel.setCurrentScreen(Screen.BottomScreen.Profile(context))
                }

                composable(route  = Screen.ConfigProbabilitiesScreen(context).route){backStackEntry->
                    val sharedRecipeCatalogViewModel = backStackEntry.sharedViewModel<RecipeCatalogViewModel>(navController)

                    ConfigureMyRecipesProbabilitiesView(recipeCatalogViewModel = sharedRecipeCatalogViewModel)
                    mainViewModel.setCurrentScreen(Screen.ConfigProbabilitiesScreen(context))
                }
                composable(
                    route = Screen.Login(context).route
                ){
                    LoginView(
                        onAuthSuccess = {
                            navController.navigate(Screen.BottomScreen.Profile(context).route) {
                                popUpTo("login") { inclusive = true }
                            }
                                        },
                        onForgotPasswordClick = { navController.navigate(Screen.RequestReset(context).route) },
                        signInViewModel = signInViewModel
                    )
                    mainViewModel.setCurrentScreen(Screen.Login(context))
                }


                composable(
                    route = Screen.RequestReset(context).route
                ){
                    ResetPasswordRequestView(
                        onSuccessfulPasswordResetRequest = {navController.navigate(Screen.SetNewPassword(context).route)},
                        viewModel = signInViewModel
                    )
                    mainViewModel.setCurrentScreen(Screen.RequestReset(context))
                }

                composable(
                    route = Screen.SetNewPassword(context).route
                ){
                    ResetPasswordConfirmView(
                        onPasswordSubmitSuccess = {navController.navigate(Screen.Login(context).route)},
                        viewModel = signInViewModel

                    )
                    mainViewModel.setCurrentScreen(Screen.SetNewPassword(context))
                }

                composable(
                    route = Screen.TopAppBarScreen.SendFeedback(context).route
                ){
                    val viewmodel:FeedbackViewModel = hiltViewModel()
                    SendFeedbackView(viewmodel,navController)
                    mainViewModel.setCurrentScreen(Screen.TopAppBarScreen.SendFeedback(context))
                }

                composable(
                    route = Screen.SignUp(context).route
                ){
                    SignUpView(
                        onSignUpSuccess = {
                            navController.navigate(Screen.Login(context).route)
                        },
                        signInViewModel = signInViewModel
                    )
                    mainViewModel.setCurrentScreen(Screen.SignUp(context))
                }
            }
            navigation(
                startDestination = Screen.AddUpdateRecipeScreen(context).route,
                route = ADD_EDIT_RECIPE_NAVIGATION
            ){
                composable(
                    route = Screen.AddUpdateRecipeScreen(context).route,
                    arguments = listOf(navArgument(ADD_EDIT_RECIPE_KEY){
                        type = NavType.LongType
                        nullable = false
                        defaultValue = 0L})
                )
                {
                    val id = if(it.arguments != null) it.arguments!!.getLong(ADD_EDIT_RECIPE_KEY) else 0L
                    val sharedRecipeViewModel = it.sharedViewModel<AddEditRecipeViewModel>(navController)
                    sharedRecipeViewModel.loadRecipe(id,true)
                    AddEditRecipeView(recipeId = id, addEditRecipeViewModel = sharedRecipeViewModel, navController = navController,mainViewModel)
                    mainViewModel.setChangesMade(false)
                    mainViewModel.setCurrentScreen(Screen.AddUpdateRecipeScreen(context))
                }
                composable(
                    route = Screen.AddIngredientToRecipeScreen(context).route,
                    arguments = listOf(navArgument(ADD_INGREDIENT_TO_RECIPE_SCREEN_ARGUMENT_KEY){
                        type = NavType.LongType
                        nullable = false
                        defaultValue = 0L
                    })
                ){backStackEntry->
                    val id = if(backStackEntry.arguments != null) backStackEntry.arguments!!.getLong(ADD_INGREDIENT_TO_RECIPE_SCREEN_ARGUMENT_KEY) else 0L
                    val sharedRecipeViewModel = backStackEntry.sharedViewModel<AddEditRecipeViewModel>(navController)
                    AddIngredientToRecipeView(id,navController = navController,ingredientViewModel,sharedRecipeViewModel,mainViewModel)
                    mainViewModel.setCurrentScreen(Screen.AddIngredientToRecipeScreen(context))
                }
            }
        }
        composable(route = Screen.BottomScreen.ShoppingList(context).route){
            val shoppingListViewModel:ShoppingListViewModel = hiltViewModel()
            ShoppingListView(navController = navController,shoppingListViewModel, mainViewModel = mainViewModel)
            mainViewModel.setCurrentScreen(Screen.BottomScreen.ShoppingList(context))
        }
        navigation(
            startDestination = Screen.BottomScreen.MealPlanScreen(context).route,
            route = MEALPLAN_NAVIGATION
            ){
            composable(route = Screen.BottomScreen.MealPlanScreen(context).route){

                MealPlan(navController = navController, mealPlanViewModel = mealPlanViewModel ,mainViewModel = mainViewModel)
                mainViewModel.setCurrentScreen(Screen.BottomScreen.MealPlanScreen(context))
            }
            composable(
                route = Screen.ReplaceMealPlanRecipeScreen(context).route,
                arguments = listOf(
                    navArgument(REPLACE_RECIPE_ARGUMENT_KEY) {
                        type = NavType.LongType
                        nullable = false
                        defaultValue = 0L
                    },
                    navArgument(MEALPLANDAYID_ARGUMENT_KEY) {
                        type = NavType.LongType
                        nullable = false
                        defaultValue = 0L
                    }
                )
            ) { backStackEntry ->
                val recipeToReplaceId = backStackEntry.arguments?.getLong(REPLACE_RECIPE_ARGUMENT_KEY) ?: 0L
                val mealPlanDayId = backStackEntry.arguments?.getLong(MEALPLANDAYID_ARGUMENT_KEY) ?: 0L

                //val mealPlanViewModel: MealPlanViewModel = backStackEntry.sharedViewModel(navController)
                val recipeCatalogViewModel: RecipeCatalogViewModel = backStackEntry.sharedViewModel(navController)
                mainViewModel.setCurrentScreen(Screen.ReplaceMealPlanRecipeScreen(context))
                mainViewModel.setCurrentTopAppBarTitle(stringResource(R.string.replacerecipe))

                ReplaceMealPlanRecipeView(
                    recipeToReplaceId = recipeToReplaceId,
                    mealPlanDayId = mealPlanDayId,
                    mealPlanViewModel = mealPlanViewModel,
                    navController = navController,
                    recipeCatalogViewModel = recipeCatalogViewModel,
                    mainViewModel = mainViewModel
                )

            }
            composable(
                route = Screen.AddMealPlanRecipeScreen(context).route,
                arguments = listOf(
                    navArgument(MEALPLANDAYID_ARGUMENT_KEY) {
                        type = NavType.LongType
                        nullable = false
                        defaultValue = 0L
                    }
                )
            ){backStackEntry ->
                val mealPlanDayId = backStackEntry.arguments?.getLong(MEALPLANDAYID_ARGUMENT_KEY) ?: 0L
               // val mealPlanViewModel: MealPlanViewModel = backStackEntry.sharedViewModel(navController)
                val recipeCatalogViewModel: RecipeCatalogViewModel = backStackEntry.sharedViewModel(navController)
                mainViewModel.setCurrentScreen(Screen.AddMealPlanRecipeScreen(context))
                AddMealPlanRecipeView(
                    mealPlanDayId = mealPlanDayId,
                    mealPlanViewModel = mealPlanViewModel,
                    navController = navController,
                    recipeCatalogViewModel = recipeCatalogViewModel,
                    mainViewModel = mainViewModel
                )
            }
            composable(
                route = Screen.RecipeViewOnlyScreen(context).route,
                arguments = listOf(
                    navArgument(SHOW_RECIPE_ARGUMENT_KEY) {
                        type = NavType.LongType
                        nullable = false
                        defaultValue = 0L
                    },
                    navArgument(MEALPLANDAYID_ARGUMENT_KEY) {
                        type = NavType.LongType
                        nullable = false
                        defaultValue = 0L
                    }
                )
            ){ backStackEntry ->
                val recipeId = backStackEntry.arguments?.getLong(SHOW_RECIPE_ARGUMENT_KEY) ?: 0L
                val mealPlanDayId = backStackEntry.arguments?.getLong(MEALPLANDAYID_ARGUMENT_KEY) ?: 0L
                val recipeViewModel: AddEditRecipeViewModel = backStackEntry.sharedViewModel(navController)
              //  val mealPlanViewModel: MealPlanViewModel = backStackEntry.sharedViewModel(navController)
                //val p = mealPlanViewModel.getQuantityForRecipeInMealPlan(mealPlanDayId,recipeId)

                recipeViewModel.loadRecipe(recipeId,false)
                mainViewModel.setCurrentScreen(Screen.RecipeViewOnlyScreen(context))
                RecipeViewOnlyView(
                    recipeId =recipeId,
                    mealPlanDayId  = mealPlanDayId,
                    recipeViewModel = recipeViewModel,
                    mealPlanViewModel = mealPlanViewModel,
                    navController = navController,
                    mainViewModel = mainViewModel
                )
            }
            composable(
                route = Screen.RecipeViewOnlyScreenWithOutMealplan(context).route,
                arguments = listOf(
                    navArgument(SHOW_RECIPE_ARGUMENT_KEY) {
                        type = NavType.LongType
                        nullable = false
                        defaultValue = 0L
                    }
                )
            ){ backStackEntry ->
                val recipeId = backStackEntry.arguments?.getLong(SHOW_RECIPE_ARGUMENT_KEY) ?: 0L
                val recipeViewModel: AddEditRecipeViewModel = backStackEntry.sharedViewModel(navController)
               // val mealPlanViewModel: MealPlanViewModel = backStackEntry.sharedViewModel(navController)
                //val p = mealPlanViewModel.getQuantityForRecipeInMealPlan(mealPlanDayId,recipeId)
                mainViewModel.setCurrentTopAppBarTitle(EMPTY_STRING)
                recipeViewModel.loadRecipe(recipeId,false)
                mainViewModel.setCurrentScreen(Screen.RecipeViewOnlyScreen(context))
                RecipeViewOnlyView(
                    recipeId =recipeId,
                    mealPlanDayId  = null,
                    recipeViewModel = recipeViewModel,
                    mealPlanViewModel = mealPlanViewModel,
                    navController = navController,
                    mainViewModel = mainViewModel
                )
            }
        }
        composable(route= Screen.BottomScreen.NutritionCockpitScreen(context).route) { backStackEntry ->
            NutritionDashboard(nutritionViewModel = nutritionViewModel)
            mainViewModel.setCurrentScreen(Screen.BottomScreen.NutritionCockpitScreen(context))
        }


    }
    val screensInBottom = listOf(
        Screen.BottomScreen.NutritionCockpitScreen(context),
        Screen.BottomScreen.MealPlanScreen(context),
        Screen.BottomScreen.CatalogScreen(context),
        Screen.BottomScreen.ShoppingList(context),
        Screen.BottomScreen.ShoppingList(context)
    )
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    BackHandler(enabled = true) {
        if (navController.currentDestination?.route in screensInBottom.map { it.route }) {
            // Schließe die Aktivität und spiele die Übergangsanimation ab
            activity.finish()
            activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        } else {
            // Andernfalls gehe zum vorherigen Ziel im Navigations-Stack
            navController.popBackStack()
        }
    }
}
@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavController): T {
    val navGraphRoute = destination.parent?.route ?: return hiltViewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return hiltViewModel(parentEntry)
}
sealed class NavigationEvent {
    data class NavigateTo(val screen: Screen) : NavigationEvent()
}

