package com.felix.mealplanner20.Views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.felix.mealplanner20.Meals.Data.Ingredient
import com.felix.mealplanner20.Meals.Data.helpers.UserRoles
import com.felix.mealplanner20.R
import com.felix.mealplanner20.Screen
import com.felix.mealplanner20.ViewModels.IngredientViewModel
import com.felix.mealplanner20.ViewModels.MainViewModel
import com.felix.mealplanner20.Views.Mealplan.GLOBAL_CARD_ELEVATION
import com.felix.mealplanner20.ui.theme.Lime600
import com.felix.mealplanner20.ui.theme.Slate200
import com.felix.mealplanner20.ui.theme.Slate300
import com.felix.mealplanner20.ui.theme.Slate500
import com.felix.mealplanner20.ui.theme.Slate950
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientHomeView(
    navController: NavController,
    ingredientViewModel: IngredientViewModel,
    mainViewModel: MainViewModel
){
   /* val isLoading by ingredientViewModel.isLoading.collectAsState()

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
        ) {
            CircularProgressIndicator2()
        }
    }
    else{*/

        val userRole by ingredientViewModel.userRole.collectAsState()
        val isRef by ingredientViewModel.isRefreshing.collectAsState()
        val filteredIngredients by ingredientViewModel.filteredIngredients.collectAsState()
        val context = LocalContext.current

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.background)
                    .border(BorderStroke(1.dp, Slate300))
            ) {
                LazyColumn(modifier = Modifier
                    .wrapContentSize()
                    .padding(3.dp))
                {
                    items(
                        items = filteredIngredients,
                        key = { it.id }
                    ){ingredient->
                        IngredientItem(ingredient) {
                            val id = ingredient.id
                            navController.navigate(Screen.AddUpdateIngredientScreen(context = context).passId(id))
                            mainViewModel.setCurrentScreen(Screen.AddUpdateIngredientScreen(context))
                        }
                    }
                }
                Box(
                    modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(0.dp, 0.dp, 24.dp, 16.dp)
                ){
                    Column{
                        if(userRole == UserRoles.FOODADMIN.toString()){
                            FloatingActionButton(
                                onClick = {
                                    navController.navigate(Screen.AddUpdateIngredientScreen(context).passId(0L))
                                    mainViewModel.setCurrentScreen(Screen.AddUpdateIngredientScreen(context))
                                },
                                shape = CircleShape
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add")
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }

}

@Composable
fun IngredientItem(ingredient:Ingredient, onClick: ()->Unit){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(191.dp)
            .padding(top = 16.dp,start = 16.dp,end = 16.dp, bottom = 0.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = GLOBAL_CARD_ELEVATION.dp),
        colors = CardColors(containerColor = Color.White, contentColor = Slate950, disabledContentColor = Slate950, disabledContainerColor = Color.White)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 1.dp)
                    .height(55.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 12.dp,  // Oben links abgerundet
                            topEnd = 12.dp,    // Oben rechts abgerundet
                            bottomStart = 0.dp, // Unten eckig
                            bottomEnd = 0.dp   // Unten eckig
                        )
                    ),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                val isGerman = Locale.getDefault().language == "de"
                Text(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterVertically),
                    text = if (isGerman) ingredient.germanName else ingredient.englishName?: ingredient.germanName,
                    style = MaterialTheme.typography.bodyMedium)
                IconButton(onClick = {}) {
                    Icon(painter = painterResource(R.drawable.add_icon), tint = Lime600, contentDescription = "add Icon")
                }
            }
            Row(modifier = Modifier.weight(1f)) {
                Box(modifier = Modifier.weight(1f)){
                    LabeledValueBox(stringResource(R.string.calories),ingredient.calories)
                }
                Box(modifier = Modifier.weight(1f)){
                    LabeledValueBox(stringResource(R.string.fat),ingredient.fat) }
                }
            Row( modifier = Modifier.weight(1f)) {
                Box(modifier = Modifier.weight(1f)){
                    LabeledValueBox(stringResource(R.string.carbs),ingredient.carbs)

                }
                Box(modifier = Modifier.weight(1f)){
                    LabeledValueBox(stringResource(R.string.protein),ingredient.protein)

                }
            }
        }
    }
}
@Composable
fun LabeledValueBox(label:String, value: Float){
    Box( modifier = Modifier.fillMaxHeight()
        .border(width = 0.5.dp, color = Slate200)
        ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(start = 16.dp),
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(color = Slate500, fontSize = 12.sp))
            Text(
                modifier = Modifier.padding(end = 16.dp),
                text = value.toString(),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
@Composable
fun LabeledIntValueBox(label:String, value: Int){
    Box( modifier = Modifier
        .border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(5.dp))
        .clip(RoundedCornerShape(5.dp))
    ) {
        Column(
            modifier = Modifier
                .padding(4.dp, 0.dp, 4.dp, 0.dp)
                .clip(RoundedCornerShape(16.dp)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = label)
            Text(text = value.toString())
        }
    }
}

@Composable
fun SpacerInBetweenLabeledValueBoxes(scaleing: Float = 1.0f){
    Spacer(modifier = Modifier.width(6.dp*scaleing))
}