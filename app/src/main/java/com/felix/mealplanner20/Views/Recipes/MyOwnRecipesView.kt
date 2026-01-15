package com.felix.mealplanner20.Views.Recipes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.felix.mealplanner20.Meals.Data.Recipe
import com.felix.mealplanner20.R
import com.felix.mealplanner20.Screen
import com.felix.mealplanner20.ViewModels.MainViewModel
import com.felix.mealplanner20.ViewModels.MyOwnRecipesViewModel
import com.felix.mealplanner20.Views.Components.MyCircularProgressIndicator
import com.felix.mealplanner20.ui.theme.Lime600

@Composable
fun MyOwnRecipesView(
    navController: NavController,
    myOwnRecipesViewModel: MyOwnRecipesViewModel,
    mainViewModel: MainViewModel
)
{
    val isLoading by myOwnRecipesViewModel.isLoading.collectAsState()
    val myOwnRecipeList = myOwnRecipesViewModel.getAllRecipes.collectAsState(initial = listOf())
    val context = LocalContext.current

    if (isLoading) {
        MyCircularProgressIndicator()
    }
    else if (!isLoading && myOwnRecipeList.value.isEmpty()){
        Box{
            Column{


            Text(
                text = stringResource(R.string.no_recipes_info_text),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
                Spacer(
                    modifier = Modifier.height(24.dp)

                )
            FloatingActionButton(
                onClick = {
                    navController.navigate(Screen.AddUpdateRecipeScreen(context = context).passId(0L))
                },
                shape = CircleShape,
                modifier = Modifier.align(Alignment.CenterHorizontally)

            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
            }
        }

    }
    else{

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ){

        LazyVerticalGrid(
            columns = GridCells.Fixed(2), // Zwei Spalten für das Grid
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp, start = 16.dp,end = 16.dp), // Padding unten, damit die FAB sichtbar bleibt
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            content = {
                items(
                    items = myOwnRecipeList.value,
                    key = { it.id }){recipe->
                    ResponsiveImageCard(
                        recipe = recipe,
                        onClick = {
                            val id = recipe.id
                            navController.navigate(Screen.AddUpdateRecipeScreen(context).passId(id))
                            mainViewModel.setCurrentScreen(Screen.AddUpdateRecipeScreen(context))
                        }
                    )
                }
            }
        )
        Box(
            modifier = Modifier
                .padding(0.dp, 0.dp, 16.dp, 16.dp)
                .align(Alignment.BottomEnd)
        ) {
            Column {
                FloatingActionButton(
                    onClick = {
                        navController.navigate(
                            Screen.AddUpdateRecipeScreen(context = context).passId(0L)
                        )
                    },
                    shape = CircleShape,
                    containerColor = Lime600, // Hintergrundfarbe weiß
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Create")
                }
            }

        }
        }


}
}

@Composable
fun ResponsiveImageCard(recipe: Recipe, modifier: Modifier = Modifier,onClick: ()->Unit) {
    var textLineCount by remember { mutableStateOf(2) }
    BoxWithConstraints {
        val cardWidth = maxWidth * 1f // 80% der Bildschirmbreite
        val cardHeight = cardWidth * 1.264f // Höhe abhängig von der Breite


        Card(
            modifier = modifier
                .width(cardWidth)
                .height(cardHeight)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .clickable { onClick() },
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize().background(Color.White),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

               if (recipe.imgUri!=null) {
                    AsyncImage(
                        model = recipe.imgUri,
                        contentDescription = "img",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .padding(
                                top = 4.dp,
                                start = 4.dp,
                                end = 4.dp,
                                bottom = if (textLineCount > 1) 0.dp else 4.dp)
                            .clip(RoundedCornerShape(8.dp)))

                }
                else{
                   Image(
                       painter = painterResource(id = R.drawable.shake2240),
                       contentDescription = "Dummy Image",
                       modifier = Modifier
                           .fillMaxWidth()
                           .aspectRatio(1f)
                           .padding(if (textLineCount > 1) 0.dp else 4.dp)
                           .clip(RoundedCornerShape(8.dp)),
                       contentScale = ContentScale.Crop
                   )

                }
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier
                        .padding(
                            horizontal = 8.dp,
                            vertical =if (textLineCount > 1) 3.dp else 12.dp,
                            )
                        .align(AbsoluteAlignment.Left)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Left,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    onTextLayout = { textLayoutResult ->
                        textLineCount = textLayoutResult.lineCount
                    }
                )

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewResponsiveImageCardGrid() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE2E8F0)), // Slate-200 Hintergrund
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2), // Zwei Spalten
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(4) { index ->
                ResponsiveImageCard(
                    recipe = Recipe(title = "Dummyrezept"),
                    onClick = {}
                )
            }
        }
    }
}