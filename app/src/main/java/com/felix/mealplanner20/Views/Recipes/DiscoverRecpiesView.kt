package com.felix.mealplanner20.Views.Recipes

import android.annotation.SuppressLint
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.IntSize
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.felix.mealplanner20.Meals.Data.Recipe
import com.felix.mealplanner20.Meals.Data.helpers.Mealtype
import com.felix.mealplanner20.R
import com.felix.mealplanner20.Screen
import com.felix.mealplanner20.ViewModels.DiscoverRecipesViewModel
import com.felix.mealplanner20.ViewModels.SettingsViewModel
import com.felix.mealplanner20.Views.Components.MyCircularProgressIndicator
import com.felix.mealplanner20.Views.ScreenWidthInDp
import com.felix.mealplanner20.Views.StyledSearchBar
import com.felix.mealplanner20.caching.ImageUrlBuilder
import com.felix.mealplanner20.di.BASE_URL
import com.felix.mealplanner20.ui.theme.Slate200
import com.felix.mealplanner20.ui.theme.Slate300
import com.felix.mealplanner20.ui.theme.Slate500
import com.felix.mealplanner20.ui.theme.Slate950
import com.felix.mealplanner20.use_cases.CARBS_CALORIES
import com.felix.mealplanner20.use_cases.FAT_CALORIES
import com.felix.mealplanner20.use_cases.PROTEIN_CALORIES
import kotlinx.coroutines.flow.filter
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max

@SuppressLint("SuspiciousIndentation")
@Composable
fun DiscoverRecipesView(
    navController: NavController,
    discoverRecipesViewModel: DiscoverRecipesViewModel
)
{
    val isLoading by discoverRecipesViewModel.isLoading.collectAsState()
    val isLoadingAdditionalItems by discoverRecipesViewModel.isLoadingAdditionalItems.collectAsState()
    val recipes = discoverRecipesViewModel.recipes.collectAsState()
    val type by discoverRecipesViewModel.type.collectAsState(null)
    val context = LocalContext.current
    val isSearching by discoverRecipesViewModel.isSearching.collectAsState()
    val searchQuery by discoverRecipesViewModel.searchQuery.collectAsState()
    val listState = rememberLazyListState()
    val horizontalScrollState = rememberSaveable(saver = ScrollState.Saver) { ScrollState(0) }
    val showOriginalTitle by discoverRecipesViewModel.showOriginalTitle.collectAsState(false)

    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current




        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
        ){

            LaunchedEffect(listState, isLoadingAdditionalItems, isSearching, recipes.value.size) {
                snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
                    .filter { it != null && it >= recipes.value.size - 2 }
                    .collect {
                        discoverRecipesViewModel.loadAdditionalRecipes()
                    }
            }

            BackHandler(enabled = isSearching) {
                discoverRecipesViewModel.toggleSearchMode(false)
                keyboard?.hide()
            }

                Column {
                    Crossfade(targetState = isSearching, label = "search-toggle") { searching ->
                        if (searching) {
                            StyledSearchBar(
                                searchQuery = searchQuery,
                                onQueryChange = { discoverRecipesViewModel.setSearchQueryAndExecuteSearch(it) },
                                onXClick = {
                                    discoverRecipesViewModel.toggleSearchMode(false)
                                    keyboard?.hide()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .focusRequester(focusRequester),
                                placeholder = stringResource(R.string.search_recipes)
                            )
                            LaunchedEffect(Unit) {
                                focusRequester.requestFocus()
                            }
                        } else {
                            val mealTypes = listOf(
                                null to stringResource(R.string.all),
                                Mealtype.MEAL to stringResource(R.string.meal),
                                Mealtype.BREAKFAST to stringResource(R.string.breakfast),
                                Mealtype.DESSERT to stringResource(R.string.dessert),
                                Mealtype.SNACK to stringResource(R.string.snack),
                                Mealtype.BEVERAGE to stringResource(R.string.beverage),
                            )

                            Row(modifier = Modifier.horizontalScroll(horizontalScrollState)) {

                                SearchToggleButton(
                                    isActive = isSearching,
                                    onClick = {
                                        if (isSearching) {
                                            discoverRecipesViewModel.toggleSearchMode(false)
                                        } else {
                                            discoverRecipesViewModel.toggleSearchMode(true)
                                        }
                                    }
                                )
                               // Spacer(Modifier.width(8.dp)) oder 2 bzw 4dp?

                                mealTypes.forEach { (typeItem, label) ->
                                    NewMealTypeButton(
                                        mealType = typeItem,
                                        selectedMealType = type,
                                        onClick = {
                                            discoverRecipesViewModel.setMealTypeAndReload(
                                                typeItem
                                            )
                                        },
                                        label = label
                                    )
                                }
                            }

                        }
                    }
                }
            if (isSearching) {
                LaunchedEffect(searchQuery) {
                    // Optional: As-you-type starten (Debounce); alternativ Button/IME-Action:
                    // delay(300); if (searchQuery.length >= 2) discoverRecipesViewModel.executeSearch(reset = true)
                }
            }

            if (isLoading) {
                MyCircularProgressIndicator()
            }
            else{
                recipes.let {
                    LazyColumn(state = listState) {
                        items(
                            items = it.value,
                            key = { recipe -> "${recipe.id}" }
                        ){recipe->

                            BigRecipeCardItem(
                                recipe = recipe,
                                showOriginalTitle = showOriginalTitle,
                                onClick = {
                                    navController.navigate(Screen.DiscoverRecipesSingleViewScreen(context = context).passId(recipe.id))
                                }
                            )
                        }

                        item {
                            if (isLoadingAdditionalItems) {
                                CircularProgressIndicator(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp))
                            }
                        }
                    }
                }

            }
        }

}

