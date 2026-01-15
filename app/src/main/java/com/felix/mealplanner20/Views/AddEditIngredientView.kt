package com.felix.mealplanner20.Views

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.felix.mealplanner20.Meals.Data.EMPTY_STRING
import com.felix.mealplanner20.Meals.Data.Ingredient
import com.felix.mealplanner20.Meals.Data.helpers.UnitOfMeasure
import com.felix.mealplanner20.Meals.Data.helpers.dgeGroup
import com.felix.mealplanner20.R
import com.felix.mealplanner20.Screen
import com.felix.mealplanner20.ViewModels.IngredientViewModel
import com.felix.mealplanner20.ViewModels.MainViewModel
import kotlinx.coroutines.launch

@SuppressLint("SuspiciousIndentation")
@Composable
fun AddEditIngredientView(
    id: Long,
    ingredientViewModel: IngredientViewModel,
    navController: NavController,
    mainViewModel: MainViewModel
)
{

    val snackMessage = remember { mutableStateOf(EMPTY_STRING) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    if (id != 0L) {
        val ingredient = ingredientViewModel.getIngredientById(id).collectAsState(
            initial = Ingredient(0L,
                EMPTY_STRING,englishName = null,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f, dgeGroup.MILK,0.0f
            )
        )

            ingredient?.let{ingredient
                ingredient.value?.let{value->
                    ingredientViewModel.ingredientNameState = value.germanName
                    ingredientViewModel.ingredientCaloriesState = value.calories.toString()
                    ingredientViewModel.ingredientFatState = value.fat.toString()
                    ingredientViewModel.ingredientSaturatedFatState = value.saturatedFat.toString()
                    ingredientViewModel.ingredientCarbsState = value.carbs.toString()
                    ingredientViewModel.ingredientSugarState = value.sugar.toString()
                    ingredientViewModel.ingredientProteinState = value.protein.toString()
                    ingredientViewModel.ingredientFibreState = value.fibre.toString()
                    ingredientViewModel.ingredientAlcoholState = value.alcohol.toString()
                    ingredientViewModel.ingredientDgeTypeState = value.dgeType
                    ingredientViewModel.unitOfMeasureState= value.unitOfMeasure
                }
            }

    } else {
        ingredientViewModel.ingredientNameState = stringResource(R.string.new_ingredient_defaultname)
        ingredientViewModel.ingredientCaloriesState = "0"
        ingredientViewModel.ingredientFatState = "0"
        ingredientViewModel.ingredientSaturatedFatState = "0"
        ingredientViewModel.ingredientCarbsState = "0"
        ingredientViewModel.ingredientSugarState = "0"
        ingredientViewModel.ingredientProteinState = "0"
        ingredientViewModel.ingredientFibreState = "0"
        ingredientViewModel.ingredientAlcoholState = "0"
        ingredientViewModel.ingredientDgeTypeState = dgeGroup.GRAIN
        ingredientViewModel.unitOfMeasureState= UnitOfMeasure.GRAM
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ){
        Column(
            modifier = Modifier
                .padding(it)
                .wrapContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            InBetweenRowSpacer(1.5f)
            Row(
                modifier = Modifier
                    .padding(getMyRowPadding())
            ){
                IngredientNameTextField(
                ingredientName = ingredientViewModel.ingredientNameState,
                onValueChange = { ingredientViewModel.onIngredientNameChange(it) }
            )}

            InBetweenRowSpacer()
            val defaultFloat = 0.33f
            Row (modifier = Modifier
                .fillMaxWidth()
                .padding(getMyRowPadding()),
                horizontalArrangement = Arrangement.SpaceAround
            ){
                IngredientNumberTextField(
                    number = ingredientViewModel.ingredientCaloriesState,
                    label = stringResource(R.string.calories),
                    onValueChange ={ingredientViewModel.onIngredientCaloriesChange(it)}
                )
                IngredientNumberTextField(
                    number = ingredientViewModel.ingredientFatState,
                    label = stringResource(R.string.fat),
                    onValueChange = {ingredientViewModel.onIngredientFatChange(it)}
                )
                IngredientNumberTextField(
                    number =  ingredientViewModel.ingredientCarbsState,
                    label = stringResource(R.string.carbs),
                    onValueChange = {ingredientViewModel.onIngredientCarbsChange(it)})
            }
            InBetweenRowSpacer()
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(getMyRowPadding()),
                horizontalArrangement = Arrangement.SpaceAround
            ){
                IngredientNumberTextField(
                    number = ingredientViewModel.ingredientSugarState,
                    label = stringResource(R.string.sugar),
                    onValueChange = {ingredientViewModel.onIngredientSugarChange(it)})
                IngredientNumberTextField(
                    number = ingredientViewModel.ingredientSaturatedFatState,
                    label = stringResource(R.string.saturated_fat),
                    onValueChange = {ingredientViewModel.onIngredientSaturatedFatChange(it)}
                )
                IngredientNumberTextField(
                    number = ingredientViewModel.ingredientProteinState,
                    label = stringResource(R.string.protein),
                    onValueChange = {ingredientViewModel.onIngredientProteinChange(it)}
                )
            }
            InBetweenRowSpacer()
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(getMyRowPadding()),
                horizontalArrangement = Arrangement.SpaceAround
            ){
                IngredientNumberTextField(
                    number =  ingredientViewModel.ingredientFibreState,
                    label = stringResource(R.string.fibre),
                    onValueChange = { ingredientViewModel.onIngredientFibreChange(it)}
                )
                IngredientNumberTextField(
                    number = ingredientViewModel.ingredientAlcoholState,
                    label = stringResource(R.string.alcohol),
                    onValueChange = {ingredientViewModel.onIngredientAlcoholChange(it)}
                )
                EnumDropdown(ingredientViewModel.ingredientDgeTypeState, stringResource(R.string.dge_label),
                    onValueChange = {ingredientViewModel.onIngredientDgeTypeChange(it.toString())})
            }
            EnumDropdown(ingredientViewModel.unitOfMeasureState, stringResource(R.string.uom_label),
                onValueChange = {ingredientViewModel.onUnitOfMeasureChange(it.toString())})
            InBetweenRowSpacer()
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(getMyRowPadding())){
                Button(
                    colors = ButtonColors(
                        colorScheme.primary,
                        colorScheme.onPrimary,
                        colorScheme.primaryContainer,
                        colorScheme.onPrimaryContainer
                    ),
                    onClick = {
                        if(noStateIsEmpty(ingredientViewModel)){
                            if(id != 0L){
                                ingredientViewModel.uploadUpdateIngredient(
                                    Ingredient(
                                        id = id,
                                        germanName = ingredientViewModel.ingredientNameState.trim(),
                                        englishName = null,
                                        calories = ingredientViewModel.ingredientCaloriesState.toFloat(),
                                        fat = ingredientViewModel.ingredientFatState.toFloat(),
                                        saturatedFat = ingredientViewModel.ingredientSaturatedFatState.toFloat(),
                                        carbs = ingredientViewModel.ingredientCarbsState.toFloat(),
                                        sugar = ingredientViewModel.ingredientSugarState.toFloat(),
                                        protein = ingredientViewModel.ingredientProteinState.toFloat(),
                                        fibre = ingredientViewModel.ingredientFibreState.toFloat(),
                                        alcohol = ingredientViewModel.ingredientAlcoholState.toFloat(),
                                        dgeType = ingredientViewModel.ingredientDgeTypeState,
                                        unitOfMeasure = ingredientViewModel.unitOfMeasureState)
                                )
                                snackMessage.value = "Ingredient has been updated"
                            }else{
                                ingredientViewModel.uploadIngredient(
                                    Ingredient(
                                        germanName = ingredientViewModel.ingredientNameState,
                                        englishName = null,
                                        calories = ingredientViewModel.ingredientCaloriesState.toFloat(),
                                        fat = ingredientViewModel.ingredientFatState.toFloat(),
                                        saturatedFat = ingredientViewModel.ingredientSaturatedFatState.toFloat(),
                                        carbs = ingredientViewModel.ingredientCarbsState.toFloat(),
                                        sugar = ingredientViewModel.ingredientSugarState.toFloat(),
                                        protein = ingredientViewModel.ingredientProteinState.toFloat(),
                                        fibre = ingredientViewModel.ingredientFibreState.toFloat(),
                                        alcohol = ingredientViewModel.ingredientAlcoholState.toFloat(),
                                        dgeType = ingredientViewModel.ingredientDgeTypeState,
                                        unitOfMeasure = ingredientViewModel.unitOfMeasureState
                                    ))
                                snackMessage.value = "Ingredient has been created"
                            }
                        }else{
                            snackMessage.value = "Enter fields to create an Ingredient"
                        }
                        scope.launch {
                            //snackbarHostState.showSnackbar(snackMessage.value)
                            val navSuccess =  navController.navigateUp()
                            if(navSuccess)
                                mainViewModel.setCurrentScreen(Screen.IngredientHomeScreen(context))
                        }
                    }
                ) {
                    Text(
                        text = if (id != 0L) {
                            stringResource(R.string.update_ingredient)
                        } else {
                            stringResource(R.string.add_ingredient)
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                /*
                EighthWidthSpacer()
                TODO: sollen FOODADMIN ingredients deleten dürfen?! erstmal nicht.
                Button(
                    colors = ButtonColors(
                        colorScheme.tertiary,
                        colorScheme.onTertiary,
                        colorScheme.tertiaryContainer,
                        colorScheme.onTertiaryContainer),
                    onClick = {
                        ingredientViewModel.deleteIngredientById(id = id)
                    scope.launch {
                        val b = navController.navigateUp()
                        if(b)
                            mainViewModel.setCurrentScreen(Screen.IngredientHomeScreen(context))
                    }
                }) {
                    Text(text = stringResource(R.string.delete_text))
                }*/

            }

        }
    }
}

@Composable
fun IngredientNameTextField(
    ingredientName: String,
    onValueChange:(String)->Unit
){
    OutlinedTextField(
        value = ingredientName,
        onValueChange = onValueChange,
        label = { Text(text = stringResource(R.string.name), color = Color.Black,fontWeight = FontWeight.Bold) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(getMyDoubleFieldPadding()),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        colors = TextFieldDefaults.colors(
        )
    )
}

@Composable
fun EighthWidthSpacer(){
    Spacer(modifier = Modifier.width((ScreenWidthInDp() / 8.0).dp))
}

@Composable
fun InBetweenRowSpacer(scaling:Float  = 1.0f){
    Spacer(modifier = Modifier.height(6.dp*scaling))
}

@Composable
fun IngredientNumberTextField(
    number: String,
    label: String,
    onValueChange:(String)->Unit
){
    OutlinedTextField(
        value = number,
        onValueChange = onValueChange,
        label = { Text(text = label, color = Color.Black,fontWeight = FontWeight.Bold) },
        modifier = Modifier
            .width((ScreenWidthInDp() / 3.4).dp)
            .padding(getMyFieldPadding())
            .height(65.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
        colors = TextFieldDefaults.colors(
        )
    )
}

fun noStateIsEmpty(viewModel: IngredientViewModel):Boolean{
    if(
        viewModel.ingredientNameState.isNotEmpty() &&
        viewModel.ingredientCaloriesState.isNotEmpty() &&
        viewModel.ingredientFatState.isNotEmpty() &&
        viewModel.ingredientCarbsState.isNotEmpty() &&
        viewModel.ingredientProteinState.isNotEmpty() &&
        viewModel.ingredientSaturatedFatState.isNotEmpty() &&
        viewModel.ingredientAlcoholState.isNotEmpty() &&
        viewModel.ingredientFibreState.isNotEmpty() &&
        viewModel.ingredientSugarState.isNotEmpty()
        ){return true}
    else return false
}

fun getMyRowPadding(): PaddingValues {
    return PaddingValues(start = 8.dp, top = 3.dp, end = 8.dp, bottom = 3.dp)
}
fun getMyFieldPadding(): PaddingValues {
    return PaddingValues(start = 8.dp, top = 3.dp, end = 8.dp, bottom = 3.dp)
}
fun getMyDoubleFieldPadding(): PaddingValues {
    return PaddingValues(start = 16.dp, top = 6.dp, end = 16.dp, bottom = 6.dp)
}

@Composable
fun ScreenWidthInDp(): Float {
    val configuration = LocalConfiguration.current
    val screenWidthPx = configuration.screenWidthDp
    return screenWidthPx.toFloat()
}


@Composable
fun <T : Enum<T>> EnumDropdown(
    selectedEnum: T,
    label: String,
    onValueChange: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .width((ScreenWidthInDp() / 3.4).dp)
            .height(70.dp)
            .padding(start = 8.dp, top = 12.dp, end = 8.dp, bottom = 3.dp)
            .border(
                width = 1.dp,
                color = Color.Black,
                shape = RoundedCornerShape(4.dp)
            )
    ) {
        OutlinedTextFieldLikeButton(
            text = selectedEnum.name,
            onClick = { expanded = true }
        )
        Icon(Icons.Default.ArrowDropDown, contentDescription = "")

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            @Suppress("UNCHECKED_CAST")
            (selectedEnum::class.java.enumConstants as Array<T>).forEach { enumValue ->
                DropdownMenuItem(
                    text = { Text(enumValue.name) },
                    onClick = {
                        expanded = false
                        onValueChange(enumValue)
                    }
                )
            }
        }
    }
}


@Composable
fun OutlinedTextFieldLikeButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = TextFieldDefaults.colors().focusedContainerColor
        ),
        shape = RoundedCornerShape(4.dp),
        elevation = ButtonDefaults.buttonElevation(0.dp)
    ) {
        Text(
            text = text,
            color = Color.Black,
            fontWeight = FontWeight.Normal
        )
    }
}