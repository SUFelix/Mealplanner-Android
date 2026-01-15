package com.felix.mealplanner20.Views.Mealplan

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.felix.mealplanner20.MEALPLAN_TEST_TAG
import com.felix.mealplanner20.Meals.Data.MealPlanDay
import com.felix.mealplanner20.Meals.Data.helpers.RecipeItem
import com.felix.mealplanner20.Meals.Data.helpers.RecipeQuantity
import com.felix.mealplanner20.R
import com.felix.mealplanner20.Screen
import com.felix.mealplanner20.ViewModels.MainViewModel
import com.felix.mealplanner20.ViewModels.MealPlanViewModel
import com.felix.mealplanner20.ViewModels.RecipeCatalogViewModel
import com.felix.mealplanner20.Views.Components.MyCircularProgressIndicator
import com.felix.mealplanner20.Views.Components.CustomButton
import com.felix.mealplanner20.Views.Components.CustomFullWidthButton
import com.felix.mealplanner20.ui.theme.Lime600
import com.felix.mealplanner20.ui.theme.Slate200
import com.felix.mealplanner20.ui.theme.Slate300
import com.felix.mealplanner20.ui.theme.Slate400
import com.felix.mealplanner20.ui.theme.Slate500
import com.felix.mealplanner20.ui.theme.Slate950
import kotlinx.coroutines.launch

const val GLOBAL_CARD_ELEVATION =  2
const val packageName = "com.felix.mealplanner20"

@Composable
fun MealPlan(
    navController: NavController,
    mealPlanViewModel: MealPlanViewModel,
    mainViewModel: MainViewModel
){
    val mealPlanList = mealPlanViewModel.getAllMealPlanDays.collectAsState(initial = listOf())
    val recipeCaloriesMap by mealPlanViewModel.recipeCaloriesMap.collectAsState()

    //val isLoading by mealPlanViewModel.isLoading.collectAsState()

    /*if (isLoading) {
            MyCircularProgressIndicator()
    }
    else*/
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Slate200)
                .border(BorderStroke(1.dp, Slate300))
                .testTag(MEALPLAN_TEST_TAG)
    ) {
        var showGenerateMealplanAlertDialog by remember { mutableStateOf(false) }

        if (showGenerateMealplanAlertDialog) {
            val context = LocalContext.current
            GenerateMealPlanAlertDialog(

                onConfirm = {
                    mealPlanViewModel.generateMealPlan(context)
                    showGenerateMealplanAlertDialog = false
                            },
                onDismiss = { showGenerateMealplanAlertDialog = false }
            )
        }

        LazyColumn {
            itemsIndexed(
                items = mealPlanList.value,
                key = { _,item -> item.id }
            ) { index, item ->
                SingleDayPart(
                    mealPlanDay = item,
                    navController = navController,
                    mealplanViewModel = mealPlanViewModel,
                    recipeCaloriesMap = recipeCaloriesMap,
                    dayNumber = index + 1
                )
            }

            item {
                Box(
                    modifier = Modifier
                        .height(80.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ){
                    CustomButton(
                        text = stringResource(R.string.create_mealplan),
                        onClick = {   showGenerateMealplanAlertDialog = true },
                        textColor = Color.White,
                        buttonColor = Lime600,
                        borderColor = Slate950,
                        modifier = Modifier
                            .wrapContentWidth()
                            .height(50.dp)
                            .padding(end = 16.dp)
                    )
                }
            }
        }
    }
    }

