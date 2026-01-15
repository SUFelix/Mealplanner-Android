package com.felix.mealplanner20.Views.Recipes

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.felix.mealplanner20.Meals.Data.IngredientWithRecipe
import com.felix.mealplanner20.Meals.Data.RecipeDescription
import com.felix.mealplanner20.Meals.Data.helpers.deleteInternalImageIfExists
import com.felix.mealplanner20.Meals.Data.helpers.importCroppedImageToInternal
import com.felix.mealplanner20.R
import com.felix.mealplanner20.Screen
import com.felix.mealplanner20.ViewModels.AddEditRecipeViewModel
import com.felix.mealplanner20.ViewModels.IngredientViewModel
import com.felix.mealplanner20.ViewModels.MainViewModel
import com.felix.mealplanner20.ViewModels.SettingsViewModel
import com.felix.mealplanner20.Views.Components.MyCircularProgressIndicator
import com.felix.mealplanner20.Views.Components.CustomAlertDialog
import com.felix.mealplanner20.Views.Components.CustomButton
import com.felix.mealplanner20.Views.Components.CustomFullWidthButton
import com.felix.mealplanner20.Views.Components.SwipeableItemWithActions
import com.felix.mealplanner20.Views.ProfileSettingsLogin.ToggleButton
import com.felix.mealplanner20.ui.theme.Lime600
import com.felix.mealplanner20.ui.theme.Slate200
import com.felix.mealplanner20.ui.theme.Slate300
import com.felix.mealplanner20.ui.theme.Slate500
import com.felix.mealplanner20.ui.theme.Slate950
import com.felix.mealplanner20.ui.theme.TomatoRed
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.Locale
import java.util.UUID

@Composable
fun AddEditRecipeView(
    recipeId: Long,
    addEditRecipeViewModel: AddEditRecipeViewModel,
    navController: NavController,
    mainViewModel: MainViewModel
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val changesMade by mainViewModel.changesMade.collectAsState()
    val isDirty by addEditRecipeViewModel.isDirty
    val remoteRecipeId by addEditRecipeViewModel.remoteRecipeId


    val allowedUnitsMap by addEditRecipeViewModel.allowedUnitsForIngredients.collectAsState()

    val publishRequirementHasIngredient by addEditRecipeViewModel.publishRequirementHasIngredient.collectAsState()
    val publishRequirementTypeSelected by addEditRecipeViewModel.publishRequirementTypeSelected.collectAsState()
    val publishRequirementNameNotEmpty by addEditRecipeViewModel.publishRequirementNameNotEmpty.collectAsState()
    val publishRequirementHasImage by addEditRecipeViewModel.publishRequirementHasImage.collectAsState()
    val publishRequirementHasDescription by addEditRecipeViewModel.publishRequirementHasDescription.collectAsState()
    val publishRequirementNoUnsavedChanges by addEditRecipeViewModel.publishRequirementNoUnsavedChanges.collectAsState()

    val canEditRemote by addEditRecipeViewModel.canEditRemote.collectAsState()

    BackHandler(enabled = true) {
        if(changesMade) {
            mainViewModel.setShowExitWithoutSaveAlertDialog(true)
        }
        else{
            navController.navigateUp()
        }
    }




    val imageCropLauncher = rememberLauncherForActivityResult(
        contract = CropImageContract(),
        onResult = { result ->
            if (result.isSuccessful) {
                result.uriContent?.let { croppedUri ->
                    try {
// Optional: altes internes Bild löschen
                        context.deleteInternalImageIfExists(addEditRecipeViewModel.imgUri.value)
// Neues Bild intern speichern
                        val stored = context.importCroppedImageToInternal(croppedUri, "recipe_images")
                        addEditRecipeViewModel.changeAnything(
                            newImgUri = stored,
                            additionalAction = { mainViewModel.setChangesMade(true) }
                        )
                    } catch (e: Exception) {
                        Toast.makeText(context, "Couldn't import image", Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    }
                }
            } else {
                println("ImageCropping error: ${result.error}")
            }
        }
    )



    val photoPickerAvailable = remember {
        androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.isPhotoPickerAvailable(context)
    }

    val pickRecipeImage = rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            imageCropLauncher.launch(
                CropImageContractOptions(
                    it,
                    CropImageOptions(
                        imageSourceIncludeCamera = false,
                        cropShape = com.canhub.cropper.CropImageView.CropShape.RECTANGLE,
                        aspectRatioX = 1,
                        aspectRatioY = 1,
                        fixAspectRatio = true,
                        outputCompressFormat = Bitmap.CompressFormat.JPEG,
                        outputRequestHeight = 1080,
                        outputRequestWidth = 1080,
                        outputCompressQuality = 85
                    )
                )
            )
        }
    }

    val openRecipeImage = rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
