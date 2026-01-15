package com.felix.mealplanner20.Views

import android.annotation.SuppressLint
import android.provider.Settings.Global.getString
import android.text.method.LinkMovementMethod
import android.util.TypedValue
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.felix.mealplanner20.Meals.Data.EMPTY_STRING
import com.felix.mealplanner20.Navigation
import com.felix.mealplanner20.R
import com.felix.mealplanner20.Screen
import com.felix.mealplanner20.ViewModels.IngredientViewModel
import com.felix.mealplanner20.ViewModels.MainViewModel
import com.felix.mealplanner20.ViewModels.RecipeCatalogViewModel
import com.felix.mealplanner20.ViewModels.SettingsViewModel
import com.felix.mealplanner20.ui.theme.Lime500
import com.felix.mealplanner20.ui.theme.Slate200
import com.felix.mealplanner20.ui.theme.Slate400
import com.felix.mealplanner20.ui.theme.Slate500
import com.felix.mealplanner20.ui.theme.Slate950
import com.felix.mealplanner20.ui.theme.Typography
import kotlinx.coroutines.delay

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "CoroutineCreationDuringComposition",
    "UnusedMaterialScaffoldPaddingParameter"
)
@Composable
fun MainView (){
    val context = LocalContext.current
    val mainViewModel: MainViewModel = hiltViewModel()
    val ingredientViewModel: IngredientViewModel = hiltViewModel()
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val recipeCatalogViewModel: RecipeCatalogViewModel = hiltViewModel()
    val navController: NavHostController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val title = remember {
        mutableStateOf(mainViewModel.currentScreen.value.title)
    }

    val bottomBar: @Composable () -> Unit = {
        if(mainViewModel.currentScreen.value is Screen.BottomScreen||mainViewModel.currentScreen.value is Screen.TopAppBarScreen) {

            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .height(64.dp)
                ,
                containerColor = Slate950,
                contentColor = colorScheme.onBackground
                ) {
                val screensInBottom = listOf(
                    Screen.BottomScreen.NutritionCockpitScreen(context),
                    Screen.BottomScreen.MealPlanScreen(context),
                    Screen.BottomScreen.CatalogScreen(context),
                    Screen.BottomScreen.Profile(context),
                    Screen.BottomScreen.ShoppingList(context)
                )

                screensInBottom.forEach{
                    screen->
                    var isSelected = currentRoute?.startsWith(screen.route) == true

                    currentRoute?.let {
                        if(it.startsWith(Screen.TopAppBarScreen.DiscoverRecipesScreen(context).route)){
                            if(screen.route == Screen.BottomScreen.CatalogScreen(context).route){
                                isSelected = true
                            }
                        }
                        else if(it.startsWith(Screen.TopAppBarScreen.MyOwnRecipesScreen(context).route)){
                            if(screen.route == Screen.BottomScreen.CatalogScreen(context).route){
                                isSelected = true
                            }
                        }
                    }

                    val tint = if(isSelected) Lime500 else Color.White
                    NavigationBarItem(
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route)
                            title.value  =screen.title
                            mainViewModel.setCurrentScreen(screen)
                            if(screen.route == Screen.BottomScreen.CatalogScreen(context).route){
                                recipeCatalogViewModel.updateSelectedTabIndex(0)
                            }
                                  },
                        icon = {
                            Icon(tint=tint,painter = painterResource(id = screen.icon), contentDescription = screen.shortTitle(context))},
                        label = {
                            Text(
                                text = screen.shortTitle(context),
                                color = tint,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp)
                            ) },
                        colors = NavigationBarItemColors(
                            selectedIconColor = colorScheme.primary,
                            selectedTextColor = colorScheme.primary,
                            selectedIndicatorColor = Color.Black,
                            unselectedIconColor = colorScheme.secondary,
                            unselectedTextColor = colorScheme.secondary,
                            disabledIconColor = colorScheme.tertiary,
                            disabledTextColor = colorScheme.tertiary,
                        ),
                        modifier = Modifier
                            .padding(top = 6.dp)
                            .testTag("bottom_nav_${screen.route}")
                    )
                }
            }
        }
    }
    Scaffold (
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = bottomBar,
        //containerColor = Slate950,
        topBar = {
            when(mainViewModel.currentScreen.value.route){
                Screen.IngredientHomeScreen(context).route -> IngredientTopAppBar(
                    navController = navController,
                    ingredientViewModel = ingredientViewModel,
                    title = mainViewModel.currentTopAppBarTitle.value,
                )
                Screen.AddIngredientToRecipeScreen(context).route -> IngredientTopAppBar(
                    navController = navController,
                    ingredientViewModel = ingredientViewModel,
                    title = stringResource(R.string.add_ingredient),
                )
                Screen.AddUpdateRecipeScreen(context).route -> AddUpdateRecipeTopAppBar(
                    navController = navController,
                    title = mainViewModel.currentTopAppBarTitle.value,
                    mainViewModel = mainViewModel
                )
                Screen.RecipeViewOnlyScreen(context).route -> {}

                Screen.AddUpdateIngredientScreen(context = context).route -> AddUpdateIngredientTopAppBar(
                    navController = navController,
                    title = context.getString(R.string.add_ingredient)
                )
                Screen.BottomScreen.CatalogScreen(context).route -> RecipesTopAppBar(
                    navController = navController,
                    recipeCatalogViewModel = recipeCatalogViewModel
                )
                Screen.TopAppBarScreen.DiscoverRecipesScreen(context).route -> RecipesTopAppBar(
                    navController = navController,
                    recipeCatalogViewModel = recipeCatalogViewModel
                )
                Screen.TopAppBarScreen.MyOwnRecipesScreen(context).route -> RecipesTopAppBar(
                    navController = navController,
                    recipeCatalogViewModel = recipeCatalogViewModel
                )
                Screen.ConfigProbabilitiesScreen(context).route -> BackArrowTopAppBar(
                    title = context.getString(R.string.config_probabilities),
                    onBackArrowClick = {
                        navController.navigateUp()
                    }
                )
                Screen.Login(context).route -> BackArrowTopAppBar(
                    title =  context.getString(R.string.sign_in),
                    onBackArrowClick = {
                        navController.navigateUp()
                    }
                )
                Screen.SignUp(context).route ->BackArrowTopAppBar(
                    title =  context.getString(R.string.sign_up),
                    onBackArrowClick = {
                        navController.navigateUp()
                    }
                )
                Screen.BottomScreen.Profile(context).route ->{}

                Screen.AddMealPlanRecipeScreen(context).route ->BackArrowTopAppBar(
                    title =  mainViewModel.currentTopAppBarTitle.value,
                    onBackArrowClick = {
                        navController.navigateUp()
                    }
                )
                Screen.ReplaceMealPlanRecipeScreen(context).route ->BackArrowTopAppBar(
                    title =  mainViewModel.currentTopAppBarTitle.value,
                    onBackArrowClick = {
                        navController.navigateUp()
                    }
                )

                Screen.DiscoverRecipesSingleViewScreen(context).route ->BackArrowTopAppBar(
                    title =  mainViewModel.currentTopAppBarTitle.value,
                    onBackArrowClick = {
                        navController.navigateUp()
                    }
                )
                Screen.BottomScreen.NutritionCockpitScreen(context).route -> NutriScoreInfoTopAppBar(
                    title = title.value,
                )
                Screen.TopAppBarScreen.SendFeedback(context).route -> BackArrowTopAppBar(
                    title = stringResource(R.string.send_feedback),
                    onBackArrowClick = {
                        navController.navigateUp()
                    }
                )
                else -> DefaultTopAppBar(
                    title = title.value,
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }

    ){paddingValues->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)){
            Navigation(navController = navController, ingredientViewModel = ingredientViewModel, mainViewModel = mainViewModel, settingsViewModel = settingsViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultTopAppBar(
    title: String,
){
    TopAppBar(
        colors = TopAppBarColors(
            containerColor = Slate200,
            scrolledContainerColor = Slate200,
            navigationIconContentColor = Slate950,
            titleContentColor = Slate200,
            actionIconContentColor = Slate950
        ),

        title = { Text(modifier = Modifier.padding(start = 16.dp), text = title) },
        actions = {

        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackArrowTopAppBar(
    title: String,
    onBackArrowClick:()->Unit
){
    val scope = rememberCoroutineScope()
    TopAppBar(
        colors = TopAppBarColors(
            containerColor = Slate200,
            scrolledContainerColor = Slate200,
            navigationIconContentColor = Slate950,
            titleContentColor = Slate200,
            actionIconContentColor = Slate950
        ),
        title = { Text(text = title) },
        actions = {
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    onBackArrowClick()
                }) {
                Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Navigate back")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutriScoreInfoTopAppBar(
    title: String,
){
    val scope = rememberCoroutineScope()
    var showInfo by remember { mutableStateOf(false) }
    TopAppBar(
        colors = TopAppBarColors(
            containerColor = Slate200,
            scrolledContainerColor = Slate200,
            navigationIconContentColor = Slate950,
            titleContentColor = Slate200,
            actionIconContentColor = Slate950
        ),

        title = { Text(modifier = Modifier.padding(start = 16.dp), text = title) },
        actions = {
            IconButton(
                onClick = {
                     showInfo = !showInfo
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_info_outline_24) ,
                    contentDescription = "Nutriscore information")
            }

            DropdownMenu(
                expanded = showInfo,
                onDismissRequest = { showInfo = false }
            ) {

                     HtmlTextRes(
                        resId = R.string.nutritionscrore_info_text,
                        modifier = Modifier.padding(16.dp).widthIn(max = 300.dp)
                    )
            }
        },
    )
}

@Composable
fun HtmlTextRes(
    @StringRes resId: Int,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            TextView(ctx).apply {
                movementMethod = LinkMovementMethod.getInstance() // Links klickbar
// Optional: Stil an Compose anlehnen
                setTextSize(TypedValue.COMPLEX_UNIT_SP,14f)
            }
        },
        update = { tv ->
            tv.text = HtmlCompat.fromHtml(
                context.getString(resId),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUpdateRecipeTopAppBar(
    navController: NavHostController,
    title: String,
    mainViewModel: MainViewModel
){
    TopAppBar(
        colors = TopAppBarColors(
            containerColor = Slate200,
            scrolledContainerColor = Slate200,
            navigationIconContentColor = Slate950,
            titleContentColor = Slate200,
            actionIconContentColor = Slate950
        ),
        title = { Text(text = title) },
        navigationIcon = {
            IconButton(
                onClick = {
                    if(mainViewModel.changesMade.value){
                        mainViewModel.setShowExitWithoutSaveAlertDialog(true)
                    }else{
                        navController.navigateUp()
                    }
                }) {
                Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Navigate back")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipesTopAppBar(
    navController: NavHostController,
    recipeCatalogViewModel: RecipeCatalogViewModel
){
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val selectedTabIndex by recipeCatalogViewModel.selectedTabIndex.collectAsState()

    Column {
        TopAppBar(
            colors = TopAppBarColors(
                containerColor = Slate200,
                scrolledContainerColor = Slate200,
                navigationIconContentColor = Slate950,
                titleContentColor = Slate200,
                actionIconContentColor = Slate950
            ),
            title = { Text(EMPTY_STRING) },
            actions = {
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = Slate200,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = Slate500
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = {
                                recipeCatalogViewModel.updateSelectedTabIndex(0)
                                navController.navigate(Screen.TopAppBarScreen.MyOwnRecipesScreen(context).route)
                            },
                            text = { Text(
                                text = stringResource(R.string.my_recipes),
                                style = Typography.titleMedium

                            ) }
                        )
                        Tab(
                            selected = selectedTabIndex == 1,
                            onClick = {
                                recipeCatalogViewModel.updateSelectedTabIndex(1)
                                navController.navigate(Screen.TopAppBarScreen.DiscoverRecipesScreen(context).route)
                                },
                            text = {
                                Text(
                                    stringResource(R.string.discover),
                                    style = Typography.titleMedium)
                            }
                        )
                    }
                }
            )

            when (selectedTabIndex) {
                0 -> Screen.TopAppBarScreen.MyOwnRecipesScreen(context = context)
                1 -> Screen.TopAppBarScreen.DiscoverRecipesScreen(context = context)
            }
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientTopAppBar(
    navController:NavController,
    ingredientViewModel: IngredientViewModel,
    title: String
) {

    val isSearchBarVisible by ingredientViewModel.isSearchBarVisible.collectAsState()
    val searchQuery by ingredientViewModel.searchQuery.collectAsState(initial = EMPTY_STRING)

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isSearchBarVisible) {
        if (isSearchBarVisible) {
            delay(75)
            focusRequester.requestFocus()
        }
    }

    TopAppBar(
        colors = TopAppBarColors(
            containerColor = Slate200,
            scrolledContainerColor = Slate200,
            navigationIconContentColor = Slate950,
            titleContentColor = Slate200,
            actionIconContentColor = Slate950
        ),
        title = {
            if (isSearchBarVisible) {
                StyledSearchBar(
                    searchQuery = searchQuery,
                    onQueryChange ={ newQuery ->
                        ingredientViewModel.updateSearchQuery(newQuery)
                                   },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    onXClick = { ingredientViewModel.toggleSearchBar() }
                )
            } else {
                Text(text = title)
            }
        },
        actions = {
            if (!isSearchBarVisible) {
                IconButton(onClick = { ingredientViewModel.toggleSearchBar() }) {
                    Icon(painter = painterResource(R.drawable.search_icon), contentDescription = "Search", tint = Slate950)
                }
            }
        },
        navigationIcon = {
            if(!isSearchBarVisible){
                IconButton(
                    onClick = {
                        navController.navigateUp()
                    }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)
                }
            }
        }
    )
}

@Composable
fun StyledSearchBar(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onXClick:()->Unit,
    placeholder: String = stringResource(R.string.search_ingredients)
) {
    TextField(
        value = searchQuery,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 20.sp, color = Slate400)
            ) },
        singleLine = true,
        leadingIcon = { Icon(painter = painterResource(R.drawable.search_icon), contentDescription = "Search Icon", tint = Slate950) },
        trailingIcon = {
            IconButton(
                onClick = {
                   onXClick()
                }) {
                Icon(painter = painterResource(R.drawable.x_icon), contentDescription = "cancel search", tint = Slate950)
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 0.dp),
        shape = RoundedCornerShape(24.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Slate200,
            unfocusedContainerColor = Slate200,
            disabledContainerColor = colorScheme.onSurface.copy(alpha = 0.12f),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = Slate950
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUpdateIngredientTopAppBar(
    navController: NavHostController,
    title: String
){
    val scope = rememberCoroutineScope()
    TopAppBar(
        colors = TopAppBarColors(
            containerColor = Slate200,
            scrolledContainerColor = Slate200,
            navigationIconContentColor = Slate950,
            titleContentColor = Slate200,
            actionIconContentColor = Slate950
        ),
        title = { Text(text = title) },
        actions = {

        },
        navigationIcon = {
            IconButton(
                onClick = {
                    navController.navigateUp()
                }) {
                Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)
            }
        }
    )
}