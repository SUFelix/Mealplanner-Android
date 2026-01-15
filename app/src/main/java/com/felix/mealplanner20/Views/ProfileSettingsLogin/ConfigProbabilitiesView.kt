
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.felix.mealplanner20.Meals.Data.Recipe
import com.felix.mealplanner20.Meals.Data.helpers.Mealtype
import com.felix.mealplanner20.R
import com.felix.mealplanner20.ViewModels.RecipeCatalogViewModel
import com.felix.mealplanner20.Views.Components.MyCircularProgressIndicator
import com.felix.mealplanner20.Views.ScreenWidthInDp
import com.felix.mealplanner20.ui.theme.Lime600
import com.felix.mealplanner20.ui.theme.Slate200
import com.felix.mealplanner20.ui.theme.Slate300
import com.felix.mealplanner20.ui.theme.Slate500
import com.felix.mealplanner20.ui.theme.Slate950


@Composable
fun ConfigureMyRecipesProbabilitiesView(
    recipeCatalogViewModel: RecipeCatalogViewModel
) {
    val isLoading by recipeCatalogViewModel.isLoading.collectAsState()
    if (isLoading) {
            MyCircularProgressIndicator()
    }
    else{
        val myRecipes by recipeCatalogViewModel.getAllRecipes.collectAsState(initial = listOf())

        val meals = myRecipes.filter { it.isMeal }
        val breakfasts = myRecipes.filter { it.isBreakfast }
        val snacks = myRecipes.filter { it.isSnack }

        val mealWeights by recipeCatalogViewModel
            .observeWeightsByMealTypeAsMap(Mealtype.MEAL)
            .collectAsState(initial = emptyMap())
        val breakfastWeights by recipeCatalogViewModel
            .observeWeightsByMealTypeAsMap(Mealtype.BREAKFAST)
            .collectAsState(initial = emptyMap())
        val snackWeights by recipeCatalogViewModel
            .observeWeightsByMealTypeAsMap(Mealtype.SNACK)
            .collectAsState(initial = emptyMap())

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Slate200)
                .border(BorderStroke(1.dp,color = Slate300))
        ){

            LazyColumn(
                modifier = Modifier
                    .padding(16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color = Color.White)
            ) {

                if (meals.isNotEmpty()) {
                    item {
                        ConfigProbBlockHeadline(stringResource(R.string.meals))
                    }
                    itemsIndexed(meals) { index,recipe ->
                        val isLast = index == meals.lastIndex

                        val currentWeight = mealWeights[recipe.id] ?: 0.5f
                        NewRecipeSliderItem(
                            recipe = recipe,
                            currentWeight = currentWeight,
                            onWeightChange = { newWeight ->
                                recipeCatalogViewModel.updateWeight(recipe.id, Mealtype.MEAL, newWeight)
                            },
                            isLast = isLast
                        )
                    }
                    item { Spacer(modifier = Modifier.height(16.dp).fillMaxWidth().background(color = Slate200)) }
                }

                // Breakfasts
                if (breakfasts.isNotEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .background(color = Slate200)
                        ) {
                            ConfigProbBlockHeadline(text = stringResource(R.string.breakfasts))
                        }
                    }
                    itemsIndexed(breakfasts, key = { _, recipe -> recipe.id }) { index, recipe ->
                        val isLast = index == breakfasts.lastIndex
                        val currentWeight = breakfastWeights[recipe.id] ?: 0.5f
                        NewRecipeSliderItem(
                            recipe = recipe,
                            currentWeight = currentWeight,
                            onWeightChange = { newWeight ->
                                recipeCatalogViewModel.updateWeight(recipe.id, Mealtype.BREAKFAST, newWeight)
                            },
                            isLast = isLast
                        )
                    }
                    item { Spacer(modifier = Modifier.height(16.dp).fillMaxWidth().background(color = Slate200)) }
                }

                if (snacks.isNotEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .background(color = Slate200)
                        ) {
                            ConfigProbBlockHeadline(text = stringResource(R.string.snacks))
                        }
                    }
                    itemsIndexed(snacks) { index, recipe ->
                        val isLast = index == snacks.lastIndex
                        val currentWeight = snackWeights[recipe.id] ?: 0.5f
                        NewRecipeSliderItem(
                            recipe = recipe,
                            currentWeight = currentWeight,
                            onWeightChange = { newWeight ->
                                recipeCatalogViewModel.updateWeight(recipe.id, Mealtype.SNACK, newWeight)
                            },
                            isLast = isLast
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ConfigProbBlockHeadline(text:String) {
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
            .background(color = Color.White)
    ) {
        Text(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .align(Alignment.CenterVertically),
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun NewRecipeSliderItem(
    recipe: Recipe,
    currentWeight: Float,
    onWeightChange: (Float) -> Unit,
    valueRange: IntRange = 0..100,
    isLast: Boolean = false
) {

    var sliderValue by rememberSaveable(recipe.id) { mutableStateOf(currentWeight) }
    LaunchedEffect(recipe.id, currentWeight) {
        sliderValue = currentWeight
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
            .height(84.dp),
        shape = cornerShape,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Slate200)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = recipe.title,
                style = MaterialTheme.typography.bodySmall,
                color = Slate500
            )
            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row{
                    Text(
                        text = "${(sliderValue * 100).toInt()}%",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.alignByBaseline()
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.probability),
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.alignByBaseline()
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                BoxWithConstraints(
                    modifier = Modifier.width((ScreenWidthInDp() / 2.5f).dp)
                ) {
                    val trackHeight = 6.dp
                    val thumbSize = 18.dp

                    // echte verfügbare Breite in dp
                    val widthDp = maxWidth
                    val thumbRadius = thumbSize / 2

                    // Thumb darf nicht über den Rand hinaus
                    val thumbX = (sliderValue * (widthDp - thumbSize))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterEnd)
                            .height(trackHeight)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Slate950)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth(1f - sliderValue)
                            .align(Alignment.CenterEnd)
                            .height(trackHeight)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Slate200)
                    )

                    Box(
                        modifier = Modifier
                            .offset(x = thumbX, y = 12.dp)   // y wie vorher
                            .size(thumbSize)
                            .clip(CircleShape)
                            .border(2.dp, Color.White, CircleShape)
                            .background(Lime600)
                    )

                    Slider(
                        value = sliderValue,
                        onValueChange = { sliderValue = it },
                        onValueChangeFinished = { onWeightChange(sliderValue) },
                        valueRange = 0f..1f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .alpha(0f),
                        colors = SliderDefaults.colors(
                            thumbColor = Color.Transparent,
                            activeTrackColor = Color.Transparent,
                            inactiveTrackColor = Color.Transparent
                        )
                    )
                }
            }
        }
    }
}