// Persistentes Leserecht für den Cropper (nur im Fallback nötig)
            context.contentResolver.takePersistableUriPermission(
                it,
                android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            imageCropLauncher.launch(
                CropImageContractOptions(
                    it,
                    CropImageOptions(
                        imageSourceIncludeCamera = false,
                        cropShape = com.canhub.cropper.CropImageView.CropShape.RECTANGLE,
                        aspectRatioX = 1,
                        aspectRatioY = 1,
                        fixAspectRatio = true,
                        outputCompressFormat = Bitmap.CompressFormat.JPEG,
                        outputRequestHeight = 1080,
                        outputRequestWidth = 1080,
                        outputCompressQuality = 85
                    )
                )
            )
        }
    }



    val isLoading by addEditRecipeViewModel.isLoading.collectAsState()
    val isPublishing by addEditRecipeViewModel.isPublishing.collectAsState()
    if (isLoading) {
            MyCircularProgressIndicator()
    }
    else if(isPublishing){
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
                .wrapContentSize(Alignment.Center)
        ) {
            Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                MyCircularProgressIndicator()
                Spacer(Modifier.height(12.dp))
                Text(
                    style = MaterialTheme.typography.titleMedium,
                    text = "Publishing your recipe..."
                )
            }
        }
    }
    else{
    mainViewModel.setCurrentTopAppBarTitle(addEditRecipeViewModel.recipeName.value)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Slate200)
            .border(BorderStroke(1.dp, Slate300))
    ) {
        Column( modifier = Modifier
            .background(Slate200)
        ){
            if(mainViewModel.showExitWithoutSaveAlertDialog.value){
                CustomAlertDialog(
                    title = stringResource(R.string.confirm_exit_without_saving),
                    text  = stringResource(R.string.do_you_really_want_to_go_back_your_changes_will_not_be_saved),
                    onConfirm = {
                        navController.navigateUp()
                        mainViewModel.setShowExitWithoutSaveAlertDialog(false)
                    },
                    onDismiss = { mainViewModel.setShowExitWithoutSaveAlertDialog(false) }
                )
            }

            RecipeBlock1(
                recipeTitle = addEditRecipeViewModel.recipeName.value,
                onValueChange = {
                    addEditRecipeViewModel.changeAnything(
                        newName = it,
                        additionalAction = {mainViewModel.setChangesMade(true)}
                    )
                },
                imgUri = addEditRecipeViewModel.imgUri.value,
                onImageClick = {
                    if (photoPickerAvailable) {
                        pickRecipeImage.launch(
                            androidx.activity.result.PickVisualMediaRequest(
                                androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    } else {
                        openRecipeImage.launch(arrayOf("image/*"))
                    }
                },
                mainViewModel = mainViewModel,
                addEditRecipeViewModel = addEditRecipeViewModel
            )
            RecipeBlock2(
                addEditRecipeViewModel = addEditRecipeViewModel,
                mainViewModel = mainViewModel
            )

            Box {
                CustomFullWidthButton(
                    text = stringResource(R.string.add_ingredient),
                    onClick = {
                        navController.navigate(
                            Screen.AddIngredientToRecipeScreen(context).passId(recipeId)
                        )
                    },
                    textColor = Color.White,
                    buttonColor = Lime600
                )
            }

            RecipeBlock3(
                addEditRecipeViewModel = addEditRecipeViewModel,
                mainViewModel = mainViewModel
            )

            Box {
                CustomFullWidthButton(
                    text = stringResource(R.string.add_description_step),
                    onClick = {
                        val currentSteps = addEditRecipeViewModel.recipeDescriptionSteps.value ?: emptyList()
                        val newStepNr = currentSteps.size + 1

                        Log.d("DEBUG","INSIDE BUTTON CLICK")

                        addEditRecipeViewModel.changeAnything(
                            addNewDescriptionStep = true,
                            additionalAction = { }
                        )
                    },
                    textColor = Color.White,
                    buttonColor = Lime600
                )
            }

            RecipeBlock4(
                addEditRecipeViewModel = addEditRecipeViewModel,
                mainViewModel = mainViewModel
            )

            PublishRequirementsChecklist(
                listOf(
                    publishRequirementTypeSelected.copy(label = stringResource(R.string.at_least_one_type)),
                    publishRequirementHasIngredient.copy(label = stringResource(R.string.has_at_least_one_ingredient)),
                    publishRequirementNameNotEmpty.copy(label = stringResource(R.string.title_not_empty))),
                listOf(
                    publishRequirementHasImage.copy(label = stringResource(R.string.recipe_has_image)),
                    publishRequirementHasDescription.copy(label = stringResource(R.string.recipe_has_at_least_one_step)),
                    publishRequirementNoUnsavedChanges.copy(label = stringResource(R.string.recipe_has_no_unsaved_changes)),
                    PublishRequirement(id = "not_from_remote",label = stringResource(R.string.not_from_remote),isMet = canEditRemote || remoteRecipeId==null)
                )
            )

            Box(
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(top = 8.dp)
            ) {
                Column {

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {

                        CustomButton(
                            text = stringResource(id = R.string.delete),
                            onClick = {
                                scope.launch {
                                    try {
                                        addEditRecipeViewModel.deleteRecipeById(recipeId)
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.recipe_deleted_successfully),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } catch (e: Exception) {
                                        Log.e("Debug", "Fehler beim Löschen des Rezepts", e)
                                    } finally {
                                        delay(15)
                                        navController.navigateUp()
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                        )

                        CustomButton(
                            text = if (recipeId != 0L) {
                                stringResource(R.string.update_recipe)
                            } else {
                                stringResource(R.string.add_recipe)
                            },
                            onClick = {
                                    if (recipeId != 0L) {
                                        addEditRecipeViewModel.updateRecipeToDatabase(context,recipeId)
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.recipe_saved_successfully_toast),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        addEditRecipeViewModel.saveRecipeToDatabase(context)
                                        navController.navigateUp()
                                    }
                                mainViewModel.setChangesMade(false)
                            },
                            modifier = Modifier
                                .weight(1.3f)
                                .height(50.dp)
                                .padding(horizontal = 16.dp),
                            buttonColor = Lime600,
                            textColor = Color.White,
                            borderColor = Slate950,
                            textStyle = MaterialTheme.typography.titleMedium.copy(lineHeight = 16.sp)
                        )


                        CustomButton(
                            text = stringResource(R.string.publish),
                            enabled = (!isDirty && (canEditRemote||remoteRecipeId==null)) ,//TODO überprüfung ob remote recipe id dem user gehört
                            onClick = {
                                scope.launch {
                                    try {
                                        addEditRecipeViewModel.onPublishClicked(context)
                                    } catch (e: Exception) {
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                        )
                    }
                }
            }
        }
    }
}
}

@Composable
fun RecipeBlock1(
    recipeTitle: String?,
    onValueChange: (String) -> Unit,
    imgUri: Uri?,
    onImageClick: () -> Unit,
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    addEditRecipeViewModel: AddEditRecipeViewModel,
    mainViewModel: MainViewModel
){
    Column(
        modifier = Modifier
            .padding(16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(4.dp)
                .background(
                    color = Color.White
                )
                .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp))

        ){
            EditableImg(
                imgUri = imgUri,
                onImageClick = onImageClick,
                fallbackDrawableId = R.drawable.baseline_add_photo_alternate_24,
                modifier = if (imgUri == null) {
                    Modifier.size(64.dp)
                } else {
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                }
            )

        }

        EditableStringSetting(
            text = stringResource(R.string.title),
            value = recipeTitle?:"default title",
            onValueChange = {onValueChange(it)}
        )
        EditableCounterSetting(
            addEditRecipeViewModel = addEditRecipeViewModel,
            mainViewModel = mainViewModel,
            text = stringResource(R.string.servings) +": ",
            isLast = true
        )
    }
}

@Composable
fun RecipeBlock2(
    addEditRecipeViewModel: AddEditRecipeViewModel,
    mainViewModel: MainViewModel
){
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .heightIn(max = 4000.dp)


        )  {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 12.dp,  // Oben links abgerundet
                            topEnd = 12.dp,    // Oben rechts abgerundet
                            bottomStart = 0.dp, // Unten eckig
                            bottomEnd = 0.dp   // Unten eckig
                        )
                    )
            ){
                Text(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterVertically),
                    text = stringResource(R.string.ingredients),
                    style = MaterialTheme.typography.bodyMedium)
            }
            LazyColumn {
                items(
                    items = addEditRecipeViewModel.recipeIngredients,
                    key = { ingredientWithRecipe -> ingredientWithRecipe.ingredientId }
                ) {
                    SwipeableItemWithActions(
                        isRevealed = false,
                        actions = {
                            IconButton(
                                modifier = Modifier
                                    .background(TomatoRed)
                                    .size(84.dp),
                                onClick = {
                                    addEditRecipeViewModel.changeAnything(
                                        ingredientToDelete = it,
                                        additionalAction = {mainViewModel.setChangesMade(true)}
                                    )
                                },
                                content = {
                                    Icon(
                                        painter =  painterResource(R.drawable.muelleimer_icon),
                                        tint = Color.White,
                                        contentDescription = "delete icon"
                                ) }
                            )
                        }
                    ) {
                        IngredientWithQuantityListItem(it, addEditRecipeViewModel, mainViewModel = mainViewModel)
                    }
                }
            }
        }
}

