package com.felix.mealplanner20.ViewModels

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.felix.mealplanner20.ADD_EDIT_RECIPE_KEY
import com.felix.mealplanner20.Meals.Data.EMPTY_STRING
import com.felix.mealplanner20.Meals.Data.IngredientWithRecipe
import com.felix.mealplanner20.Meals.Data.PublishRecipeResult
import com.felix.mealplanner20.Meals.Data.Recipe
import com.felix.mealplanner20.Meals.Data.RecipeDescription
import com.felix.mealplanner20.Meals.Data.RecipeRepository
import com.felix.mealplanner20.Meals.Data.helpers.UnitOfMeasure
import com.felix.mealplanner20.R
import com.felix.mealplanner20.Views.Recipes.PublishRequirement
import com.felix.mealplanner20.use_cases.RecipeUseCases
import com.felix.mealplanner20.use_cases.UpdateRecipeUseCase
import com.felix.mealplanner20.use_cases.UploadRecipeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddEditRecipeViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository,
    private val recipeUseCase: RecipeUseCases,
    private val publishRecipeUseCase: UploadRecipeUseCase,
    private val updateRecipeUseCase: UpdateRecipeUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var hasLoaded = false
    private var _isDirty = mutableStateOf(false)
    val isDirty:State<Boolean> = _isDirty

    var recipeIngredients by mutableStateOf<List<IngredientWithRecipe>>(emptyList())

    private val recipeIngredientsFlow: Flow<List<IngredientWithRecipe>> =
        snapshotFlow { recipeIngredients }

    private var tempRecipe = Recipe(caloriesPerServing = 111f)

    private val  _recipeName = mutableStateOf<String>( EMPTY_STRING)
    val recipeName: State<String> = _recipeName
    private var currentRecipeId: Long? = null

    private val _allowedUnitsForIngredients = MutableStateFlow<Map<Long, List<Pair<UnitOfMeasure, Float>>>>(emptyMap())
    val allowedUnitsForIngredients: StateFlow<Map<Long, List<Pair<UnitOfMeasure, Float>>>> = _allowedUnitsForIngredients.asStateFlow()

    private var originalRecipeImgUri: Uri? = null
    private var originalStepImgById: Map<String, String?> = emptyMap()

    private val _isMeal = MutableStateFlow(tempRecipe.isMeal)
    val isMeal: StateFlow<Boolean> = _isMeal.asStateFlow()

    private val _isSnack = MutableStateFlow(tempRecipe.isSnack)
    val isSnack: StateFlow<Boolean> = _isSnack.asStateFlow()

    private val _isBreakfast = MutableStateFlow(tempRecipe.isBreakfast)
    val isBreakfast: StateFlow<Boolean> = _isBreakfast.asStateFlow()

    private val _isBeverage = MutableStateFlow(tempRecipe.isBeverage)
    val isBeverage: StateFlow<Boolean> = _isBeverage.asStateFlow()

    private val _isDessert = MutableStateFlow(tempRecipe.isDessert)
    val isDessert: StateFlow<Boolean> = _isDessert.asStateFlow()

    private val _remoteId = mutableStateOf(tempRecipe.remoteId)
    val remoteRecipeId = _remoteId


    private val _canEditRemote = MutableStateFlow(false)
    val canEditRemote: StateFlow<Boolean> = _canEditRemote.asStateFlow()



    private val  _createdBy = mutableStateOf<String>( EMPTY_STRING)
    val createdBy: State<String> = _createdBy

    private val _servings = mutableStateOf<Float>(tempRecipe.servings)
    val servings: State<Float> = _servings

    private val _imgUri = mutableStateOf<Uri?>(tempRecipe.imgUri)
    val imgUri: State<Uri?> = _imgUri

    private val _recipeDescriptionSteps: MutableState<List<RecipeDescription>?> = mutableStateOf(null)
    val recipeDescriptionSteps: State<List<RecipeDescription>?> = _recipeDescriptionSteps

    private val _removedItems = MutableStateFlow<Set<Long>>(emptySet())
    val removedItems: StateFlow<Set<Long>> = _removedItems.asStateFlow()

    private val _isLoading = MutableStateFlow<Boolean>(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isPublishing = MutableStateFlow<Boolean>(false)
    val isPublishing: StateFlow<Boolean> = _isPublishing


    private val recipeNameFlow: Flow<String> = snapshotFlow { _recipeName.value }

    // Publish-Requirement: Name darf nicht leer/blank sein
    val publishRequirementNameNotEmpty: StateFlow<PublishRequirement> =
        recipeNameFlow
            .map { name ->
                PublishRequirement(
                    id = "has_name",
                    label = "Titel darf nicht leer sein",
                    isMet = name.isNotBlank() // true, wenn Name nicht nur Whitespace ist
                )
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                PublishRequirement(
                    id = "has_name",
                    label = "Titel darf nicht leer sein",
                    isMet = false
                )
            )

    val publishRequirementTypeSelected: StateFlow<PublishRequirement> =
        combine(
            isMeal,
            isSnack,
            isBreakfast,
            isBeverage,
            isDessert
        ) { meal, snack, breakfast, beverage, dessert ->
            PublishRequirement(
                id = "type_selected",
                label = "Mindestens ein Typ muss ausgewählt sein",
                isMet = meal || snack || breakfast || beverage || dessert
            )
        }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                // Startwert (muss vom Typ PublishRequirement sein)
                PublishRequirement(
                    id = "type_selected",
                    label = "Mindestens ein Typ muss ausgewählt sein",
                    isMet = false
                )
            )

    val publishRequirementHasIngredient: StateFlow<PublishRequirement> =
        recipeIngredientsFlow
            .map { list ->
                PublishRequirement(
                    id = "has_ingredient",
                    label = "Mindestens eine Zutat muss vorhanden sein",
                    isMet = list.isNotEmpty()
                )
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                PublishRequirement("has_ingredient", "Mindestens eine Zutat muss vorhanden sein", isMet = false)
            )

    private val imgUriFlow: Flow<Uri?> = snapshotFlow { _imgUri.value }

    val publishRequirementHasImage: StateFlow<PublishRequirement> =
        imgUriFlow
            .map { uri ->
                PublishRequirement(
                    id = "has_image",
                    label = "Das Rezept muss ein Bild haben",
                    isMet = (uri != null) // true wenn nicht null
                )
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                PublishRequirement(
                    id = "has_image",
                    label = "Das Rezept muss ein Bild haben",
                    isMet = false
                )
            )

    private val recipeDescriptionStepsFlow: Flow<List<RecipeDescription>?> =
        snapshotFlow { _recipeDescriptionSteps.value }

    // PublishRequirement: mindestens ein Beschreibungsschritt
    val publishRequirementHasDescription: StateFlow<PublishRequirement> =
        recipeDescriptionStepsFlow
            .map { list ->
                PublishRequirement(
                    id = "has_description",
                    label = "Mindestens ein Beschreibungsschritt muss vorhanden sein",
                    isMet = !list.isNullOrEmpty() // true, wenn Liste nicht null und nicht leer
                )
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                PublishRequirement(
                    id = "has_description",
                    label = "Mindestens ein Beschreibungsschritt muss vorhanden sein",
                    isMet = false
                )
            )

    private val isDirtyFlow: Flow<Boolean> = snapshotFlow { _isDirty.value }

    val publishRequirementNoUnsavedChanges: StateFlow<PublishRequirement> =
        isDirtyFlow
            .map { dirty ->
                PublishRequirement(
                    id = "no_unsaved_changes",
                    label = "Recipe has no unsaved changes",
                    isMet = !dirty // erfüllt, wenn nicht "dirty"
                )
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                PublishRequirement(
                    id = "no_unsaved_changes",
                    label = "Recipe has no unsaved changes",
                    isMet = !_isDirty.value
                )
            )



    init {

        savedStateHandle.get<Long>(ADD_EDIT_RECIPE_KEY)?.let {it->
            if(it != 0L){
                viewModelScope.launch {
                    _isLoading.value = true
                    recipeUseCase.getRecipeByIdUseCase(it)?.also { recipe ->
                        val myRecipe = recipe.first()
                        currentRecipeId = myRecipe.id
                        _remoteId.value = myRecipe.remoteId
                        _recipeName.value = myRecipe.title
                        _isMeal.value = myRecipe.isMeal
                        _isSnack.value = myRecipe.isSnack
                        _isBreakfast.value = myRecipe.isBreakfast
                        _isBeverage.value = myRecipe.isBeverage
                        _isDessert.value = myRecipe.isDessert
                        _servings.value = myRecipe.servings
                        _imgUri.value = myRecipe.imgUri
                        recipeUseCase.getAllIngredientsForOneRecipeUseCase(it)
                        _recipeDescriptionSteps.value = recipeRepository.getRecipeDescriptionStepsByRecipeId(myRecipe.id)
                        _isLoading.value = false

                        originalRecipeImgUri = _imgUri.value
                        originalStepImgById = _recipeDescriptionSteps.value
                            ?.associate { it.id to it.imgUri }
                            ?: emptyMap()
                        recomputeUpdatePermission()
                    }
                }
            }else{
                _isLoading.value = false
            }
        }
        Log.d("Recipe Info(_remoteId.value): ","${_remoteId.value}")
    }
    fun loadRecipe(recipeId: Long,checkForHasLoaded:Boolean) {
        if(!checkForHasLoaded or !hasLoaded){viewModelScope.launch {
            try {
                recipeUseCase.getRecipeByIdUseCase(recipeId)?.let { recipe ->
                    val myRecipe = recipe.first()
                    currentRecipeId = myRecipe.id
                    _remoteId.value = myRecipe.remoteId
                    _recipeName.value = myRecipe.title
                    _isMeal.value = myRecipe.isMeal
                    _isSnack.value = myRecipe.isSnack
                    _isBreakfast.value = myRecipe.isBreakfast
                    _isBeverage.value = myRecipe.isBeverage
                    _isDessert.value = myRecipe.isDessert
                    _imgUri.value = myRecipe.imgUri
                    _createdBy.value = myRecipe.createdBy?:"system"
                    _servings.value = myRecipe.servings
                    recipeIngredients = recipeUseCase.getAllIngredientsForOneRecipeUseCase(myRecipe.id).first()
                    recipeIngredients.forEach { ingredient ->
                        loadAllowedUnitsForIngredient(ingredient.ingredientId)
                    }
                    _recipeDescriptionSteps.value = recipeRepository.getRecipeDescriptionStepsByRecipeId(myRecipe.id)

                    originalRecipeImgUri = _imgUri.value
                    originalStepImgById = _recipeDescriptionSteps.value
                        ?.associate { it.id to it.imgUri }
                        ?: emptyMap()
                    recomputeUpdatePermission()
                }
            } catch (e: Exception) {
                Log.e("ERROR","loadRecipe fehlgeschlagen")
            }
        }

        hasLoaded = true
        }
    }

    fun changeAnything(
        newImgUri: Uri? = null,
        newName: String? = null,
        newDescription: List<RecipeDescription>? = null,
        updatedDescriptionStep: RecipeDescription? = null,
        newIsMeal: Boolean? = null,
        newIsSnack: Boolean? = null,
        newIsBreakfast: Boolean? = null,
        newServings: Float? = null,
        newIsBeverage: Boolean? = null,
        newIsDessert: Boolean? = null,
        ingredientToDelete: IngredientWithRecipe? = null,
        ingredientToUpdate: IngredientWithRecipe? = null,
        tempIngredientToAdd: Triple<Long, Float, UnitOfMeasure>? = null,
        ingredientToAdd: Triple<Long, Float, UnitOfMeasure>? = null,
        addNewDescriptionStep:Boolean = false,
        newDescriptionStepText:Pair<String,String>? = null,
        newDescriptionStepUri:Pair<String,String>? = null,
        deleteStepNr:String? = null,
        additionalAction:()->Unit
    ){
        newImgUri?.let { changeImage(it) }
        newName?.let { onRecipeNameChange(it) }
        newDescription?.let { onRecipeDescriptionChange(it) }
        newIsMeal?.let { onIsMealChange(it) }
        newIsSnack?.let { onIsSnackChange(it) }
        newIsBreakfast?.let { onIsBreakfastChange(it) }
        newServings?.let { onServingsChange(it) }
        newIsBeverage?.let { onIsBeverageChange(it) }
        newIsDessert?.let { onIsDessertChange(it) }

        ingredientToDelete?.let { deleteIngredientFromRecipe(it) }
        ingredientToUpdate?.let { onSingleRecipeIngredientsChange(it) }

        tempIngredientToAdd?.let { (ingredientId, quantity, unit) ->
            addIngredientToTempRecipe(ingredientId, quantity, unit)
        }
        ingredientToAdd?.let { (ingredientId, quantity, unit) ->
            addIngredientToRecipe(currentRecipeId!!,ingredientId, quantity, unit)
        }


        if (addNewDescriptionStep){
            if(currentRecipeId!=null && currentRecipeId != 0L){
                addEmptyDescriptionStepToRecipe(currentRecipeId!!)
            }
            else{
                addEmptyDescriptionStepToTempRecipe()
            }
        }
        newDescriptionStepText?.let {
            updateStepText(it.first,it.second)
        }
        newDescriptionStepUri?.let {
            updateStepPicture(it.first,it.second)
        }

        deleteStepNr?.let {
            deleteRecipeStep(it)
        }

        updatedDescriptionStep?.let { updateDescriptionStep(it) }

       additionalAction()
        _isDirty.value = true
    }

    fun loadAllowedUnitsForIngredient(ingredientId: Long) {
        viewModelScope.launch {
            try {
                val allowedUnitsFromRepo = recipeRepository.getAllowedUnitsForIngredient(ingredientId)
                val mappedUnits = allowedUnitsFromRepo.mapNotNull { allowedUnit ->
                    val uomEnum = try {
                        UnitOfMeasure.valueOf(allowedUnit.unitOfMeasure)
                    } catch (e: Exception) {
                        null
                    }
                    uomEnum?.let { it to allowedUnit.gramsPerUnit }
                }

                // Map updaten
                _allowedUnitsForIngredients.value = _allowedUnitsForIngredients.value + (ingredientId to mappedUnits)
            } catch (e: Exception) {
                Log.e("AddEditRecipeVM", "Fehler beim Laden der AllowedUnits für Ingredient $ingredientId", e)
            }
        }
    }


    private fun updateDescriptionStep(step: RecipeDescription) {
        _recipeDescriptionSteps.value = _recipeDescriptionSteps.value?.map {
            if (it.id == step.id) step.copy(text = step.text, imgUri = step.imgUri) else it
        }
    }

    private fun changeImage(newImgUri: Uri) {
        _imgUri.value = newImgUri
    }
    private fun onRecipeNameChange(newName:String){
        _recipeName.value = newName
    }
    private fun onRecipeDescriptionChange(newDescription: List<RecipeDescription>){
        _recipeDescriptionSteps.value = newDescription
    }

    private fun onIsMealChange(newValue:Boolean) {
        _isMeal.value = newValue
    }

    private fun onIsSnackChange(newValue:Boolean) {
        _isSnack.value =  newValue
    }

    private fun onIsBreakfastChange(newValue:Boolean) {
        _isBreakfast.value = newValue
    }
    private fun onServingsChange(newValue:Float) {
        _servings.value = newValue
    }
    private fun onIsBeverageChange(newValue:Boolean) {
        _isBeverage.value = newValue
    }
    private fun onIsDessertChange(newValue:Boolean) {
        _isDessert.value = newValue
    }

    private fun deleteIngredientFromRecipe(deleteItem:IngredientWithRecipe){
        val newList = recipeIngredients - deleteItem
        recipeIngredients = newList
    }
    private fun onSingleRecipeIngredientsChange(newIngredient: IngredientWithRecipe) {
        val updatedList = recipeIngredients.map { existing ->
            if (newIngredient.ingredientId == existing.ingredientId) {
                val allowedUnits = allowedUnitsForIngredients.value[newIngredient.ingredientId]
                val gramsPerUnit = allowedUnits?.firstOrNull { it.first == newIngredient.unitOfMeasure }?.second ?: 1f
                val ingredientInGrams = newIngredient.originalQuantity * gramsPerUnit

                newIngredient.copy(
                    ingredientQuantity = ingredientInGrams
                )
            } else existing
        }

        recipeIngredients = updatedList

        loadAllowedUnitsForIngredient(newIngredient.ingredientId)
    }

    private fun addIngredientToRecipe(recipeId: Long, ingredientId: Long, originalQuantity: Float, unitOfMeasure: UnitOfMeasure) {
        val ingredientExists = recipeIngredients.any {
            it.recipeId == recipeId && it.ingredientId == ingredientId
        }
        if(!ingredientExists){
            val allowedUnits = allowedUnitsForIngredients.value[ingredientId]
            val gramsPerUnit = allowedUnits?.firstOrNull { it.first == unitOfMeasure }?.second ?: 1f
            val ingredientInGrams = originalQuantity * gramsPerUnit
            val newIngredientWithRecipe = IngredientWithRecipe(
                recipeId = recipeId,
                ingredientId = ingredientId,
                ingredientQuantity = ingredientInGrams,
                unitOfMeasure = unitOfMeasure,
                originalQuantity = originalQuantity
            )
            recipeIngredients = recipeIngredients + newIngredientWithRecipe
            loadAllowedUnitsForIngredient(ingredientId)
        }
    }
    private fun addIngredientToTempRecipe(ingredientId: Long, originalQuantity: Float, unitOfMeasure: UnitOfMeasure) {
        if (tempRecipe.id == 0L) {
            tempRecipe = tempRecipe.copy(id = generateTempId())
        }
        val ingredientExists = recipeIngredients.any {
            it.recipeId == tempRecipe.id && it.ingredientId == ingredientId
        }
        if(!ingredientExists){
            val allowedUnits = allowedUnitsForIngredients.value[ingredientId]
            val gramsPerUnit = allowedUnits?.firstOrNull { it.first == unitOfMeasure }?.second ?: 1f
            val ingredientInGrams = originalQuantity * gramsPerUnit
            val newIngredientWithRecipe = IngredientWithRecipe(
                recipeId = tempRecipe.id,
                ingredientId = ingredientId,
                ingredientQuantity = ingredientInGrams,
                unitOfMeasure = unitOfMeasure,
                originalQuantity = originalQuantity
            )
            recipeIngredients = recipeIngredients + newIngredientWithRecipe
            loadAllowedUnitsForIngredient(ingredientId)
        }
    }

    private fun addEmptyDescriptionStepToRecipe(recipeId: Long){
        val newRecipeDescriptionStep = RecipeDescription(
            recipeId = recipeId.toInt(),
            stepNr = getNextDescriptionStepNumber(),
            text = "Beschreibung...",
            englishText = "Description...",
            germanText = "Beschreibung...",
            imgUri = null
        )

        if(_recipeDescriptionSteps.value != null) {
            _recipeDescriptionSteps.value = _recipeDescriptionSteps.value!! + newRecipeDescriptionStep
        }
        else {
            _recipeDescriptionSteps.value = listOf(newRecipeDescriptionStep)
        }
    }

    private fun addEmptyDescriptionStepToTempRecipe(){
        if (tempRecipe.id == 0L) {
            tempRecipe = tempRecipe.copy(id = generateTempId())
        }
        val newRecipeDescriptionStep = RecipeDescription(
            recipeId = tempRecipe.id.toInt(),
            stepNr = getNextDescriptionStepNumber(),
            text = "Beschreibung...",
            englishText = "Description...",
            germanText = "Beschreibung...",
            imgUri = null
        )
        if(_recipeDescriptionSteps.value != null) {
            _recipeDescriptionSteps.value = _recipeDescriptionSteps.value!! + newRecipeDescriptionStep
        }
        else {
            _recipeDescriptionSteps.value = listOf(newRecipeDescriptionStep)
        }
    }

    private fun generateTempId(): Long {
        return System.currentTimeMillis()
    }
    fun getNextDescriptionStepNumber(): Int {
        return (_recipeDescriptionSteps.value?.size ?: 0) + 1
    }


    fun saveRecipeToDatabase(context: Context){
        viewModelScope.launch {
           val id =  saveRecipeToDatabaseAndReturnId(context)
            id?.let {
                currentRecipeId = id
            }

            if (id != null) {
                Toast.makeText(
                    context,
                    context.getString(R.string.recipe_created_sucessfully),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private suspend fun saveRecipeToDatabaseAndReturnId(context: Context): Long? {

        val valid = checkRecipeIsValid(context)
        if (!valid) return null

        return withContext(Dispatchers.IO) {
            val allIngredients = recipeIngredients.map { it.ingredientId }
            val ingredientsAreVegan = recipeUseCase.areAllIngredientsVeganUseCase(allIngredients)
            val ingredientsAreVegetarian = recipeUseCase.areAllIngredientsVegetarianUseCase(allIngredients)
            val newCPFratio = recipeUseCase.calcCPFratioUseCase(recipeIngredients)
            val newCaloriesPerServing = recipeUseCase.calculateTotalCaloriesForIngredientWithRecipeListUseCase(recipeIngredients) / _servings.value

            val recipeId = recipeUseCase.addRecipeUseCase(
                Recipe(
                    title = _recipeName.value.trim(),
                    isMeal = _isMeal.value,
                    isSnack = _isSnack.value,
                    isBreakfast = _isBreakfast.value,
                    isBeverage = _isBeverage.value,
                    imgUri = _imgUri.value,
                    isVegan = ingredientsAreVegan,
                    isVegetarian = ingredientsAreVegetarian,
                    createdBy = _createdBy.value,
                    servings = _servings.value,
                    cpfRatio = newCPFratio,
                    caloriesPerServing = newCaloriesPerServing,
                    isDessert = _isDessert.value
                )
            )

            if (recipeId != -1L) {
                val existingIngredients = recipeUseCase.getAllIngredientsForOneRecipeUseCase(recipeId).first()
                val ingredientsToDelete = existingIngredients.filter { existingIngredient ->
                    !recipeIngredients.any { it.ingredientId == existingIngredient.ingredientId }
                }

                ingredientsToDelete.forEach { ingredientToDelete ->
                    recipeUseCase.deleteIngredientFromRecipeUseCase(ingredientToDelete.recipeId, ingredientToDelete.ingredientId)
                }

                recipeIngredients.forEach { ingredientWithRecipe ->
                    val updatedIngredient = ingredientWithRecipe.copy(recipeId = recipeId)
                    recipeUseCase.addIngredientToRecipeUseCase(
                        recipeId = recipeId,
                        ingredientId = updatedIngredient.ingredientId,
                        quantity = updatedIngredient.ingredientQuantity,
                        unitOfMeasure = updatedIngredient.unitOfMeasure,
                        originalQuantity = updatedIngredient.originalQuantity
                    )
                }

                recipeDescriptionSteps.value?.let {
                    updateRecipeIdAndInsertSteps(recipeId)
                }
                _isDirty.value = false

                return@withContext recipeId  // Gibt die ID des neu angelegten Rezepts zurück
            }

            return@withContext null  // Falls das Rezept nicht erfolgreich erstellt wurde, gibt null zurück
        }
    }

    fun updateRecipeToDatabase(context: Context,recipeId: Long):Boolean {

        val valid = checkRecipeIsValid(context)
        if (!valid) return false

        viewModelScope.launch(Dispatchers.IO) {
            val allIngredients = recipeIngredients.map { it.ingredientId }
            val ingredientsAreVegan = recipeUseCase.areAllIngredientsVeganUseCase(allIngredients)
            val ingredientsAreVegetarian = recipeUseCase.areAllIngredientsVegetarianUseCase(allIngredients)
            val newCPFratio = recipeUseCase.calcCPFratioUseCase(recipeIngredients)
            val newCaloriesPerserving = recipeUseCase.calculateTotalCaloriesForIngredientWithRecipeListUseCase(recipeIngredients)/_servings.value

            if (recipeId != -1L) {
                val existingIngredients = recipeUseCase.getAllIngredientsForOneRecipeUseCase(recipeId).first()

                val ingredientsToDelete = existingIngredients.filter { existingIngredient ->
                    !recipeIngredients.any { it.ingredientId == existingIngredient.ingredientId }
                }
                recipeUseCase.updateRecipeMainTable(
                    recipeId,
                    _recipeName.value,
                    isMeal.value,
                    isBreakfast.value,
                    isSnack.value,
                    isBeverage.value,
                    isDessert.value,
                    ingredientsAreVegan,
                    ingredientsAreVegetarian,
                    _imgUri.value,
                    _createdBy.value,
                    _servings.value,
                    newCPFratio,
                    newCaloriesPerserving
                )

                ingredientsToDelete.forEach { ingredientToDelete ->
                    recipeUseCase.deleteIngredientFromRecipeUseCase(ingredientToDelete.recipeId, ingredientToDelete.ingredientId)
                }

                recipeIngredients.forEach { ingredientWithRecipe ->
                    val updatedIngredient = ingredientWithRecipe.copy(recipeId = recipeId)

                    recipeUseCase.addIngredientToRecipeUseCase(
                        recipeId = recipeId,
                        ingredientId = updatedIngredient.ingredientId,
                        quantity = updatedIngredient.ingredientQuantity,
                        unitOfMeasure = updatedIngredient.unitOfMeasure,
                        originalQuantity = updatedIngredient.originalQuantity
                    )
                }

                recipeDescriptionSteps.value?.let {
                    updateRecipeIdAndInsertSteps(recipeId)
                }
            }
        }
        _isDirty.value = false

        return true
    }
    fun updateRecipeIdAndInsertSteps(newRecipeId: Long) {
        viewModelScope.launch {
            try {
                val updatedSteps = _recipeDescriptionSteps.value?.map { step ->
                    step.copy(recipeId = newRecipeId.toInt())
                } ?: emptyList()
                // Lade alle bereits gespeicherten Schritte aus der Datenbank
                val existingSteps = recipeRepository.getRecipeDescriptionStepsByRecipeId(newRecipeId)


                val stepsToDelete = existingSteps.filter { existingStep ->
                    !updatedSteps.any { it.id == existingStep.id }
                }

                // Lösche die nicht mehr existierenden Schritte
                stepsToDelete.forEach { step ->
                    recipeRepository.deleteDescriptionStep(step.id)
                }

                // Nummerierung der Schritte aktualisieren
                val reindexedSteps = updatedSteps.mapIndexed { index, step ->
                    step.copy(stepNr = index + 1)
                }

                // Aktualisierte Liste im State speichern
                _recipeDescriptionSteps.value = reindexedSteps

                // Speichere die aktualisierten Schritte in der Datenbank
                if (reindexedSteps.isNotEmpty()) {
                    recipeRepository.insertRecipeDescriptionSteps(reindexedSteps)
                }

                _isDirty.value = false

            } catch (e: Exception) {
                println("Fehler beim Aktualisieren der Rezeptschritte: ${e.message}")
            }
        }
    }


    fun resetUIVariables() {
        tempRecipe = Recipe(caloriesPerServing = 222f)
        recipeIngredients = emptyList()
        _recipeName.value = EMPTY_STRING
        _recipeDescriptionSteps.value = null
        _isMeal.value = true
        _isSnack.value = false
        _isBreakfast.value = false
        _isBeverage.value = false
        _isDessert.value = false
        _servings.value = 1f
        _imgUri.value = null
        _recipeDescriptionSteps.value = null
    }

    private fun atLeastOneRecipeTypeTrue(): Boolean {
        return when {
            _isMeal.value -> true
            _isSnack.value -> true
            _isBreakfast.value -> true
            _isBeverage.value -> true
            _isDessert.value -> true   //TODO recipe type als ENUM wäre robuster und cleaner!!
            else -> false
        }
    }

    private fun checkRecipeIsValid(context: Context): Boolean {
        return when {
            recipeIngredients.isEmpty() -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.ingredient_missing_toast),
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
            recipeName.value.isBlank() -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.missing_name_toast),
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
            !atLeastOneRecipeTypeTrue() -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.missing_recipetype_toast),
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
            else -> true
        }
    }

    suspend fun deleteRecipeById(id: Long) {
        recipeUseCase.deleteRecipeByIdUseCase(id)
    }

    suspend fun publishRecipe(context: Context, recipeId: Long): PublishRecipeResult{
        _isPublishing.value = true

        return try {
            val recipe = recipeRepository.getRecipeById(recipeId).firstOrNull()
            if (recipe != null) {
                publishRecipeInternal(context, recipeId)
            } else {
                PublishRecipeResult.PostToServerFailed()
            }
        } catch (e: Exception) {
            PublishRecipeResult.UnknownError()
        } finally {
            _isPublishing.value = false
        }
    }


    private suspend fun publishRecipeInternal(context: Context, recipeId: Long): PublishRecipeResult {
        return withContext(Dispatchers.IO) {
             publishRecipeUseCase.execute(context, recipeId)
        }
    }
    fun onPublishClicked(context: Context) {
        viewModelScope.launch {
            val localRecipeId =  currentRecipeId
            val valid = checkRecipeIsValid(context)

            if (localRecipeId == null || !valid) {
                Toast.makeText(
                    context,
                    context.getString(R.string.recipe_invalid_cannot_publish),
                    Toast.LENGTH_SHORT
                ).show()
                return@launch
            }

            _isPublishing.value = true
            Log.i("DEBUG PUT","${_remoteId.value}")
            val result = try {
                if (_remoteId.value != null) {
                    updatePublishedRecipe(context, localRecipeId) // PUT
                } else {
                    publishRecipe(context, localRecipeId) // POST
                }
            } finally {
                _isPublishing.value = false
            }


            when (result) {
                is PublishRecipeResult.Success -> {
                    if (_remoteId.value != null) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.recipe_updated_successfully),
                            Toast.LENGTH_SHORT
                        ).show()
                        recipeRepository.setRemoteId(localRecipeId,result.remoteId)
                        resetBaselineToCurrent()
                    } else {
                        Toast.makeText(
                            context,
                            context.getString(R.string.dein_rezept_wurde_erfolgreich_ver_ffentlicht),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                is PublishRecipeResult.LoginRequired -> {
                    Toast.makeText(
                        context,
                        context.getString(R.string.login_required_toast),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is PublishRecipeResult.UserUnverified -> {
                    Toast.makeText(
                        context,
                        context.getString(R.string.user_unverified_toast),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is PublishRecipeResult.RecipeImageUploadFailed -> {
                    Log.w("PublishRecipe", "Recipe image upload failed, traceId=${result.traceId}")
                    Toast.makeText(
                        context,
                        context.getString(R.string.recipe_image_upload_failed_toast),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is PublishRecipeResult.DescriptionImagesUploadFailed -> {
                    Log.w("PublishRecipe", "Description images upload failed, traceId=${result.traceId}")
                    Toast.makeText(
                        context,
                        context.getString(R.string.description_images_upload_failed_toast),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is PublishRecipeResult.MissingImageMetadata -> {
                    // TODO: String-Resource anlegen
                    Toast.makeText(
                        context,
                        "Bilder-Metadaten fehlen. Bitte lade das Rezeptbild/Schrittbilder erneut hoch.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is PublishRecipeResult.ValidationFailed -> {
                    // Optional: details in die UI geben (z. B. StateFlow für Feldfehler)
                    // validationErrorsState.value = result.details
                    Log.w("PublishRecipe", "Validation failed, traceId=$${result.traceId}, details=$${result.details}")
                    // TODO: String-Resource anlegen
                    Toast.makeText(
                        context,
                        "Bitte prüfe deine Eingaben und korrigiere die markierten Felder.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is PublishRecipeResult.ImageTooLarge -> {
                    Log.w("PublishRecipe", "Image too large, traceId=${result.traceId}")
                    // TODO: String-Resource anlegen
                    Toast.makeText(
                        context,
                        "Das Bild ist zu groß. Bitte nutze ein kleineres Bild.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is PublishRecipeResult.ImageFormatNotSupported -> {
                    Log.w("PublishRecipe", "Unsupported image type, traceId=${result.traceId}")
                    // TODO: String-Resource anlegen
                    Toast.makeText(
                        context,
                        "Dieses Bildformat wird nicht unterstützt. Erlaubt sind JPG/PNG.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is PublishRecipeResult.Conflict -> {
                    // Z. B. wenn Rezept zwischenzeitlich geändert wurde
                    // TODO: Hier könntest du ein Dialog/Reload anbieten
                    // TODO: String-Resource anlegen
                    Toast.makeText(
                        context,
                        "Konflikt beim Veröffentlichen. Bitte aktualisiere das Rezept und versuche es erneut.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is PublishRecipeResult.PostToServerFailed -> {
                    Log.e("PublishRecipe", "Post to server failed, traceId=${result.traceId}")
                    Toast.makeText(
                        context,
                        context.getString(R.string.recipe_could_not_be_published_toast),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is PublishRecipeResult.UnknownError -> {
                    Log.e("PublishRecipe", "Unknown error, traceId=${result.traceId}")
                    Toast.makeText(
                        context,
                        context.getString(R.string.recipe_could_not_be_published_toast),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun updateStepText(stepId: String, newText: String) {
        _recipeDescriptionSteps.value = _recipeDescriptionSteps.value?.map { step ->
            if (step.id == stepId) step.copy(text = newText) else step
        }
    }
    private fun updateStepPicture(stepId: String, newUri: String) {
        _recipeDescriptionSteps.value = _recipeDescriptionSteps.value?.map { step ->
            if (step.id == stepId) step.copy(imgUri = newUri) else step
        }
    }
    private fun deleteRecipeStep(stepId: String) {
        _recipeDescriptionSteps.value = _recipeDescriptionSteps.value?.let { steps ->
            val updatedSteps = steps.filterNot { step -> step.id == stepId }

            updatedSteps.mapIndexed { index, step ->
                step.copy(stepNr = index + 1)
            }
        }
    }

private fun normalize(uri: String?): String? =
    uri?.takeIf { it.isNotBlank() }

private fun computeUpdateChanges(recipeId: Long): UpdateRecipeUseCase.UpdateChanges {
// Rezeptbild: unchanged / delete / replace
    val oldRecipe = originalRecipeImgUri
    val newRecipe = _imgUri.value
    val recipeAction = when {
        (oldRecipe == null && newRecipe == null) -> UpdateRecipeUseCase.ImageAction.Unchanged
        (oldRecipe != null && newRecipe == null) -> UpdateRecipeUseCase.ImageAction.Delete
        (oldRecipe == null && newRecipe != null) -> UpdateRecipeUseCase.ImageAction.Replace
        (oldRecipe != null && newRecipe != null && oldRecipe != newRecipe) -> UpdateRecipeUseCase.ImageAction.Replace
        else -> UpdateRecipeUseCase.ImageAction.Unchanged
    }// Schritte: Array<String?> mit 20 Slots (Index = stepNr - 1)
    val slots = Array<String?>(20) { "" }
    val currentSteps = _recipeDescriptionSteps.value.orEmpty()

    currentSteps.forEach { step ->
        val old = normalize(originalStepImgById[step.id])
        val new = normalize(step.imgUri)
        val code = when {
            old == null && new == null -> ""               // unchanged
            old != null && new == null -> "-"              // delete
            old == null && new != null -> "REPLACE"        // wird später in Code umgewandelt
            old != null && new != null && old != new -> "REPLACE"
            else -> ""                                      // unchanged
        }
        val idx = (step.stepNr - 1).coerceIn(0, slots.lastIndex)
        slots[idx] = code
    }

// “REPLACE” Platzhalter so lassen; der UseCase ersetzt sie durch echte Codes

    return UpdateRecipeUseCase.UpdateChanges(
        recipeImage = recipeAction,
        stepCodesArray = slots
    )
}

    private suspend fun updatePublishedRecipe(context: Context, recipeId: Long): PublishRecipeResult {
// Achtung: Body kommt aus DB → Stelle sicher, dass vorher gespeichert wurde
        val changes = computeUpdateChanges(recipeId)// REPLACE-Marker in echte Codes umwandeln, damit UpdateRecipeUseCase nur noch ausführt?
        // Falls ihr die Code-Generierung lieber hier habt:
        fun mkCode(): String = UUID.randomUUID().toString() + "###" + recipeId.toString()

        val finalSlots = changes.stepCodesArray.map { slot ->
            when (slot) {
                "REPLACE" -> mkCode()
                else -> slot
            }
        }.toTypedArray()

        val finalChanges = UpdateRecipeUseCase.UpdateChanges(
            recipeImage = changes.recipeImage,
            stepCodesArray = finalSlots
        )

        return updateRecipeUseCase.execute(context, recipeId, finalChanges)
    }

    private fun resetBaselineToCurrent() {
        originalRecipeImgUri = _imgUri.value
        originalStepImgById = _recipeDescriptionSteps.value
            ?.associate { it.id to it.imgUri }
            ?: emptyMap()
        _isDirty.value = false
    }
    private fun recomputeUpdatePermission() {
        viewModelScope.launch {
            val id = currentRecipeId ?: return@launch
            val perm = updateRecipeUseCase.checkUpdatePermission(id)
            _canEditRemote.value = (perm is UpdateRecipeUseCase.UpdatePermission.Allowed)
        }
    }


}