package com.felix.mealplanner20.ViewModels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.felix.mealplanner20.Meals.Data.DTO.RecipeDTO
import com.felix.mealplanner20.Meals.Data.IngredientWithRecipe
import com.felix.mealplanner20.Meals.Data.Recipe
import com.felix.mealplanner20.Meals.Data.RecipeDescription
import com.felix.mealplanner20.Meals.Data.RecipeRepository
import com.felix.mealplanner20.Meals.Data.SettingsRepository
import com.felix.mealplanner20.Meals.Data.helpers.Mealtype
import com.felix.mealplanner20.caching.DiscoverMemoryCache
import com.felix.mealplanner20.use_cases.DownloadRecipeResult
import com.felix.mealplanner20.use_cases.DownloadRecipeUseCase
import com.felix.mealplanner20.use_cases.SynchronizeIngredientsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import java.util.Locale


@HiltViewModel
class DiscoverRecipesViewModel @Inject constructor (
    private val recipeRepository: RecipeRepository,
    private val synchronizeIngredientsUseCase: SynchronizeIngredientsUseCase,
    private val downloadRecipeUseCase: DownloadRecipeUseCase,
    private val settingsRepository: SettingsRepository
): ViewModel(){

    private var lastExecutedQueryNorm: String = ""
    private var searchJob: Job? = null
    private val minSearchChars = 2

    val showOriginalTitle = settingsRepository.observeShowOriginalTitle()

    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes

    private val _isSearchBarVisible = MutableStateFlow(false)
    val isSearchBarVisible: StateFlow<Boolean> = _isSearchBarVisible

    private val _ingredientWithRecipes = MutableStateFlow<List<IngredientWithRecipe>>(emptyList())
    val ingredientWithRecipes: StateFlow<List<IngredientWithRecipe>> = _ingredientWithRecipes

    private val _recipeDescriptionSteps = MutableStateFlow<List<RecipeDescription>?>(emptyList())
    val recipeDescriptionSteps: StateFlow<List<RecipeDescription>?> = _recipeDescriptionSteps

    private val _isLoading = MutableStateFlow<Boolean>(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isLoadingAdditionalItems = MutableStateFlow<Boolean>(false)
    val isLoadingAdditionalItems: StateFlow<Boolean> = _isLoadingAdditionalItems

    private val _singleRecipeIsLoading = MutableStateFlow<Boolean>(false)
    val singleRecipeIsLoading: StateFlow<Boolean> = _singleRecipeIsLoading

    private val _currentRecipe = MutableStateFlow<Recipe?>(null)
    val currentRecipe: StateFlow<Recipe?> = _currentRecipe

    private val _type = MutableStateFlow<Mealtype?>(Mealtype.MEAL)
    val type: StateFlow<Mealtype?> = _type

    private val _downloadRecipeChannel = Channel<DownloadRecipeResult>(Channel.BUFFERED)
    val downloadRecipeFlow = _downloadRecipeChannel.receiveAsFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    private var searchPage = 0
    private var searchAllFetched = false

    private fun currentLang(): String =
        if (Locale.getDefault().language == "de") "de" else "en"




    var currentPage = MutableStateFlow(0)
    private val pageSize = 15
    private var allRecipesFetched = false

    init {
        loadRecipes()
    }

    fun resetCurrentPage(){
        currentPage.value = 0 //TODO muss noch mehr passieren?
        Log.d("CURRENTPAGE: ","${currentPage.value}")
    }

    fun syncIngredients(){
        viewModelScope.launch(Dispatchers.IO) {
            synchronizeIngredientsUseCase.invoke()
        }
    }
    fun resetAllRecipesFetched(){
        allRecipesFetched = false
    }
    fun setMealType(newType:Mealtype?){
        _type.value = newType
    }
    fun setMealTypeAndReload(newType:Mealtype?){
        setMealType(newType)
        resetCurrentPage()
        resetAllRecipesFetched()
        loadRecipes()
    }

    fun loadRecipes(areAdditional:Boolean=false) {
        if (_isLoadingAdditionalItems.value) return
        if (areAdditional) _isLoadingAdditionalItems.value = true else _isLoading.value = true

        if (!areAdditional && tryLoadFromCache(_type.value, areAdditional = false)) {
            return
        }

        if (!allRecipesFetched) {
            viewModelScope.launch {
                try {
                    val nextPage = currentPage.value + 1
                    val recipeResponse: RecipeResponse? = if (_type.value == null) {
                        recipeRepository.getRecipesFromServer(nextPage, pageSize)
                    } else {
                        recipeRepository.getTypeRecipesFromServer(type = _type.value!!, nextPage, pageSize)
                    }
                    recipeResponse?.let { response ->
                        val mapped = response.recipes.map { it.toRecipe() }

                        if (areAdditional) {
                            // Cache anhängen und State aus Cache übernehmen
                            DiscoverMemoryCache.putRecipes(_type.value, mapped, nextPage, allFetched = !response.hasNextPage)
                            DiscoverMemoryCache.get(_type.value)?.let { snap ->
                                _recipes.value = snap.recipes
                                currentPage.value = snap.currentPage
                                allRecipesFetched = snap.allRecipesFetched
                            }
                        } else {
                            // Erste Seite für den Typ ersetzen
                            DiscoverMemoryCache.replaceRecipes(_type.value, mapped, currentPage = 1, allFetched = !response.hasNextPage)
                            DiscoverMemoryCache.get(_type.value)?.let { snap ->
                                _recipes.value = snap.recipes
                                currentPage.value = snap.currentPage
                                allRecipesFetched = snap.allRecipesFetched
                            }
                        }
                    }

                } catch (e: Exception) {
                    Log.e("ERROR loading Recipes", "Fehlermeldung: ${e.stackTraceToString()}")
                } finally {
                    if (areAdditional) _isLoadingAdditionalItems.value = false else _isLoading.value = false
                }
            }
        } else {
            if (areAdditional) _isLoadingAdditionalItems.value = false else _isLoading.value = false
        }

    }

    fun loadAdditionalRecipes() {
        if (_isSearching.value) executeSearch(reset = false) else loadRecipes(areAdditional = true)
    }

    fun downloadRecipe(context: Context, recipeId: Long) {
        viewModelScope.launch {
            _downloadRecipeChannel.send(DownloadRecipeResult.Loading)
            try {
                val result = downloadRecipeUseCase.invoke(context, recipeId)
                _downloadRecipeChannel.send(result)
            } catch (e: Exception) {
                _downloadRecipeChannel.send(DownloadRecipeResult.UnknownError(e))
            }
        }
    }

    fun downloadRecipe(context: Context) {
        val recipeId = currentRecipe.value?.id
        if (recipeId == null) {
            viewModelScope.launch {
                _downloadRecipeChannel.send(DownloadRecipeResult.RecipeNotFound)
            }
            return
        }
        downloadRecipe(context, recipeId)
    }

    fun loadSingleRecipe(recipeId: Long) {
        viewModelScope.launch {
            _singleRecipeIsLoading.value = true
            try {
                loadCurrentRecipe(recipeId)
                fetchComponentsForSingleRecipe(recipeId)
            } catch (e: Exception) {
                Log.e("DiscoverRecipesViewModel", "Error loading recipe details for id=$recipeId", e)
            } finally {
                _singleRecipeIsLoading.value = false
                Log.d("DiscoverRecipesViewModel", "loadSingleRecipe FINALLY set singleRecipeIsLoading=false for id=$recipeId")
            }
        }
    }


    suspend fun fetchComponentsForSingleRecipe(recipeId: Long) {
        try {
            val components = recipeRepository.getRecipeComponentsFromServer(recipeId)

            components?.let {
                components.first?.let {
                    _ingredientWithRecipes.value = it
                }
                components.second?.let {
                    _recipeDescriptionSteps.value = it
                }
            }

            if (components != null && components.first != null) {
                _ingredientWithRecipes.value = components.first!!
            }
        } catch (e: Exception) {
            Log.e("DiscoverRecipesViewModel", "Error fetching ingredients", e)
        }
    }

     fun loadCurrentRecipe(recipeId: Long) {
            _currentRecipe.value = recipes.value.find { it.id == recipeId }
         Log.d("CURRENT RECIPE LOAD","${_currentRecipe.value}")
     }
    private fun tryLoadFromCache(type: Mealtype?, areAdditional: Boolean): Boolean {
        if (areAdditional) return false
        val snap = DiscoverMemoryCache.get(type) ?: return false
        _recipes.value = snap.recipes
        currentPage.value = snap.currentPage
        allRecipesFetched = snap.allRecipesFetched
        _isLoading.value = false
        return true
    }

    fun setSearchQueryAndExecuteSearch(q: String) {
        _searchQuery.value = q
        if (q.isBlank() && _isSearching.value) {
            toggleSearchMode(false)
        }
        executeSearch(true)
    }

    fun executeSearch(reset: Boolean = true) {
        val qRaw = _searchQuery.value
        val qNorm = qRaw.trim().lowercase()

        _isSearching.value = true

        if (qNorm.length < minSearchChars) {
            _recipes.value = emptyList()
            searchPage = 0
            searchAllFetched = true
            return
        }

        if (reset && qNorm == lastExecutedQueryNorm) {
            return
        }
        if (!reset && qNorm != lastExecutedQueryNorm) {
            executeSearch(reset = true)
            return
        }

        if (reset) {
            searchJob?.cancel()
        }

        searchJob = viewModelScope.launch {
            if (reset) {
                //_recipes.value = emptyList()
                searchPage = 0
                searchAllFetched = false
                _isLoading.value = true
            } else {
                if (searchAllFetched || _isLoadingAdditionalItems.value) return@launch
                _isLoadingAdditionalItems.value = true
            }
            try {
                val nextPage = searchPage + 1
                val resp = recipeRepository.searchRecipesFromServer(
                    q = qRaw.trim(),     // Roh-Query an API
                    lang = currentLang(),
                    page = nextPage,
                    pageSize = pageSize
                )
                resp?.let { r ->
                    val newItems = r.recipes.map { it.toRecipe() }
                    _recipes.value = if (reset) newItems else _recipes.value + newItems
                    searchPage = nextPage
                    searchAllFetched = !r.hasNextPage
                    if (reset) lastExecutedQueryNorm = qNorm
                }
            } finally {
                if (reset) _isLoading.value = false else _isLoadingAdditionalItems.value = false
            }
        }
    }
    fun toggleSearchMode(enable: Boolean) {
        if (enable) {
            _isSearching.value = true
            _recipes.value = emptyList()
            searchPage = 0
            searchAllFetched = false
        } else {
            _isSearching.value = false
            _searchQuery.value = ""
// Zurück in Discover
            resetCurrentPage()
            resetAllRecipesFetched()
            loadRecipes()
        }
    }
}

@Serializable
data class RecipeResponse(
    val recipes: List<RecipeDTO>,
    val hasNextPage: Boolean
)

data class StepUriMap(
    val stepId: String,
    val image: Uri?
)
