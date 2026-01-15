package com.felix.mealplanner20.Shopping

import android.widget.Space
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.felix.mealplanner20.Meals.Data.Ingredient
import com.felix.mealplanner20.R
import com.felix.mealplanner20.SHOPPINGLIST_TEST_TAG
import com.felix.mealplanner20.ViewModels.MainViewModel
import com.felix.mealplanner20.Views.Components.CustomButton
import com.felix.mealplanner20.Views.Components.SwipeableItemWithActions
import com.felix.mealplanner20.Views.Mealplan.GLOBAL_CARD_ELEVATION
import com.felix.mealplanner20.ui.theme.Lime600
import com.felix.mealplanner20.ui.theme.Slate300
import com.felix.mealplanner20.ui.theme.Slate500
import com.felix.mealplanner20.ui.theme.Slate950
import com.felix.mealplanner20.ui.theme.TomatoRed
import com.felix.mealplanner20.use_cases.ShoppingListItemWithIngredient
import kotlinx.coroutines.delay
import java.util.Locale


@Composable
fun ShoppingListView(
    navController: NavController,
    shoppingListViewModel: ShoppingListViewModel,
    mainViewModel: MainViewModel
) {
    val shoppingList = shoppingListViewModel.shoppingListItems.collectAsState(emptyList())
    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag(SHOPPINGLIST_TEST_TAG)
            .background(color = MaterialTheme.colorScheme.background)
            .border(BorderStroke(1.dp, Slate300))

    ) {
        shoppingList.value?.let{
            LazyColumn(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(3.dp)
            )
            {
                items(
                    items = it,
                    key = { it.id }
                ){shoppingListItemWithIngredient->
                    NewShoppingListItem(
                        shoppingListItem = shoppingListItemWithIngredient,
                        onDeleteClick = {shoppingListViewModel.deleteItemFromShoppingList(shoppingListItemWithIngredient.id) }
                    )
                }
                item {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(75.dp)
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        CustomButton(
                            text = stringResource(R.string.clear_list),
                            onClick = {  shoppingListViewModel.clearShoppingList() },
                            width = 129.dp
                        )

                        CustomButton(
                            text = stringResource(R.string.create_list),
                            onClick = {  shoppingListViewModel.createShoppingList()},
                            width = 129.dp,
                            textColor = Color.White,
                            buttonColor = Lime600
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NewShoppingListItem(
    shoppingListItem: ShoppingListItemWithIngredient,
    onDeleteClick: ()->Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = GLOBAL_CARD_ELEVATION.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(Color.White)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ){
                IconButton(
                    onClick = {
                        onDeleteClick()
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.muelleimer_icon),
                        contentDescription = "Mehr Optionen",
                        tint = Slate500
                    )
                }

                val isGerman = Locale.getDefault().language == "de"

                Text(
                    text = if (isGerman)shoppingListItem.ingredient.germanName else shoppingListItem.ingredient.englishName?:shoppingListItem.ingredient.germanName,
                    style = MaterialTheme.typography.titleMedium,
                    color = Slate500
                )
            }
            Text(
                text = "${shoppingListItem.quantity.toInt()} ${shoppingListItem.unitOfMeasure.toUOMshoppingListshortcut(
                    LocalContext.current)}",
                style = MaterialTheme.typography.titleLarge,
                color = Slate950
            )
        }
    }
}