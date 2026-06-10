package com.felix.mealplanner20.Shopping

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.felix.mealplanner20.R
import com.felix.mealplanner20.SHOPPINGLIST_TEST_TAG
import com.felix.mealplanner20.ViewModels.MainViewModel
import com.felix.mealplanner20.Views.Components.CustomButton
import com.felix.mealplanner20.Views.Mealplan.GLOBAL_CARD_ELEVATION
import com.felix.mealplanner20.ui.theme.Lime600
import com.felix.mealplanner20.ui.theme.Slate300
import com.felix.mealplanner20.ui.theme.Slate500
import com.felix.mealplanner20.ui.theme.Slate950
import com.felix.mealplanner20.use_cases.ShoppingListItemWithIngredient
import java.util.Locale


@Composable
fun ShoppingListView(
    navController: NavController,
    shoppingListViewModel: ShoppingListViewModel,
    mainViewModel: MainViewModel
) {
    val shoppingList = shoppingListViewModel.shoppingListItems.collectAsState(emptyList())
    val showCustomItemInput by shoppingListViewModel.showCustomItemInput.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag(SHOPPINGLIST_TEST_TAG)
            .background(color = MaterialTheme.colorScheme.background)
            .border(BorderStroke(1.dp, Slate300))
    ) {
        shoppingList.value?.let {
            LazyColumn(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(3.dp)
            ) {
                items(
                    items = it,
                    key = { item -> item.id }
                ) { shoppingListItemWithIngredient ->
                    NewShoppingListItem(
                        shoppingListItem = shoppingListItemWithIngredient,
                        onDeleteClick = { shoppingListViewModel.deleteItemFromShoppingList(shoppingListItemWithIngredient.id) },
                        onToggleChecked = { checked -> shoppingListViewModel.toggleItemChecked(shoppingListItemWithIngredient.id, checked) }
                    )
                }

                item {
                    AddCustomItemRow(
                        expanded = showCustomItemInput,
                        onToggle = { shoppingListViewModel.toggleCustomItemInput() },
                        onConfirm = { name -> shoppingListViewModel.addCustomItem(name) }
                    )
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(75.dp)
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        CustomButton(
                            text = stringResource(R.string.clear_list),
                            onClick = { shoppingListViewModel.clearShoppingList() },
                            width = 129.dp
                        )

                        CustomButton(
                            text = stringResource(R.string.create_list),
                            onClick = { shoppingListViewModel.createShoppingList() },
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
private fun AddCustomItemRow(
    expanded: Boolean,
    onToggle: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var text by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {
                        if (expanded) text = ""
                        onToggle()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add item",
                            tint = Lime600
                        )
                    }
                    Text(
                        text = stringResource(R.string.add_custom_item),
                        style = MaterialTheme.typography.titleMedium,
                        color = Slate500
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text(stringResource(R.string.custom_item_placeholder)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        keyboardController?.hide()
                        onConfirm(text)
                        text = ""
                    }),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        keyboardController?.hide()
                        onConfirm(text)
                        text = ""
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Confirm",
                        tint = Lime600,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun NewShoppingListItem(
    shoppingListItem: ShoppingListItemWithIngredient,
    onDeleteClick: () -> Unit = {},
    onToggleChecked: (Boolean) -> Unit = {}
) {
    val isCustom = shoppingListItem.customName != null

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
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.muelleimer_icon),
                        contentDescription = "Mehr Optionen",
                        tint = Slate500
                    )
                }

                val displayName = if (isCustom) {
                    shoppingListItem.customName!!
                } else {
                    val isGerman = Locale.getDefault().language == "de"
                    val ingredient = shoppingListItem.ingredient
                    if (isGerman) ingredient?.germanName ?: ""
                    else ingredient?.englishName ?: ingredient?.germanName ?: ""
                }

                Text(
                    text = displayName,
                    style = MaterialTheme.typography.titleMedium,
                    color = Slate500.copy(alpha = if (shoppingListItem.isChecked) 0.4f else 1f),
                    textDecoration = if (shoppingListItem.isChecked) TextDecoration.LineThrough else TextDecoration.None
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!isCustom) {
                    Text(
                        text = "${shoppingListItem.quantity.toInt()} ${shoppingListItem.unitOfMeasure.toUOMshoppingListshortcut(LocalContext.current)}",
                        style = MaterialTheme.typography.titleLarge,
                        color = Slate950.copy(alpha = if (shoppingListItem.isChecked) 0.4f else 1f)
                    )
                }
                Checkbox(
                    checked = shoppingListItem.isChecked,
                    onCheckedChange = onToggleChecked,
                    colors = CheckboxDefaults.colors(
                        checkedColor = Lime600,
                        uncheckedColor = Slate500
                    )
                )
            }
        }
    }
}