@Composable
fun SingleDayPart(
    mealPlanDay: MealPlanDay,
    navController: NavController,
    mealplanViewModel: MealPlanViewModel,
    recipeCaloriesMap: Map<Long, Float>,
    dayNumber: Int = 1
) {
    val recipeCatalogViewModel: RecipeCatalogViewModel = hiltViewModel()

    val recipeQuantities by mealplanViewModel
        .getRecipeQuantitiesForDay(mealPlanDay.id)
        .collectAsState(initial = emptyList())

    val recipeIds = remember(recipeQuantities) { recipeQuantities.map { it.recipeId } }

    val recipes by recipeCatalogViewModel
        .getRecipesByIds(recipeIds)
        .collectAsState(initial = emptyList())

    val recipeItems = remember(recipes) {
        recipes.map { recipe -> RecipeItem(recipe.id, recipe.title) }
    }

    val totalCalories = recipeQuantities.sumOf { rq ->
        ((recipeCaloriesMap[rq.recipeId] ?: 0f) * rq.quantity).toDouble()
    }.toInt()


    Box(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp, top = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Text(
                    modifier = Modifier.padding(start = 16.dp),
                    text = stringResource(R.string.day)+" "+dayNumber,
                    style = MaterialTheme.typography.titleLarge.copy(color = Slate500)

                )

                Text(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .align(Alignment.Bottom),
                    text = "${totalCalories} kCal",
                    style = MaterialTheme.typography.titleLarge
                )
            }
            LazyRow {
                itemsIndexed(recipeQuantities, key = { _, item -> item.recipeId }) { index, recipeQuantity ->
                    val recipe = recipeItems.find { it.id == recipeQuantity.recipeId }
                    val isFirst = (index == 0)

                    recipe?.let {
                        OneMealLazyRowItem(
                            recipeTitle = it.title,
                            recipeId = it.id,
                            mealPlanDayId = mealPlanDay.id,
                            recipesIdsWithQuantity = recipeQuantity,
                            navController = navController,
                            mealPlanViewModel = mealplanViewModel,
                            isFirst = isFirst
                        )
                    }
                }
                item {
                    val context = LocalContext.current
                    DashedAddButton(onClick = {
                        navController.navigate(Screen.AddMealPlanRecipeScreen(context).passId(mealPlanDay.id))
                    })
                }
            }
        }
    }
}