@Composable
fun RecipeBlock3(
    addEditRecipeViewModel: AddEditRecipeViewModel,
    mainViewModel: MainViewModel
){
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .heightIn(max = 4000.dp)
    )  {
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
        ){
            Text(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterVertically),
                text = stringResource(R.string.description),
                style = MaterialTheme.typography.bodyMedium)
        }

        addEditRecipeViewModel.recipeDescriptionSteps.value?.let { recipeDescriptionStep ->
            LazyColumn {
                items(
                    items = recipeDescriptionStep,
                    key = { recipeDescriptionStep -> recipeDescriptionStep.id }
                ) { step ->

                    val context = LocalContext.current

                    val myCropOptions = CropImageOptions(
                        imageSourceIncludeCamera = false,
                        cropShape = com.canhub.cropper.CropImageView.CropShape.RECTANGLE,
                        aspectRatioX = 1,
                        aspectRatioY = 1,
                        fixAspectRatio = true,
                        outputRequestWidth = 1080,
                        outputRequestHeight = 1080,
                        outputCompressFormat = Bitmap.CompressFormat.JPEG,
                        outputCompressQuality = 85
                    )

                    val cropLauncher = rememberRecipeStepImageCropLauncher(
                        context = context,
                        stepId = step.id,
                        currentImageUri = step.imgUri,
                        viewModel = addEditRecipeViewModel,
                        mainViewModel = mainViewModel
                    )

                    // System Photo Picker verfügbar?
                    val photoPickerAvailable = remember {
                        androidx.activity.result.contract.ActivityResultContracts
                            .PickVisualMedia
                            .isPhotoPickerAvailable(context)
                    }
                    val pickStepImage = rememberLauncherForActivityResult(
                        androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia()
                    ) { uri ->
                        uri?.let {
                            cropLauncher.launch(
                                CropImageContractOptions(
                                    it,
                                    myCropOptions
                                )
                            )
                        }
                    }
                    // Launcher: OpenDocument-Fallback
                    val openStepImage = rememberLauncherForActivityResult(
                        androidx.activity.result.contract.ActivityResultContracts.OpenDocument()
                    ) { uri ->
                        uri?.let {
                            // Persistentes Leserecht nur im Fallback nötig
                            runCatching {
                                context.contentResolver.takePersistableUriPermission(
                                    it, Intent.FLAG_GRANT_READ_URI_PERMISSION
                                )
                            }
                            cropLauncher.launch(
                                com.canhub.cropper.CropImageContractOptions(
                                    it,
                                    com.canhub.cropper.CropImageOptions()
                                )
                            )
                        }
                    }

                    RecipeDescriptionStep(
                        step = step,
                        onTextChange = { newText ->
                            val pair = Pair(step.id,newText)
                            addEditRecipeViewModel.changeAnything(
                                newDescriptionStepText = pair,
                                additionalAction = { mainViewModel.setChangesMade(true) }
                            )
                        },
                        onImageClick = {
                            if (photoPickerAvailable) {
                                pickStepImage.launch(
                                    androidx.activity.result.PickVisualMediaRequest(
                                        androidx.activity.result.contract.ActivityResultContracts
                                            .PickVisualMedia.ImageOnly
                                    )
                                )
                            } else {
                                openStepImage.launch(arrayOf("image/*"))
                            }
                        },
                        onDeleteClick = {
                            addEditRecipeViewModel.changeAnything(
                                deleteStepNr = step.id,
                                additionalAction = { mainViewModel.setChangesMade(true) }
                                )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun RecipeBlock4(
    addEditRecipeViewModel: AddEditRecipeViewModel,
    mainViewModel: MainViewModel
){
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
    )  {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 12.dp,  // Oben links abgerundet
                        topEnd = 12.dp,    // Oben rechts abgerundet
                        bottomStart = 0.dp, // Unten eckig
                        bottomEnd = 0.dp   // Unten eckig
                    )
                )
        ){
            Text(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterVertically),
                text = stringResource(R.string.meal_type),
                style = MaterialTheme.typography.bodyMedium)
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(BorderStroke(1.dp, Slate200))
        ){
            Column {

                val isMeal =  addEditRecipeViewModel.isMeal.collectAsState()
                val isBreakfast =  addEditRecipeViewModel.isBreakfast.collectAsState()
                val isSnack =  addEditRecipeViewModel.isSnack.collectAsState()
                val isDessert =  addEditRecipeViewModel.isDessert.collectAsState()
                val isBeverage =  addEditRecipeViewModel.isBeverage.collectAsState()

            CustomBooleanSetting(
                text = stringResource(R.string.meal),
                value = isMeal.value,
                onValueChange = { addEditRecipeViewModel.changeAnything(
                    newIsMeal = it,
                    additionalAction = {mainViewModel.setChangesMade(true)}
                ) }
            )
            CustomBooleanSetting(
                text = stringResource(R.string.breakfast),
                value = isBreakfast.value,
                onValueChange = { addEditRecipeViewModel.changeAnything(
                    newIsBreakfast = it,
                    additionalAction = {mainViewModel.setChangesMade(true)}
                ) }
            )
            CustomBooleanSetting(
                text = stringResource(R.string.snack),
                value = isSnack.value,
                onValueChange = { addEditRecipeViewModel.changeAnything(
                    newIsSnack = it,
                    additionalAction = {mainViewModel.setChangesMade(true)}
                ) }
            )
            CustomBooleanSetting(
                text = stringResource(R.string.beverage),
                value = isBeverage.value,
                onValueChange = { addEditRecipeViewModel.changeAnything(
                    newIsBeverage = it,
                    additionalAction = {mainViewModel.setChangesMade(true)}
                ) }
            )
            CustomBooleanSetting(
                text = stringResource(R.string.dessert),
                value = isDessert.value,
                onValueChange = { addEditRecipeViewModel.changeAnything(
                    newIsDessert = it,
                    additionalAction = {mainViewModel.setChangesMade(true)}
                ) },
                isLast = true
            )
            }
        }
    }
}