@Composable
fun NewMealTypeButton(mealType: Mealtype?,
                      selectedMealType: Mealtype?,
                      onClick: (Mealtype?) -> Unit,
                      label: String) {

    val color1:Color = if (selectedMealType == mealType) Slate950 else Slate200
    val color2:Color = if (selectedMealType == mealType) Color.White else Slate950
    val borderStroke: Dp = if (selectedMealType == mealType) 0.dp else 1.dp

    Button(
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier
            .padding(8.dp)
            .wrapContentWidth()
            .height(42.dp)
            .clip(RoundedCornerShape(4.dp)),
        border = BorderStroke(borderStroke, Slate300) ,
        colors = ButtonDefaults.buttonColors(containerColor = color1),
        onClick = { onClick(mealType) }
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = color2
        )
    }
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun BigRecipeCardItem(
    recipe: Recipe,
    showOriginalTitle:Boolean,
    onClick: () -> Unit
) {

    val parentImageCoords = remember { mutableStateOf<LayoutCoordinates?>(null) }
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    val imageUrl = remember(recipe.imgUri) {
        val key = recipe.imgUri.toString()
        if (key.isEmpty()) null else ImageUrlBuilder.recipe(BASE_URL, key, verify = false, expiresSeconds = 900)
    }
    Log.d("DEBUG URL","imageUrl: $imageUrl")
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .height(307.5.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .height(251.5.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Gray) // Platzhalter für Bild

        ) {

            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    //.data("http://192.168.2.216:8080/images/recipe/83fd8410-df95-4dd5-9d55-ae2efb6fd8b1..jpg")
                    .crossfade(true)

                    .listener(
                        onError = { req, res ->
                            Log.e("Coil", "Load error url=${req.data}", res.throwable)
                        },
                        onSuccess = { req, res ->
                            Log.d("Coil", "Loaded url=${req.data} size=${res.drawable.intrinsicWidth}x${res.drawable.intrinsicHeight}")
                        }
                    )

                    .build(),
                contentDescription = "Recipe Image",
                modifier = Modifier
                    .fillMaxSize()
                    .onGloballyPositioned { coords -> parentImageCoords.value = coords },
                contentScale = ContentScale.Crop
            ) {
                when (val state = painter.state) {
                    is AsyncImagePainter.State.Loading -> {
                        // Platzhalter
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            MyCircularProgressIndicator()
                        }
                    }
                    is AsyncImagePainter.State.Success -> {
                        // Drawable -> ImageBitmap (für BadgeBlurred)
                        val drawable = state.result.drawable
                        val bmp = (drawable as? BitmapDrawable)?.bitmap
                        if (bmp != null) {
                            imageBitmap = bmp.asImageBitmap()
                        }
                        SubcomposeAsyncImageContent() // zeichnet das Bild
                    }
                    is AsyncImagePainter.State.Error -> {
                        // Fehlerbild/Platzhalter
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(Color.LightGray))
                    }
                    else -> SubcomposeAsyncImageContent()
                }
            }

            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.TopEnd),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                recipe.cpfRatio?.let { cpf->

                    val carbs:Int = ((cpf.carbs *0.01f* recipe.caloriesPerServing)/ CARBS_CALORIES).toInt()
                    val protein:Int = ((cpf.protein *0.01f* recipe.caloriesPerServing)/ PROTEIN_CALORIES).toInt()
                    val fat:Int = ((cpf.fat *0.01f* recipe.caloriesPerServing)/ FAT_CALORIES).toInt()
                    BadgeBlurred(
                        text = String.format("%d %s", carbs, stringResource(R.string.carbs)),
                        parentImageBitmap = imageBitmap,
                        parentLayoutCoordinates = parentImageCoords.value
                    )
                    BadgeBlurred(
                        text = String.format("%d %s", fat, stringResource(R.string.fat)),
                        parentImageBitmap = imageBitmap,
                        parentLayoutCoordinates = parentImageCoords.value
                    )
                    BadgeBlurred(
                        text = String.format("%d %s", protein, stringResource(R.string.protein)),
                        parentImageBitmap = imageBitmap,
                        parentLayoutCoordinates = parentImageCoords.value
                    )

                }
            }
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.BottomStart),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if(recipe.isVegan || recipe.isVegetarian){
                    BadgeBlurred(
                        text = if (recipe.isVegan)stringResource(R.string.vegan) else stringResource(R.string.vegetarian),
                        parentImageBitmap = imageBitmap,
                        parentLayoutCoordinates = parentImageCoords.value
                    )
                }

                BadgeBlurred(
                    text = String.format("%d kCal", recipe.caloriesPerServing.toInt()),
                    parentImageBitmap = imageBitmap,
                    parentLayoutCoordinates = parentImageCoords.value
                )
            }
        }

        // Titel und Autor in einer Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .background(Color.White)
                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val isGerman = Locale.getDefault().language == "de"

            val screenWidth = ScreenWidthInDp()

                Text(
                    modifier = Modifier.padding(start = 12.dp).weight(1f),
                    text = if(showOriginalTitle) {
                        recipe.title
                    } else{
                        if (isGerman) recipe.germanTitle else recipe.englishTitle
                          },
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .widthIn(min = (screenWidth/6.0).dp,max  =(screenWidth/3.0).dp)
                        .wrapContentWidth(Alignment.End),
                    text = if (isGerman) {
                        "von ${recipe.createdBy}"
                    } else {
                        "by ${recipe.createdBy}"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate500,
                    maxLines = 1
                )

        }
    }
}