@Composable
fun DashedAddButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .width(182.dp)
            .height(276.5.dp)
            .clickable { onClick() }
            .clip(RoundedCornerShape(12.dp))
    ) {
        Canvas(
            modifier = Modifier.matchParentSize()
        ) {
            val dashEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
            drawRoundRect(
                color = Color.Transparent,
                cornerRadius = CornerRadius(12.dp.toPx()),
                size = size
            )
            drawRoundRect(
                color = Slate300,
                cornerRadius = CornerRadius(12.dp.toPx()),
                size = size,
                style = Stroke(width = 2.dp.toPx(), pathEffect = dashEffect)
            )
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                tint = Slate950,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun IconWithNumberBadge(
    number: Float,
    modifier: Modifier = Modifier,
    painter: Painter = painterResource(id = R.drawable.fire_icon)
) {
    Box(
        modifier = modifier
            .height(17.dp)
            .wrapContentWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        Row{
            Icon(
                painter = painter,
                contentDescription = "Calories",
                tint = Slate400
            )
            Text(
                modifier = Modifier.padding(start = 6.dp),
                text = if (number > 10) {
                    number.toInt().toString()
                } else {
                    String.format("%.2f", number)
                },
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp, color = Slate400)
            )
        }
    }
}

 @Composable
 fun OneMealLazyRowItem(
     recipeTitle: String,
     recipeId: Long,
     mealPlanDayId:Long,
     recipesIdsWithQuantity: RecipeQuantity,
     navController: NavController,
     mealPlanViewModel: MealPlanViewModel,
     isFirst:Boolean=false
 ) {

     val context = LocalContext.current
     var showItemOptionsDialog by remember { mutableStateOf(false) }
     var showScaleDialog by remember { mutableStateOf(false) }

     var uri by remember { mutableStateOf<Uri?>(null) }
     val coroutineScope = rememberCoroutineScope()
     val recipeCaloriesMap by mealPlanViewModel.recipeCaloriesMap.collectAsState() //TODO soll ich sie übergeben?
     var textLineCount by remember { mutableStateOf(1) }

     LaunchedEffect(recipeId) {
         coroutineScope.launch {
             uri = getUriOrDefaultMealUri(recipeId, mealPlanViewModel)
         }
     }

     val caloriesPerRecipe = recipeCaloriesMap[recipeId] ?: 0f
     val totalCalories = caloriesPerRecipe * recipesIdsWithQuantity.quantity

     Card(
         modifier = Modifier
             .padding(start = if (isFirst) 12.dp else 4.dp, end = 4.dp, top = 4.dp)
             .width(182.dp)
             .height(276.5.dp)
             .clickable { showItemOptionsDialog = true }
             .clip(RoundedCornerShape(12.dp))
             .background(Color.White),
         elevation = CardDefaults.cardElevation(defaultElevation = GLOBAL_CARD_ELEVATION.dp),
         shape = RoundedCornerShape(12.dp)
     ) {
         Column(
             modifier = Modifier
                 .fillMaxSize()
                 .padding(0.dp)
                 .background(Color.White),
             verticalArrangement = Arrangement.Center,
             horizontalAlignment = Alignment.Start
         ) {

             if(uri == Uri.parse("keinBild")){
                 Image(
                     painter = painterResource(id = R.drawable.apfelkarottestableassistand),
                     contentDescription = "Dummy Image",
                     modifier = Modifier
                         .fillMaxWidth()
                         .aspectRatio(1f)
                         .padding(if (textLineCount > 1) 0.dp else 4.dp)
                         .clip(RoundedCornerShape(8.dp)),
                     contentScale = ContentScale.Crop
                 )
             }


            else if(uri != null) {
                AsyncImage(
                    model = uri,
                    contentDescription = recipeTitle,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(182.dp)
                        .height(195.5.dp)
                        .padding(horizontal = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }
            else{
               MyCircularProgressIndicator()
            }

             Text(
                 modifier = Modifier
                     .align(Alignment.Start)
                     .padding(
                         top = 6.dp,
                         start = 9.dp
                     )
                     .fillMaxWidth(),
                 text = recipeTitle,
                 style = MaterialTheme.typography.labelLarge,
                 textAlign = TextAlign.Start,
                 maxLines = 2,
                 overflow = TextOverflow.Ellipsis,
                 onTextLayout = { textLayoutResult ->
                     textLineCount = textLayoutResult.lineCount
                 }
             )
             Row(
                 modifier = Modifier
                     .fillMaxWidth()
                     .padding(if (textLineCount > 1) 6.dp else 12.dp,)
             ){
                 IconWithNumberBadge(
                     number = totalCalories,
                     modifier = Modifier
                         .padding(start = 3.dp, end = 3.dp)
                 )

                 IconWithNumberBadge(
                     number = recipesIdsWithQuantity.quantity,
                     modifier = Modifier
                         .padding(start = 16.dp, end = 3.dp),
                     painter = painterResource(id = R.drawable.scale_icon)
                 )
             }
         }
     }
     if (showItemOptionsDialog) {
         MealPlanItemOptionsAlertDialog(
             onDismiss = { showItemOptionsDialog = false },
             firstOnClick = {
                 showItemOptionsDialog = false
                 navController.navigate(Screen.ReplaceMealPlanRecipeScreen(context).passIds(recipeId,mealPlanDayId))
             },
             secondOnClick = {
                 showItemOptionsDialog = false
                 mealPlanViewModel.deleteMealFromMealplanDay(mealPlanDayId,recipeId)
             },
             thirdOnClick = {
                 showItemOptionsDialog = false
                 showScaleDialog = true
             },
             fourthOnClick = {
                 showItemOptionsDialog = false
                 navController.navigate(Screen.RecipeViewOnlyScreen(context).passIds(recipeId,mealPlanDayId))
             }
         )
     }
     if(showScaleDialog){
         CounterAlertDialog(
             onDismissRequest = { showScaleDialog = false },
             onConfirm = {sliderValue->
                 mealPlanViewModel.updateMealQuantity(mealPlanDayId,recipeId,sliderValue)
                 showScaleDialog = false
             },
             currentScaleValue = recipesIdsWithQuantity.quantity
         )
     }
 }

@Preview
@Composable
fun OneMealLazyRowItemPreview() {
    val navController = rememberNavController()
    val mealPlanViewModel:MealPlanViewModel = hiltViewModel()

    OneMealLazyRowItem(
        recipeTitle = "Test Rezept",
        recipeId = 1L,
        mealPlanDayId = 1L,
        recipesIdsWithQuantity = RecipeQuantity(1L, 2f),
        navController = navController,
        mealPlanViewModel = mealPlanViewModel,
        isFirst = true
    )
}

suspend fun getUriOrDefaultMealUri(recipeId: Long, mealPlanViewModel: MealPlanViewModel): Uri? {
    val uri =  mealPlanViewModel.getRecipeUri(recipeId)
    if(uri==null){
        return (Uri.parse("keinBild"))
    }
    else return uri
}

@Composable
fun GenerateMealPlanAlertDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        containerColor = Slate300,
        onDismissRequest = onDismiss,
        title = {
            Text(
                style  = MaterialTheme.typography.titleLarge,
                text = stringResource(R.string.confirm_regeneration)
            ) },
        text = { Text(
            style = MaterialTheme.typography.titleMedium,
            text = stringResource(R.string.m_chtest_du_wirklich_einen_neuen_essensplan_erstellen)) },
        confirmButton = {
            TextButton(onClick = {
                onConfirm()
                onDismiss()
            }) {
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

@Composable
fun CounterAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (Float) -> Unit,
    currentScaleValue:Float
) {
    // State to manage the slider value
    val sliderValue = remember { mutableStateOf(currentScaleValue) }

    AlertDialog(
        containerColor = Slate200,
        onDismissRequest = onDismissRequest,
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.adjust_quantity),
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp)
                    )
            }
                },
        text = {
            QuarterwiseCounter(
                value = sliderValue.value,
                onValueChange = {newValue ->
                    sliderValue.value = newValue
                }
            )
        },
        confirmButton = {
            Row{
                CustomButton(
                    modifier = Modifier
                        .height(50.dp)
                        .weight(1f),
                    text = stringResource(R.string.cancel),
                    onClick ={ onDismissRequest() }
                )

                Spacer(modifier = Modifier.width(16.dp))

                CustomButton(
                    modifier = Modifier
                        .height(50.dp)
                        .weight(1f),
                    buttonColor = Lime600,
                    textColor = Color.White,
                    borderColor = Slate950,
                    text = stringResource(R.string.confirm),
                    onClick ={ onConfirm(sliderValue.value) }
                )
            }
        }
    )
}
@Composable
fun QuarterwiseCounter(
    value:Float = 1f,
    minValue: Float = 0.25f,
    maxValue: Float = 99f,
    onValueChange: (Float)->Unit
) {
    var counterValue by remember { mutableStateOf(value) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        FloatingActionButton(
            modifier = Modifier.size(44.dp),
            shape = CircleShape,
            onClick = {  if (counterValue > minValue){
                counterValue -= 0.25f
                onValueChange(counterValue)
            }
            },
            containerColor = Color.White,
            contentColor = Slate950

        ) {
            Icon(
                painterResource(id = R.drawable.baseline_remove_24),
                contentDescription = "Decrement",
                tint = Slate950
            )
        }

        Text(
            text = "%.2f".format(counterValue),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .width(50.dp),
            textAlign = TextAlign.Center
        )

        FloatingActionButton(
            modifier = Modifier.size(44.dp),
            shape = CircleShape,
            containerColor = Color.White,
            contentColor = Slate950,
            onClick = {
                if (counterValue < maxValue){
                    counterValue += 0.25f
                    onValueChange(counterValue)
                }
            }) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Increment",
                tint = Slate950
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealPlanItemOptionsAlertDialog(
    onDismiss: () -> Unit,
    firstOnClick:()->Unit,
    secondOnClick:()->Unit,
    thirdOnClick: ()->Unit,
    fourthOnClick: ()->Unit
) {
    BasicAlertDialog(
        onDismissRequest = onDismiss,
        content = {
            Box(modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(Slate200)
            ){
                Column {
                    CustomFullWidthButton(
                        text = stringResource(R.string.replace),
                        onClick = { firstOnClick() },
                        verticalPadding = 12.dp
                    )
                    CustomFullWidthButton(
                        text = stringResource(R.string.delete),
                        onClick = { secondOnClick() },
                        verticalPadding = 8.dp
                    )
                    CustomFullWidthButton(
                        buttonColor = Lime600,
                        textColor = Color.White,
                        borderColor = Slate950,
                        text = stringResource(R.string.scale_quantity),
                        onClick = { thirdOnClick() },
                        verticalPadding = 8.dp
                    )
                    CustomFullWidthButton(
                        buttonColor = Lime600,
                        textColor = Color.White,
                        borderColor = Slate950,
                        text = stringResource(R.string.show_recipe),
                        onClick = { fourthOnClick() },
                        verticalPadding = 12.dp
                    )
                }
            }
        },
        modifier = Modifier
            .padding(16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
    )
}