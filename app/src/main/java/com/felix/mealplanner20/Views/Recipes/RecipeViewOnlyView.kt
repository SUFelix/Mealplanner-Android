package com.felix.mealplanner20.Views.Recipes

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.felix.mealplanner20.Meals.Data.EMPTY_STRING
import com.felix.mealplanner20.Meals.Data.IngredientWithRecipe
import com.felix.mealplanner20.Meals.Data.helpers.NAVIGATE_BACK_CONTENT_DESCRIPTION
import com.felix.mealplanner20.R
import com.felix.mealplanner20.ViewModels.AddEditRecipeViewModel
import com.felix.mealplanner20.ViewModels.IngredientViewModel
import com.felix.mealplanner20.ViewModels.MainViewModel
import com.felix.mealplanner20.ViewModels.MealPlanViewModel
import com.felix.mealplanner20.ui.theme.Lime600
import com.felix.mealplanner20.ui.theme.Slate100
import com.felix.mealplanner20.ui.theme.Slate200
import com.felix.mealplanner20.ui.theme.Slate500
import java.util.Locale

@Composable
fun RecipeViewOnlyView(
    recipeId: Long,
    mealPlanDayId:Long?,
    recipeViewModel: AddEditRecipeViewModel,
    mealPlanViewModel: MealPlanViewModel,
    navController: NavController,
    mainViewModel: MainViewModel
){
    mainViewModel.setCurrentTopAppBarTitle(recipeViewModel.recipeName.value)
    var recipeQuantity: Float by remember {
        mutableStateOf(1f)
    }
    LaunchedEffect(recipeId) {
        mealPlanDayId?.let {
            recipeQuantity = mealPlanViewModel.getQuantityForRecipeInMealPlan(mealPlanDayId, recipeId)?:1f
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Slate200)
            .heightIn(max = 4000.dp)
            .verticalScroll(rememberScrollState())
    ){
        Column {
            BigImageWithBackArrow(
                imgUri = recipeViewModel.imgUri.value,
                onArrowClick = {navController.popBackStack()}
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clip(
                        RoundedCornerShape(
                            topStart = 12.dp,  // Oben links abgerundet
                            topEnd = 12.dp,    // Oben rechts abgerundet
                            bottomStart = 0.dp, // Unten eckig
                            bottomEnd = 0.dp   // Unten eckig
                        )
                    ),
                horizontalArrangement = Arrangement.Center
            ){
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp,top = 16.dp, bottom = 8.dp),
                           // .align(Alignment.CenterVertically),
                        text = recipeViewModel.recipeName.value,
                        style = MaterialTheme.typography.labelMedium.copy(fontSize = 32.sp, fontWeight = FontWeight.Normal)
                    )
                    GreenDotsRow(5)
                }

            }
            CookModeStyledIngredientsBlock(recipeQuantity,recipeViewModel)

            CookModeDescriptionBlock(recipeViewModel)
        }
    }
}


@Composable
fun GreenDotsRow(i:Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        repeat(i) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    //.background(Lime500, shape = CircleShape)
            ){
                androidx.compose.material.Icon(painter = painterResource(R.drawable.frame), tint = Lime600, contentDescription = null)
            }
           // I(painter = )
        }
    }
}



@Composable
fun CookModeStyledIngredientsBlock(
    recipeQuantity: Float,
    recipeViewModel: AddEditRecipeViewModel
) {
    val servingsText = if (recipeQuantity == 1f) {
        stringResource(R.string.ingredients_for_1_serving)
    } else {
        stringResource(R.string.ingredients_for_X_servings, recipeQuantity)
    }

    val recipeServings  =recipeViewModel.servings

    Column(
        modifier = Modifier
            .padding(16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 12.dp,
                        topEnd = 12.dp,
                        bottomStart = 0.dp,
                        bottomEnd = 0.dp
                    )
                )
                .background(Color.White)
        ) {
            Text(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterVertically),
                text = servingsText,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .heightIn(max = 4000.dp)
                .background(Color.White)
                .border(BorderStroke(1.dp, Slate200))
        ) {
            itemsIndexed(recipeViewModel.recipeIngredients) { index, ingredient ->
                IngredientWithQuantityViewOnlyItem(
                    ingredientWithRecipe = ingredient,
                    recipeQuantity = (recipeQuantity / recipeServings.value)
                )
            }
        }
    }
}