@Composable
fun CustomBooleanSetting(
    text: String,
    value: Boolean = false,
    onValueChange: (Boolean) -> Unit,
    isLast: Boolean = false
) {
    val cornerShape = RoundedCornerShape(
        topStart = 0.dp,
        topEnd = 0.dp,
        bottomStart =if(isLast) 12.dp else 0.dp,
        bottomEnd = if (isLast)12.dp else 0.dp
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = cornerShape,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Slate200)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = Slate500
            )
            Spacer(modifier = Modifier.weight(1f))
            ToggleButton(
                isChecked = value,
                onToggle = {
                    onValueChange(it)
                }
            )
        }
    }
}

@Composable
fun EditableCounterSetting(
    addEditRecipeViewModel: AddEditRecipeViewModel,
    mainViewModel: MainViewModel,
    text: String,
    isLast: Boolean = false
) {
    val cornerShape = RoundedCornerShape(
        topStart = 0.dp,
        topEnd = 0.dp,
        bottomStart = if (isLast) 12.dp else 0.dp,
        bottomEnd = if (isLast) 12.dp else 0.dp
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp),
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(modifier  =Modifier.weight(1f)){
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate500
                    )
                }
                Box(modifier  =Modifier.weight(1f)){
                    Counter(addEditRecipeViewModel = addEditRecipeViewModel, mainViewModel = mainViewModel)

                }
            }
        }
    }
}

