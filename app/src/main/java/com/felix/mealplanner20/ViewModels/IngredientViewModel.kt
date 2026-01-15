package com.felix.mealplanner20.ViewModels

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felix.mealplanner20.Meals.Data.EMPTY_STRING
import com.felix.mealplanner20.Meals.Data.Ingredient
import com.felix.mealplanner20.Meals.Data.IngredientRepository
import com.felix.mealplanner20.Meals.Data.helpers.UnitOfMeasure
import com.felix.mealplanner20.Meals.Data.helpers.dgeGroup
import com.felix.mealplanner20.use_cases.UploadIngredientUseCase
import com.felix.mealplanner20.use_cases.UploadUpdateIngredientUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class IngredientViewModel @Inject constructor (
    private val ingredientRepository: IngredientRepository,
    private val prefs: SharedPreferences,
    private val uploadIngredientUseCase: UploadIngredientUseCase,
    private val uploadUpdateIngredientUseCase: UploadUpdateIngredientUseCase
):ViewModel() {
    var ingredientNameState by mutableStateOf(EMPTY_STRING)
    var ingredientCaloriesState by mutableStateOf(EMPTY_STRING)
    var ingredientFatState by mutableStateOf(EMPTY_STRING)
    var ingredientSaturatedFatState by mutableStateOf(EMPTY_STRING)
    var ingredientCarbsState by mutableStateOf(EMPTY_STRING)
    var ingredientSugarState by mutableStateOf(EMPTY_STRING)
    var ingredientProteinState by mutableStateOf(EMPTY_STRING)
    var ingredientFibreState by mutableStateOf(EMPTY_STRING)
    var ingredientAlcoholState by mutableStateOf(EMPTY_STRING)
    var ingredientDgeTypeState by mutableStateOf(dgeGroup.MILK)
    var unitOfMeasureState by mutableStateOf(UnitOfMeasure.GRAM)

    private val _searchQuery = MutableStateFlow(EMPTY_STRING)
    val searchQuery: StateFlow<String> = _searchQuery

    private val _isSearchBarVisible = MutableStateFlow(false)
    val isSearchBarVisible: StateFlow<Boolean> = _isSearchBarVisible

    private val _userRole = MutableStateFlow<String?>(null)
    val userRole: StateFlow<String?> get() = _userRole
    val isRefreshing = MutableStateFlow(false)

    private val _filteredIngredients: StateFlow<List<Ingredient>> = _searchQuery
        .flatMapLatest { query ->
            getAllIngredients.map { ingredients ->
                filterIngredients(query, ingredients)
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            emptyList()
        )

    val filteredIngredients: StateFlow<List<Ingredient>> = _filteredIngredients


    val getAllIngredients: Flow<List<Ingredient>> = ingredientRepository.getAllIngredients()

    private val _isLoading = MutableStateFlow<Boolean>(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadData()
    }
    private fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            try{
                val storedUserRole = prefs.getString("role", null)
                if (!storedUserRole.isNullOrEmpty()) {
                    _userRole.value = storedUserRole!!
                }
            }catch (e:Exception){
                Log.e("Error loading Data",e.toString())
            }finally {
                _isLoading.value = false
            }
        }
    }


    private fun filterIngredients(query: String, ingredients: List<Ingredient>,language:LANGUAGE = LANGUAGE.GERMAN): List<Ingredient> {
        val isGerman = Locale.getDefault().language == "de"
        return if (query.isBlank()) {
            ingredients
        }

        else if(!isGerman){
            ingredients.filter { it.englishName!!.contains(query, ignoreCase = true) }
        }
        else {
            ingredients.filter { it.germanName.contains(query, ignoreCase = true) }
        }
    }
    enum class LANGUAGE{
        GERMAN,
        ENGLISH
    }

    fun toggleSearchBar() {
        _isSearchBarVisible.value = !_isSearchBarVisible.value
        if(!_isSearchBarVisible.value ){
            updateSearchQuery(EMPTY_STRING)
        }
    }

    fun closeSearchbarIfOpen(){
        if(_isSearchBarVisible.value){
            toggleSearchBar()
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    fun onIngredientNameChange(newName:String){
        ingredientNameState = newName
    }

    fun onIngredientCaloriesChange(newName:String){
        ingredientCaloriesState = newName
    }
    fun onIngredientFatChange(newName:String){
        ingredientFatState = newName
    }
    fun onIngredientSaturatedFatChange(newName:String){
        ingredientSaturatedFatState = newName
    }

    fun onIngredientCarbsChange(newName:String){
        ingredientCarbsState = newName
    }
    fun onIngredientSugarChange(newName:String){
        ingredientSugarState = newName
    }
    fun onIngredientProteinChange(newName:String){
        ingredientProteinState = newName
    }
    fun onIngredientFibreChange(newName:String){
        ingredientFibreState = newName
    }
    fun onIngredientAlcoholChange(newName:String){
        ingredientAlcoholState = newName
    }
    fun onIngredientDgeTypeChange(dgeGroupName: String){
         getDgeTypeFromString(dgeGroupName)?.let{ingredientDgeTypeState = it}
    }
    fun onUnitOfMeasureChange(unitOfMeasureName: String){
        getUnitOfMeasureFromString(unitOfMeasureName)?.let{unitOfMeasureState = it}
    }
    fun getIngredientById(id:Long):Flow<Ingredient>{
        return ingredientRepository.getIngredientById(id)
    }

    suspend fun getIngredientIngredientById(id:Long):Ingredient?{
       return getIngredientById(id).firstOrNull()
    }

    fun addIngredient(ingredient: Ingredient){
        viewModelScope.launch(Dispatchers.IO) {
            ingredientRepository.addIngredient(ingredient)
        }
    }
    fun updateIngredient(ingredient: Ingredient){
        viewModelScope.launch(Dispatchers.IO) {
            ingredientRepository.updateIngredient(ingredient)
        }
    }
    fun deleteIngredient(ingredient: Ingredient){
        viewModelScope.launch(Dispatchers.IO) {
            ingredientRepository.deleteIngredient(ingredient)
        }
    }
    fun deleteIngredientById(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            delay(100)

            getIngredientById(id).firstOrNull()?.let {
                delay(100)
                ingredientRepository.deleteIngredient(it) }
        }
    }
    fun syncronizeIngredients(){
        isRefreshing.update { true }
        viewModelScope.launch(Dispatchers.IO) {
            ingredientRepository.syncronizeIngredients()
            ingredientRepository.getAllIngredients()
            isRefreshing.update { false }
        }
    }
    fun uploadIngredient(ingredient: Ingredient){
        isRefreshing.update { true }
        viewModelScope.launch(Dispatchers.IO) {
            uploadIngredientUseCase.execute(ingredient)
            ingredientRepository.getAllIngredients()
            isRefreshing.update { false }
        }
    }
    fun uploadUpdateIngredient(ingredient: Ingredient){
        isRefreshing.update { true }
        viewModelScope.launch(Dispatchers.IO) {
            uploadUpdateIngredientUseCase.execute(ingredient)
            ingredientRepository.getAllIngredients()
            isRefreshing.update { false }
        }
    }


    fun getDgeTypeFromString(dgeTypeString: String): dgeGroup? {
        return try {
            dgeGroup.valueOf(dgeTypeString)
        } catch (e: IllegalArgumentException) {
            null
        }
    }
    fun getUnitOfMeasureFromString(unitOfMeasureString: String): UnitOfMeasure? {
        return try {
            UnitOfMeasure.valueOf(unitOfMeasureString)
        } catch (e: IllegalArgumentException) {
            null
        }
    }
}