@Composable
fun CookModeDescriptionBlock(
    recipeViewModel: AddEditRecipeViewModel
){
    val descriptionSteps = recipeViewModel.recipeDescriptionSteps.value

    Column(
        modifier = Modifier
            .padding(top = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Slate200)
            .heightIn(max = 4000.dp)

    ) {
        descriptionSteps?.forEach{ step ->
                RecipeDescriptionViewOnlyStep(step.stepNr,step.text,step.imgUri)
        }
        Spacer(modifier = Modifier.height(30.dp))
    }


}
@Composable
fun NonEditableIngredientWithQuantityListItem(
    ingredientWithRecipe: IngredientWithRecipe?,
    servings: Float = 1f,
    isLast: Boolean = false
){
    ingredientWithRecipe?.let{ IngredientWithRecipe ->
        val ingredientViewModel: IngredientViewModel = hiltViewModel()
        val ingredientFlowState = mutableStateOf(ingredientViewModel.getIngredientById(IngredientWithRecipe.ingredientId))
        val ingredientName = remember { mutableStateOf("") }

        LaunchedEffect(ingredientFlowState) {
            ingredientFlowState.value.collect { ingredient ->
                ingredientName.value = ingredient.germanName
            }
        }
        val cornerShape = RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 0.dp,
            bottomStart =if(isLast) 12.dp else 0.dp,
            bottomEnd = if (isLast)12.dp else 0.dp
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = cornerShape,
            colors = CardDefaults.cardColors(containerColor = Slate200.copy(alpha = 0.5f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            border = BorderStroke(1.dp, Color.White)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().padding( 16.dp)
            ){
                Box{
                    Text(
                        text = ingredientName.value,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 18.sp),
                        color = Slate500
                    )
                }
                Box{
                    NonEditableQuantityPartInListView(ingredientWithRecipe,servings)
                }
            }
        }
    }
}
@Composable
fun NonEditableQuantityPartInListView(
    ingredientWithRecipe: IngredientWithRecipe,
    servings: Float = 1f,
) {
    val adjustedQuantity =  ingredientWithRecipe.ingredientQuantity * servings
    val formatingStyle = if(dontShowDecimalPlace(ingredientWithRecipe.ingredientQuantity)) "%.0f" else "%.1f"
    val text = String.format(formatingStyle, adjustedQuantity)

    Text(
        text = text + " "+ingredientWithRecipe.unitOfMeasure.toUOMshoppingListshortcut(LocalContext.current),
        style = MaterialTheme.typography.labelSmall.copy(fontSize = 18.sp) ,
        modifier = Modifier.clip(RoundedCornerShape(8.dp))
    )
}
private fun dontShowDecimalPlace(numberToCheck: Float):Boolean {
    if (ersteNachkommastelleIstNull(numberToCheck)) return true
    val CUTOFF_DECIMAL_ABOVE_VALUE = 50
    if(numberToCheck>CUTOFF_DECIMAL_ABOVE_VALUE) return true
    else return false
}
private fun ersteNachkommastelleIstNull(zahl: Float): Boolean {
    val ganzzahligerAnteil = zahl.toInt()
    val ersteNachkommastelle = ((zahl - ganzzahligerAnteil) * 10).toInt()
    return ersteNachkommastelle == 0
}
@Composable
fun IngredientWithQuantityViewOnlyItem(
    ingredientWithRecipe: IngredientWithRecipe?,
    recipeQuantity: Float
){

    val isGerman = Locale.getDefault().language == "de"
    ingredientWithRecipe?.let{ IngredientWithRecipe ->
        val ingredientViewModel: IngredientViewModel = hiltViewModel()
        val ingredientFlowState = mutableStateOf(ingredientViewModel.getIngredientById(IngredientWithRecipe.ingredientId))
        val ingredientName = remember { mutableStateOf(EMPTY_STRING) }

        LaunchedEffect(ingredientFlowState) {
            ingredientFlowState.value.collect { ingredient ->

                val localizedTitle:String = if (isGerman) ingredient.germanName else ingredient.englishName ?:ingredient.germanName

                ingredientName.value = localizedTitle
            }
        }
        Box(
            modifier = Modifier
                .border(BorderStroke(1.dp, Slate200))
        ){

            val adjustedQuantity = (ingredientWithRecipe.originalQuantity *recipeQuantity)
            val qtyText = if (adjustedQuantity % 1 == 0f) {
                adjustedQuantity.toInt().toString()
            } else {
                String.format("%.2f", adjustedQuantity)
            }
            val uomText  = ingredientWithRecipe.unitOfMeasure.toUOMshoppingListshortcut(LocalContext.current)

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ){
                Text(
                    text = ingredientName.value,
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate500,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 12.dp)
                )
                Text(
                    text = "$qtyText$uomText",
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}
@Composable
fun BigImageWithBackArrow(
    imgUri: Uri? = null,
    onArrowClick:()->Unit = {}
) {
    val fallbackDrawableId = R.drawable.baseline_fastfood_24

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        if (imgUri != null) {
            Image(
                painter = rememberAsyncImagePainter(model = imgUri),
                contentDescription = "Selectable image",
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )
        } else {
            Image(
                painter = painterResource(id = fallbackDrawableId),
                contentDescription = "Fallback image",
                modifier = Modifier
                    .fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        IconButton(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 42.dp)
                .clip(shape = CircleShape)
                .background(Slate100),
            onClick = {onArrowClick()},
            content = {
                Image(
                    painter =  painterResource(R.drawable.arrow_icon),
                    contentDescription = NAVIGATE_BACK_CONTENT_DESCRIPTION,
                    modifier = Modifier
                        .size(48.dp)
                        .graphicsLayer(scaleX = -1f)
                )
            }
        )
    }
}

@Composable
fun RecipeDescriptionViewOnlyStep(
    stepNumber: Int,
    stepText: String,
    imageUri: String?
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = if (imageUri != null) {
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                } else {
                    Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                }
            ) {
                if (imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(model = imageUri),
                        contentDescription = "Step Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.LightGray)
                    )
                }
            }
            Divider(
                color = Color.White, // Slate950
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth()
            )
            Box(
                modifier = Modifier
                    .offset(y = (-30).dp)
                    .size(60.dp)
                    .border(
                        width = 1.dp,
                        color = Color.White,
                        shape = CircleShape
                    )
                    .background(color = Lime600, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stepNumber.toString(),
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            Text(
                text = stepText,
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                    .fillMaxWidth(),
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 18.sp)
            )
        }
    }
}