@Composable
fun EditableStringSetting(
    text: String,
    value: String,
    onValueChange: (String) -> Unit,
    isLast: Boolean = false
) {
    val cornerShape = RoundedCornerShape(
        topStart = 0.dp,
        topEnd = 0.dp,
        bottomStart =if(isLast) 12.dp else 0.dp,
        bottomEnd = if (isLast)12.dp else 0.dp
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = cornerShape,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Slate200)
    ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier =
                Modifier.weight(1f)
                ){
                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = text,
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate500
                    )
                }



                Box(
                    modifier = Modifier.weight(4f),
                    contentAlignment = Alignment.Center
                ){
                    RecipeTitleTextField(value, onValueChange)
                }
            }
    }
}

@Composable
fun RecipeTitleTextField(
    recipeTitle: String?,
    onValueChange:(String)->Unit
){
    OutlinedTextField(
        value = recipeTitle ?: "",
        onValueChange = onValueChange,
        singleLine = false,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(12.dp),
        textStyle = MaterialTheme.typography.bodySmall,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Slate200,
            focusedContainerColor = Slate200,
            cursorColor = Slate950,
            focusedTextColor = Slate950,
            unfocusedTextColor = Slate500,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun EditableImg(
    modifier: Modifier = Modifier,
    imgUri: Uri? = null,
    imgData:ByteArray? = null,
    onImageClick: () -> Unit = {},
    fallbackDrawableId:Int
) {

    Box(
        modifier = modifier
            .then(
                Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .clickable { onImageClick() }
            )
    ){
        if(imgData != null){
            val bitmap = BitmapFactory.decodeByteArray(imgData, 0, imgData.size)
            Image(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White),
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Selectable image",
                contentScale = ContentScale.Crop
            )
        }
        else if (imgUri != null) {
            Image(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White),
                painter = rememberAsyncImagePainter(model = imgUri),
                contentDescription = "Selectable image",
                contentScale = ContentScale.Crop
            )
            
        } else {
            Image(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White),
                painter = painterResource(id = fallbackDrawableId),
                contentDescription = "Fallback image",
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun IngredientWithQuantityListItem(
    ingredientWithRecipe: IngredientWithRecipe?,
    recipeViewModel: AddEditRecipeViewModel,
    mainViewModel: MainViewModel,
    isLast: Boolean = false
){
    val isGerman = Locale.getDefault().language == "de"
    ingredientWithRecipe?.let{ IngredientWithRecipe ->
        val ingredientViewModel: IngredientViewModel = hiltViewModel()
        val ingredientFlowState = mutableStateOf(ingredientViewModel.getIngredientById(IngredientWithRecipe.ingredientId))
        val ingredientName = remember { mutableStateOf("") }


        LaunchedEffect(ingredientFlowState) {
            ingredientFlowState.value.collect { ingredient ->
                ingredientName.value = if(isGerman) ingredient.germanName else ingredient.englishName?:ingredient.germanName
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
                .height(84.dp),
            shape = cornerShape,
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            border = BorderStroke(1.dp, Slate200)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ){
                Box(modifier = Modifier.weight(1f)){
                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = ingredientName.value,
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate500
                    )
                }
                Box(modifier = Modifier.weight(1f)){
                    QuantityPartInListView(ingredientWithRecipe,recipeViewModel, mainViewModel           )
                }
            }
        }
    }
}

@Composable
fun Counter(
    initialValue: Int = 1,
    minValue: Int = 1,
    maxValue: Int = Int.MAX_VALUE,
    addEditRecipeViewModel: AddEditRecipeViewModel,
    mainViewModel: MainViewModel
) {
    var counterValue by remember { mutableStateOf(addEditRecipeViewModel.servings.value.toInt()) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            //.padding(16.dp)
    ) {
        FloatingActionButton(
            modifier = Modifier.size(44.dp),
            shape = CircleShape,
            onClick = {  if (counterValue > minValue){
            counterValue--
            addEditRecipeViewModel.changeAnything(
                newServings = counterValue.toFloat(),
                additionalAction = {mainViewModel.setChangesMade(true)}
            )
            }
            },
            containerColor = Slate200,
            contentColor = Slate950

        ) {
            Icon(
                painterResource(id = R.drawable.baseline_remove_24),
                contentDescription = "Decrement",
                tint = Slate950
            )
        }

        Text(
            text = counterValue.toString(),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .width(50.dp),
            textAlign = TextAlign.Center
        )

        FloatingActionButton(
            modifier = Modifier.size(44.dp),
            shape = CircleShape,
            containerColor = Slate200,
            contentColor = Slate950,
            onClick = {
                if (counterValue < maxValue){
                counterValue++
                    addEditRecipeViewModel.changeAnything(
                        newServings = counterValue.toFloat(),
                        additionalAction = {mainViewModel.setChangesMade(true)}
                    )
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


@Composable
fun QuantityPartInListView(
    ingredientWithRecipe: IngredientWithRecipe,
    recipeViewModel:AddEditRecipeViewModel,
    mainViewModel: MainViewModel
) {
    var text by remember { mutableStateOf(ingredientWithRecipe.originalQuantity.toString()) }
    var expanded by remember { mutableStateOf(false) }
    val allowedUnits = recipeViewModel.allowedUnitsForIngredients.collectAsState().value[ingredientWithRecipe.ingredientId] ?: emptyList()
    var selectedUnit by remember { mutableStateOf(ingredientWithRecipe.unitOfMeasure) }

    Row{
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .weight(0.8f)
                .padding(vertical = 12.dp)
                .clip(RoundedCornerShape(8.dp)),
            value = text,
            onValueChange = {
                text = it
                text.toFloatOrNull()?.let { newQuantity ->
                    val newIngredientWithRecipe = ingredientWithRecipe.copy(originalQuantity = newQuantity)
                    recipeViewModel.changeAnything(
                        ingredientToUpdate = newIngredientWithRecipe,
                        additionalAction = { mainViewModel.setChangesMade(true) }
                    )
                }
            },
            textStyle = MaterialTheme.typography.bodySmall,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Slate200,
                focusedContainerColor = Slate200,
                cursorColor = Slate950,
                focusedTextColor = Slate950,
                unfocusedTextColor = Slate500,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .padding(12.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Slate200)
                .clickable { expanded = true }
                .padding(horizontal = 4.dp, vertical = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier.wrapContentWidth(),
                    contentAlignment = Alignment.Center // Text zentrieren
                ) {
                    Text(
                        text = selectedUnit.toUOMshortcutString5(LocalContext.current),
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate950,maxLines = 1,
                        overflow = TextOverflow.Ellipsis

                    )
                }
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown Arrow",
                    tint = Slate950,
                    modifier = Modifier.rotate(if (expanded) 180f else 0f)
                )
            }


            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                allowedUnits.forEach { unit ->
                    DropdownMenuItem(
                        onClick = {
                            selectedUnit = unit.first
                            expanded = false
                            val newIngredientWithRecipe = ingredientWithRecipe.copy(unitOfMeasure = unit.first)
                            recipeViewModel.changeAnything(
                                ingredientToUpdate = newIngredientWithRecipe,
                                additionalAction = { mainViewModel.setChangesMade(true) }
                            )
                        },
                        text = {
                            Text(text = unit.first.toUOMshortcutString5(LocalContext.current))
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDeleteContainerIngredientWithRecipe(
    ingredientWithRecipe: IngredientWithRecipe,
    onDelete: (IngredientWithRecipe) -> Unit,
    animationDuration: Int = 500,
    content: @Composable (IngredientWithRecipe) -> Unit
) {
    val addEditRecipeViewModel: AddEditRecipeViewModel = hiltViewModel()
    val removedItems by addEditRecipeViewModel.removedItems.collectAsState()

    val ingredientId = ingredientWithRecipe.ingredientId
    var isRemoved by remember {
        mutableStateOf(removedItems.contains(ingredientId))
    }

    val state = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                //isRemoved = true
                true
            } else {
                false
            }
        },
        positionalThreshold = { totalDistance -> totalDistance * 0.75f }
    )


    LaunchedEffect(key1 = isRemoved) {
        if (isRemoved) {
            delay(animationDuration.toLong())
            onDelete(ingredientWithRecipe)
        }
    }

    AnimatedVisibility(
        visible = !isRemoved,
        exit = shrinkVertically(
            animationSpec = tween(durationMillis = animationDuration),
            shrinkTowards = Alignment.Top
        ) + fadeOut()
    ) {
        SwipeToDismissBox(
            state = state,
            backgroundContent = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.errorContainer),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Löschen",
                        tint = Color.White,
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clickable {
                                // Der Nutzer hat explizit das Icon angeklickt – setze isRemoved.
                                isRemoved = true
                            }
                    )
                }
            },
            content = { content(ingredientWithRecipe) },
            enableDismissFromEndToStart = true,
            enableDismissFromStartToEnd = false
        )
    }
}
@Composable
fun RecipeDescriptionStep(
    step: RecipeDescription,
    onTextChange: (String) -> Unit,
    onImageClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(BorderStroke(1.dp, Slate200))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.step, step.stepNr),
                style = MaterialTheme.typography.bodyMedium,
                color = Slate950
            )

            IconButton(
                onClick = {
                    onDeleteClick()
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.muelleimer_icon),
                    contentDescription = "Delete Step",
                    tint = Slate500
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (step.imgUri == null) 48.dp else Dp.Unspecified),
            contentAlignment = Alignment.Center
        ){
            EditableImg(
                imgUri = step.imgUri?.toUri(),
                onImageClick = { onImageClick() }, // Öffnet den Cropper
                fallbackDrawableId = R.drawable.baseline_add_photo_alternate_24,
                modifier = if(step.imgUri != null){
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                } else{
                    Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))

                }
            )
        }




        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = step.text,
            onValueChange = onTextChange,
            placeholder = {
                Text(
                    text = stringResource(R.string.enter_recipe_description),
                    color = Slate500,
                    style = MaterialTheme.typography.bodySmall
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp)),
            textStyle = MaterialTheme.typography.bodySmall,
            maxLines = 30,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Slate200,
                focusedContainerColor = Slate200,
                cursorColor = Slate950,
                focusedTextColor = Slate950,
                unfocusedTextColor = Slate500,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )
    }
}

