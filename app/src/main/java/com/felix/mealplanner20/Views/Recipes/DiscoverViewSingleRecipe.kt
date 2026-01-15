package com.felix.mealplanner20.Views.Recipes

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.felix.mealplanner20.Meals.Data.IngredientWithRecipe
import com.felix.mealplanner20.Meals.Data.Recipe
import com.felix.mealplanner20.Meals.Data.RecipeDescription
import com.felix.mealplanner20.R
import com.felix.mealplanner20.ViewModels.DiscoverRecipesViewModel
import com.felix.mealplanner20.ViewModels.MainViewModel
import com.felix.mealplanner20.Views.Components.MyCircularProgressIndicator
import com.felix.mealplanner20.Views.Components.CustomFullWidthButton
import com.felix.mealplanner20.caching.ImageUrlBuilder
import com.felix.mealplanner20.di.BASE_URL
import com.felix.mealplanner20.ui.theme.Lime600
import com.felix.mealplanner20.ui.theme.Slate200
import com.felix.mealplanner20.ui.theme.Slate500
import com.felix.mealplanner20.use_cases.DownloadRecipeResult
import java.io.ByteArrayOutputStream
import java.util.Locale

@Composable
fun DiscoverViewSingleRecipe(
    recipeId:Long,
    discoverRecipesViewModel: DiscoverRecipesViewModel,
    mainViewModel: MainViewModel
) {
    val context = LocalContext.current

    val ingredients by discoverRecipesViewModel.ingredientWithRecipes.collectAsState(emptyList())
    val singleRecipeIsLoading by discoverRecipesViewModel.singleRecipeIsLoading.collectAsState()
    val recipeSteps by discoverRecipesViewModel.recipeDescriptionSteps.collectAsState(emptyList())
    val curRecipe by discoverRecipesViewModel.currentRecipe.collectAsState()

    val showOriginalTitle by discoverRecipesViewModel.showOriginalTitle.collectAsState(false)

    val ingredientsForSingleRecipe = ingredients.filter { it.recipeId == recipeId }
    val stepsForSingleRecipe = recipeSteps?.filter { it.recipeId == recipeId.toInt() }

    LaunchedEffect(recipeId) {
        discoverRecipesViewModel.loadSingleRecipe(recipeId)
    }
    LaunchedEffect(discoverRecipesViewModel) {
        discoverRecipesViewModel.downloadRecipeFlow.collect { result ->
            when(result) {
                is DownloadRecipeResult.Loading -> {
                    Toast.makeText(context,
                        context.getString(R.string.downloading_recipe), Toast.LENGTH_SHORT).show()
                }
                is DownloadRecipeResult.Success -> {
                    Toast.makeText(context,
                        context.getString(R.string.recipe_downloaded), Toast.LENGTH_SHORT).show()
                }
                is DownloadRecipeResult.RecipeNotFound -> {
                    Toast.makeText(context,
                        context.getString(R.string.recipe_not_found), Toast.LENGTH_SHORT).show()
                }
                is DownloadRecipeResult.ImageDownloadFailed -> {
                    Toast.makeText(context,
                        context.getString(R.string.failed_to_download_recipe_image), Toast.LENGTH_SHORT).show()
                }
                is DownloadRecipeResult.StepImagesDownloadFailed -> {
                    Toast.makeText(context,
                        context.getString(R.string.failed_to_download_step_images), Toast.LENGTH_SHORT).show()
                }
                is DownloadRecipeResult.UnknownError -> {
                    Toast.makeText(context,
                        context.getString(R.string.unknown_error, result.throwable?.message), Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }
    }

    if (singleRecipeIsLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
                .background(color = MaterialTheme.colorScheme.background)
        ) {
            MyCircularProgressIndicator()
        }
    }
    else{

    curRecipe?.let { filteredRecipe1 ->
        val isGerman = Locale.getDefault().language == "de"
        val localizedTitle = if(showOriginalTitle){filteredRecipe1.title}else {if (isGerman) filteredRecipe1.germanTitle else filteredRecipe1.englishTitle}
        mainViewModel.setCurrentTopAppBarTitle(localizedTitle)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            Column {
                Row {
                    Box {
                        Column {

                            val imageUrl = remember(curRecipe?.imgUri) {
                                val key = curRecipe?.imgUri?.toString()?.trim().orEmpty()
                                if (key.isEmpty() || key.equals("null", true)) null
                                else ImageUrlBuilder.recipe(BASE_URL, key, verify = false, expiresSeconds = 900)
                            }

                            if (imageUrl != null) {
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(imageUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "recipe image",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(256.dp),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Text(text = "bild ist null")
                            }


                                Text(
                                    modifier = Modifier
                                        .padding(start = 16.dp, end = 16.dp,top = 16.dp, bottom = 8.dp)
                                        .align(Alignment.CenterHorizontally),
                                    text = localizedTitle,//if (isGerman)filteredRecipe1.germanTitle else filteredRecipe1.englishTitle,
                                    style = MaterialTheme.typography.labelMedium.copy(fontSize = 32.sp, fontWeight = FontWeight.Normal)
                                )

                            Text(
                                modifier = Modifier.padding(end = 12.dp)
                                    .align(Alignment.CenterHorizontally),

                                text = if (isGerman) {
                                    "von ${filteredRecipe1.createdBy}"
                                } else {
                                    "by ${filteredRecipe1.createdBy}"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate500
                            )



                            CustomFullWidthButton(
                                text = stringResource(R.string.download_recipe),
                                onClick = {
                                    discoverRecipesViewModel.downloadRecipe(context = context)
                                },
                                buttonColor = Lime600,
                                textColor = Color.White
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .wrapContentSize()
                        .heightIn(max = 4000.dp)
                ) {
                    Column {
                        val portionsText = if (filteredRecipe1.servings == 1f) {
                            stringResource(R.string.ingredients_for_1_serving)
                        } else {
                            stringResource(R.string.ingredients_for_X_servings, filteredRecipe1.servings)
                        }
                        IngredientsBlock(portionsText,ingredientsForSingleRecipe,filteredRecipe1)

                        stepsForSingleRecipe?.let { steps ->
                            DescriptionBlock(steps)
                        }
                    }
                }
            }
        }
    }
    }
}

@Composable
fun RecipeDescriptionBlock(recipeSteps: List<RecipeDescription?>){

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
        ){
            Text(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterVertically),
                text = stringResource(R.string.description),
                style = MaterialTheme.typography.bodyMedium
            )
        }
        recipeSteps.forEach{ step ->
            step?.let {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(Color.White)
                        .border(BorderStroke(1.dp, Slate200))
                ) {
                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = step.text,
                        style = MaterialTheme.typography.bodyLarge

                    )
                }
            }
        }
    }
}

@Composable
fun IngredientsBlock(headline:String,ingredients: List<IngredientWithRecipe>,recipe: Recipe){
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
        ){
            Text(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterVertically),
                text = headline,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(Color.White)
                .border(BorderStroke(1.dp, Slate200))
        ) {
            itemsIndexed(ingredients) { index,item ->
                IngredientWithQuantityViewOnlyItem(
                    item,
                    1f //wir skalieren hier gar nix. Das Rezept wird in Orginalmenge angezeigt, es steht ja dabei wie viele Portionen das sind
                )
            }
        }
    }
}

