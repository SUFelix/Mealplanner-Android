package com.felix.mealplanner20.Views.Mealplan

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.felix.mealplanner20.R
import com.felix.mealplanner20.ViewModels.MainViewModel
import com.felix.mealplanner20.ViewModels.MealPlanViewModel
import com.felix.mealplanner20.ViewModels.RecipeCatalogViewModel
import com.felix.mealplanner20.Views.Components.MyCircularProgressIndicator
import com.felix.mealplanner20.Views.Recipes.ResponsiveImageCard
import com.felix.mealplanner20.ui.theme.Slate200
import com.felix.mealplanner20.ui.theme.Slate300

@Composable
fun AddMealPlanRecipeView(
    mealPlanDayId:Long,
    mealPlanViewModel: MealPlanViewModel,
    navController: NavController,
    recipeCatalogViewModel: RecipeCatalogViewModel,
    mainViewModel: MainViewModel
)
{
    val isLoading by recipeCatalogViewModel.isLoading.collectAsState()

    if (isLoading) {
        mainViewModel.setCurrentTopAppBarTitle((stringResource(R.string.add_recipe)))
            MyCircularProgressIndicator()
    }
    else{
        val recipeList = recipeCatalogViewModel.getAllRecipes.collectAsState(initial = listOf())
        mainViewModel.setCurrentTopAppBarTitle((stringResource(R.string.add_recipe)))
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Slate200)
                .border(BorderStroke(1.dp, Slate300))
        ){
            Column {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2), // Zwei Spalten für das Grid
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp, start = 16.dp,end = 16.dp), // Padding unten, damit die FAB sichtbar bleibt
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    content = {
                        items(
                            items = recipeList.value,
                            key = { it.id }
                        ){recipe->
                            ResponsiveImageCard(
                                recipe = recipe,
                                onClick = {
                                    val recipeToAddId = recipe.id
                                    mealPlanDayId?.let { mealPlanDayId ->
                                        mealPlanViewModel.addMealToMealplanDay(mealPlanDayId, recipeToAddId)
                                        navController.navigateUp()
                                    }
                                }
                            )
                        }
                    }
                )
            }
        }
    }
}