@Composable
fun rememberRecipeStepImageCropLauncher(
    context: Context,
    stepId: String,
    currentImageUri: String?, // neu: zum Aufräumen
    viewModel: AddEditRecipeViewModel,
    mainViewModel: MainViewModel
): ManagedActivityResultLauncher<CropImageContractOptions, CropImageView.CropResult> {
    return rememberLauncherForActivityResult(
        contract = CropImageContract(),
        onResult = { result ->
            if (result.isSuccessful) {
                result.uriContent?.let { croppedUri ->
                    try {
// alte interne Datei (falls vorhanden) löschen
                        context.deleteInternalImageIfExists(currentImageUri?.let { Uri.parse(it) })
// neue Datei intern speichern
                        val stored = context.importCroppedImageToInternal(croppedUri, "recipe_step_images")
                        val stepUriPair = Pair(stepId, stored.toString())
                        viewModel.changeAnything(
                            newDescriptionStepUri = stepUriPair,
                            additionalAction = { mainViewModel.setChangesMade(true) }
                        )
                    } catch (e: Exception) {
                        Log.e("RecipeStepCrop", "Import failed", e)
                        Toast.makeText(context, "Couldn't import image", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Log.e("RecipeStepCrop", "Crop error", result.error)
            }
        }
    )
}

@Composable
fun PublishRequirementsChecklist(
    saveRequirements: List<PublishRequirement>,
    publishRequirements: List<PublishRequirement>,
    modifier: Modifier = Modifier,
    title1: String = stringResource(R.string.requirements_for_saving),
    title2: String = stringResource(R.string.requirements_for_publishing),
    metColor: Color = Lime600,    // grüner Ton
    unmetColor: Color = Color(0xFFB00020),  // roter Ton
    onRequirementClick: ((PublishRequirement) -> Unit)? = null
) {
    val saveMetCount = remember(saveRequirements) { saveRequirements.count { it.isMet } }
    val publishMetCount = remember(publishRequirements) { publishRequirements.count { it.isMet } }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFFE6E6E6))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)) {
                    Text(
                        text = title1,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${saveMetCount}/${saveRequirements.size} "+stringResource(R.string.requirement_met),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }


        Column {
            saveRequirements.forEach { req ->
                val targetColor = if (req.isMet) metColor else unmetColor
                val animatedColor by animateColorAsState(targetValue = targetColor)
                val icon = if (req.isMet) Icons.Default.CheckCircle else Icons.Default.Place
                val scale by animateFloatAsState(if (req.isMet) 1.05f else 1f)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(enabled = onRequirementClick != null) {
                            onRequirementClick?.invoke(req)
                        }
                        .padding(horizontal = 4.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        if (req.isMet) stringResource(R.string.requirement_met) else stringResource(
                            R.string.requirement_not_met),
                        tint = animatedColor,
                        modifier = Modifier
                            .size(20.dp)
                            .graphicsLayer { scaleX = scale; scaleY = scale }
                    )

                    Spacer(modifier = Modifier.width(10.dp))
                    Box(modifier = Modifier.weight(1f)){
                    Text(
                        text = req.label,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (req.isMet) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.8f
                        ),
                        maxLines = 2
                    )}

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = if (req.isMet) "OK" else stringResource(R.string.missing),
                        style = MaterialTheme.typography.labelSmall,
                        color = animatedColor,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Clip,
                        modifier = Modifier
                            .defaultMinSize(minWidth = 56.dp)   // anpassen nach der längsten Übersetzung
                            .border(
                                BorderStroke(1.dp, animatedColor.copy(alpha = 0.35f)),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                }
            }
        }
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title2,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${publishMetCount}/${publishRequirements.size} "+stringResource(R.string.requirement_met),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }
        Column {
            publishRequirements.forEach { req ->
                val targetColor = if (req.isMet) metColor else unmetColor
                val animatedColor by animateColorAsState(targetValue = targetColor)
                val icon = if (req.isMet) Icons.Default.CheckCircle else Icons.Default.Place
                val scale by animateFloatAsState(if (req.isMet) 1.05f else 1f)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(enabled = onRequirementClick != null) {
                            onRequirementClick?.invoke(req)
                        }
                        .padding(horizontal = 4.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = if (req.isMet) stringResource(R.string.requirement_met) else stringResource(
                            R.string.requirement_not_met
                        ),
                        tint = animatedColor,
                        modifier = Modifier
                            .size(20.dp)
                            .graphicsLayer { scaleX = scale; scaleY = scale }
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Box(modifier = Modifier.weight(1f)) {
                        Text(
                            text = req.label,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (req.isMet) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = if (req.isMet) "OK" else stringResource(R.string.missing),
                        style = MaterialTheme.typography.labelSmall,
                        color = animatedColor,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Clip,
                        modifier = Modifier
                            .defaultMinSize(minWidth = 56.dp)   // anpassen nach der längsten Übersetzung
                            .border(
                                BorderStroke(1.dp, animatedColor.copy(alpha = 0.35f)),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}
data class PublishRequirement(
    val id: String,
    val label: String,
    val isMet: Boolean
)