@Composable
fun DescriptionBlock(
    steps: List<RecipeDescription>
) {

    val isGerman = Locale.getDefault().language == "de"
   /* val columlength = steps.count()
    LazyColumn(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Slate200)
    ) {
        item{ Spacer(modifier = Modifier.height(30.dp)) }
        items(
            items = steps,
            key = { it.id }
        ) { step ->

            val imageBytes = stepPictures.find { it.stepId == step.id }?.image

            RemoteRecipeDescriptionStep(
                stepNumber = step.stepNr,
                stepText = step.text,
                imageBytes = imageBytes
            )
        }
        item{ Spacer(modifier = Modifier.height(30.dp)) }
    }*/

    Column {
        Spacer(modifier = Modifier.height(30.dp))
        steps.forEach{ step ->
            val stepImageUrl = step.imgUri
                ?.trim()
                ?.takeIf { it.isNotEmpty() && !it.equals("null", true) }
                ?.let { ImageUrlBuilder.description(BASE_URL, it, verify = false, expiresSeconds = 900) }


            RemoteRecipeDescriptionStep(
                stepNumber = step.stepNr,
                stepText = if (isGerman) step.germanText else step.englishText,
                imageUrl = stepImageUrl
            )
        }
        Spacer(modifier = Modifier.height(30.dp))
    }


    /*val context = LocalContext.current
    val drawable = painterResource(R.drawable.add_icon)



        val bitmap = drawableToBitmap(context, R.drawable.add_icon)
        val bytearrray = bitmapToByteArray(bitmap)

    Column {
        RemoteRecipeDescriptionStep(
            stepNumber = 1,
            stepText = "Dies ist ein Testschritt mit Bild",
            imageBytes =  bytearrray
        )
        RemoteRecipeDescriptionStep(
            stepNumber =2,
            stepText = "Zwiebeln scheiden. Wasser zum kochen bringen.",
            imageBytes =  bytearrray
        )
    }*/

}
fun drawableToBitmap(context: Context, drawableResId: Int): Bitmap {
    val drawable = ContextCompat.getDrawable(context, drawableResId)!!
    val bitmap = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}

fun bitmapToByteArray(bitmap: Bitmap, format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG): ByteArray {
    val stream = ByteArrayOutputStream()
    bitmap.compress(format, 100, stream)
    return stream.toByteArray()
}

@Composable
fun RemoteRecipeDescriptionStep(
    stepNumber: Int,
    stepText: String,
    imageUrl: String?
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
                modifier = if (imageUrl != null) {
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                } else {
                    Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                }
            ) {
                if (imageUrl != null) {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Step Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.LightGray)
                    ) {
                        when (painter.state) {
                            is AsyncImagePainter.State.Loading -> {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator()
                                }
                            }
                            else -> SubcomposeAsyncImageContent()
                        }
                    }
                }
            }

            Divider(
                color = Color.White,
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth()
            )

            // Optional: Kreis anzeigen
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

            Spacer(modifier = Modifier.height(30.dp)) // offset ausgleichen

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