package com.felix.mealplanner20.Views.Recipes

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.felix.mealplanner20.Meals.Data.EMPTY_STRING
import com.felix.mealplanner20.Meals.Data.helpers.UnitOfMeasure
import com.felix.mealplanner20.ViewModels.IngredientViewModel
import com.felix.mealplanner20.ViewModels.AddEditRecipeViewModel
import com.felix.mealplanner20.ViewModels.MainViewModel
import com.felix.mealplanner20.Views.IngredientItem
import com.felix.mealplanner20.ui.theme.Slate200
import com.felix.mealplanner20.ui.theme.Slate300
import kotlinx.coroutines.launch

@Composable
fun AddIngredientToRecipeView(recipeId: Long,
                              navController: NavController,
                              ingredientViewModel: IngredientViewModel,
                              recipeViewModel: AddEditRecipeViewModel,
                              mainViewModel: MainViewModel
) {

    val filteredIngredients by ingredientViewModel.filteredIngredients.collectAsState()
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxSize().background(Slate200).border(BorderStroke(1.dp, Slate300))
    ) {
        LazyColumn(modifier = Modifier
            .wrapContentSize()
            .padding(3.dp))
        {
            items(
                items = filteredIngredients,
                key = { it.id }
            ){ingredient->
                IngredientItem(ingredient
                ) {
                    val qty = getInitQuantityFromUOM(ingredient.unitOfMeasure)
                    if (recipeId!=0L){
                        val ingredientData = Triple(ingredient.id, qty, ingredient.unitOfMeasure)
                        recipeViewModel.changeAnything(
                            ingredientToAdd = ingredientData,
                            additionalAction = {mainViewModel.setChangesMade(true)}
                        )
                    }
                    else{
                        val ingredientData = Triple(ingredient.id, qty, ingredient.unitOfMeasure)
                        recipeViewModel.changeAnything(
                            tempIngredientToAdd = ingredientData,
                            additionalAction = {mainViewModel.setChangesMade(true)}
                        )
                    }

                   scope.launch {
                        ingredientViewModel.closeSearchbarIfOpen()
                        navController.navigateUp()
                    }
                }
            }
        }
    }
}

fun getInitQuantityFromUOM(unitOfMeasure: UnitOfMeasure): Float {
    return when (unitOfMeasure) {
        UnitOfMeasure.GRAM -> 100f
        UnitOfMeasure.MILLILITER -> 100f
        UnitOfMeasure.PIECE -> 1f
        UnitOfMeasure.LITER -> 1f
        UnitOfMeasure.CUP -> 1f
        UnitOfMeasure.TABLESPOON -> 1f
        UnitOfMeasure.TEASPOON -> 1f
        UnitOfMeasure.CLOVE -> 1f
    }
}