@Composable
fun BadgeBlurred(
    text: String,
    parentImageBitmap: ImageBitmap?,
    parentLayoutCoordinates: LayoutCoordinates?,
    modifier: Modifier = Modifier,
    blurRadius: Dp = 24.dp
) {
    val badgeCoords = remember { mutableStateOf<LayoutCoordinates?>(null) }
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .wrapContentWidth()
            .height(34.dp)
            .clip(RoundedCornerShape(16.dp))
            .onGloballyPositioned { badgeCoords.value = it },
        contentAlignment = Alignment.Center
    ) {
        val parent = parentLayoutCoordinates
        val badge = badgeCoords.value

        if (parentImageBitmap != null && parent != null && badge != null) {
            // Parent-Image-Größe in px (LayoutCoordinates.size ist in px)
            val parentW = parent.size.width.toFloat()
            val parentH = parent.size.height.toFloat()

            val bmpW = parentImageBitmap.width.toFloat()
            val bmpH = parentImageBitmap.height.toFloat()

            // ContentScale.Crop mapping (Center)
            val scale = max(parentW / bmpW, parentH / bmpH)
            val displayedW = bmpW * scale
            val displayedH = bmpH * scale
            val offsetDisplayedX = (displayedW - parentW) / 2f    // wie weit das vergrößerte Bitmap links/rechts außerhalb des Parents liegt
            val offsetDisplayedY = (displayedH - parentH) / 2f

            val parentWindowPos = parent.positionInWindow()
            val badgeWindowPos = badge.positionInWindow()
            val badgeTopLeftInParent = badgeWindowPos - parentWindowPos

            val badgeW = badge.size.width.toFloat()
            val badgeH = badge.size.height.toFloat()

            // berechne src-Rect im Bitmap (in Bitmap-Pixel)
            var srcLeft = floor((badgeTopLeftInParent.x + offsetDisplayedX.toDouble()) / scale).toInt()
            var srcTop = floor((badgeTopLeftInParent.y + offsetDisplayedY.toDouble()) / scale).toInt()
            var srcRight = ceil((badgeTopLeftInParent.x + badgeW + offsetDisplayedX.toDouble()) / scale).toInt()
            var srcBottom = ceil((badgeTopLeftInParent.y + badgeH + offsetDisplayedY.toDouble()) / scale).toInt()



            // clamp an Bitmap-Grenzen
            srcLeft = srcLeft.coerceIn(0, bmpW.toInt())
            srcTop = srcTop.coerceIn(0, bmpH.toInt())
            srcRight = srcRight.coerceIn(srcLeft + 1, bmpW.toInt())
            srcBottom = srcBottom.coerceIn(srcTop + 1, bmpH.toInt())

            val srcW = srcRight - srcLeft
            val srcH = srcBottom - srcTop

            // Canvas zeichnet den ausgeschnittenen Bereich und skaliert ihn auf Badge-Größe
            Canvas(
                modifier = Modifier
                    .matchParentSize()
                    .blur(blurRadius)
            ) {
                drawImage(
                    image = parentImageBitmap,
                    srcOffset = IntOffset(srcLeft, srcTop),
                    srcSize = IntSize(srcW, srcH),
                    dstSize = size.toIntSize() // füllt das Badge
                )
            }
        }

        // Overlay + Text
        Box(
            Modifier
                .matchParentSize()
                .background(Color.Black.copy(alpha = 0.28f)))
        Text(
            modifier = Modifier.padding(horizontal = 12.dp),
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
fun SearchToggleButton(
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (isActive) Slate950 else Slate200
    val fg = if (isActive) Color.White else Slate950
    val borderStroke = if (isActive) 0.dp else 1.dp

    Button(
        shape = RoundedCornerShape(4.dp),
        modifier = modifier
            .padding(8.dp)
            .wrapContentWidth()
            .height(42.dp)
            .clip(RoundedCornerShape(4.dp)),
        border = BorderStroke(borderStroke, Slate300),
        colors = ButtonDefaults.buttonColors(containerColor = bg, contentColor = fg),
        onClick = onClick
    ) {
        val icon = if (isActive) R.drawable.x_icon else R.drawable.search_icon
        Icon(
            painter = painterResource(icon),
            contentDescription = if (isActive) "Close search" else "Search",
            tint = fg
        )
    }
}

fun Size.toIntSize(): IntSize = IntSize(width.toInt(), height.toInt())