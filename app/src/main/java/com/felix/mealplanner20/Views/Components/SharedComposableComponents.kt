package com.felix.mealplanner20.Views.Components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.felix.mealplanner20.Meals.Data.Recipe
import com.felix.mealplanner20.Meals.Data.RecipeCalories
import com.felix.mealplanner20.R
import com.felix.mealplanner20.ViewModels.RecipeCatalogViewModel
import com.felix.mealplanner20.Views.LabeledIntValueBox
import com.felix.mealplanner20.Views.Mealplan.GLOBAL_CARD_ELEVATION
import com.felix.mealplanner20.ui.theme.Slate300

@Composable
fun MyRecipeListItem(recipe: Recipe, calories: RecipeCalories, recipeViewModel: RecipeCatalogViewModel, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, end = 8.dp, start = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = GLOBAL_CARD_ELEVATION.dp),
        border = BorderStroke(1.dp, color = Color.DarkGray)
    ) {
        Column(modifier = Modifier.padding(16.dp, 6.dp, 16.dp, 6.dp)) {
            Text(text = recipe.title, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(6.dp))
            Row {
                LabeledIntValueBox(stringResource(R.string.calories), calories.totalCalories.toInt())
                Spacer(modifier = Modifier.width(20.dp))
                var typeIcon = R.drawable.baseline_bakery_dining_24
                when{
                    recipe.isMeal->typeIcon = R.drawable.baseline_dinner_dining_24
                    recipe.isBreakfast->typeIcon = R.drawable.baseline_bakery_dining_24
                    recipe.isSnack->typeIcon = R.drawable.baseline_lunch_dining_24
                    else -> typeIcon = R.drawable.baseline_coffee_24
                }
                Image(
                    modifier = Modifier
                        .size(30.dp)
                        .padding(top = 4.dp),
                    painter = painterResource(id = typeIcon) ,
                    contentDescription = "Type"
                )
                Spacer(modifier = Modifier.width(200.dp))
            }
        }
    }
}
@Composable
fun RecipeCatalogHeadline(headline:String){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp)
            .padding(16.dp, 0.dp, 0.dp, 0.dp)
    ){
        Text(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.CenterVertically),
            text = headline,
            style = MaterialTheme.typography.headlineSmall)
    }
}
@Composable
operator fun PaddingValues.plus(other: PaddingValues): PaddingValues {
    val layoutDirection = LocalLayoutDirection.current
    return PaddingValues(
        start = this.calculateStartPadding(layoutDirection) + other.calculateStartPadding(layoutDirection),
        top = this.calculateTopPadding() + other.calculateTopPadding(),
        end = this.calculateEndPadding(layoutDirection) + other.calculateEndPadding(layoutDirection),
        bottom = this.calculateBottomPadding() + other.calculateBottomPadding()
    )
}

@Composable
fun CustomAlertDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    title:String = stringResource(R.string.confirm_regeneration),
    text:String = stringResource(R.string.m_chtest_du_wirklich_einen_neuen_essensplan_erstellen)
) {
    AlertDialog(
        containerColor = Slate300,
        onDismissRequest = onDismiss,
        title = {
            Text(
                style  = MaterialTheme.typography.titleLarge,
                text = title
            )
        },
        text = {
            Text(
                style = MaterialTheme.typography.titleMedium,
                text = text)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
                    onDismiss()
                }
            ) {
                Text(
                    style = MaterialTheme.typography.labelLarge,
                    text= stringResource(R.string.confirm)
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    style = MaterialTheme.typography.labelLarge,
                    text = stringResource(R.string.cancel)
                )
            }
        }
    )
}