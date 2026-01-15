package com.felix.mealplanner20.Views.ProfileSettingsLogin

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.felix.mealplanner20.Meals.Data.EMPTY_STRING
import com.felix.mealplanner20.Meals.Data.helpers.MORE_VERT_CONTENT_DESCRIPTION
import com.felix.mealplanner20.R
import com.felix.mealplanner20.ViewModels.ProfileViewModel
import com.felix.mealplanner20.ViewModels.SettingsViewModel
import com.felix.mealplanner20.ViewModels.SignInViewModel
import com.felix.mealplanner20.Views.Components.CustomAlertDialog
import com.felix.mealplanner20.Views.Components.CustomButton
import com.felix.mealplanner20.Views.Components.CustomFullWidthButton
import com.felix.mealplanner20.Views.Recipes.CustomBooleanSetting
import com.felix.mealplanner20.Views.Recipes.EditableImg
import com.felix.mealplanner20.Views.ScreenWidthInDp
import com.felix.mealplanner20.auth.AuthUiEvent
import com.felix.mealplanner20.ui.theme.Lime600
import com.felix.mealplanner20.ui.theme.Slate200
import com.felix.mealplanner20.ui.theme.Slate300
import com.felix.mealplanner20.ui.theme.Slate500
import com.felix.mealplanner20.ui.theme.Slate950
import com.mealplanner20.jwtauthktorandroid.auth.AuthResult
import java.io.File
import java.io.FileOutputStream
import java.util.UUID


@Composable
fun ProfileView(
    signInViewModel: SignInViewModel,
    profileViewModel: ProfileViewModel,
    settingsViewModel: SettingsViewModel,
    onLogoutSuccess: () -> Unit,
    onSignUpClick:()->Unit,
    onSignInClick:()->Unit,
    onAdvancedSettingsClick:()->Unit,
    onDreiPunkteClick:() -> Unit
    ) {

    val p  = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.primary)
            .padding(top = p)
            //.testTag(PROFILE_TEST_TAG)
    ) {
        YourProfileView(
            signInViewModel = signInViewModel,
            profileViewModel = profileViewModel,
            settingsViewModel = settingsViewModel,

            onEditProfileClick = {
                profileViewModel.toggleIsEditingWithDescriptionPost()
            },
            onSignUpClick = {
                    onSignUpClick()
            },
            onSignInClick = {
                    onSignInClick()
            },
            OnMoreVertCklick = {
                onDreiPunkteClick()
            }
        )
        Settings(
            settingsViewModel
        )
        CustomFullWidthButton(
            onClick = { onAdvancedSettingsClick() },
            text  = stringResource(R.string.advanced_settings),
            buttonColor= Lime600,
            textColor = Color.White,
            borderColor = Lime600
        )
    }
}

@Composable
fun YourProfileView(
    signInViewModel: SignInViewModel,
    profileViewModel: ProfileViewModel,
    settingsViewModel: SettingsViewModel,
    onEditProfileClick: () -> Unit,
    onSignUpClick:()->Unit,
    onSignInClick:()->Unit,
    OnMoreVertCklick:()->Unit
) {

    val authState = signInViewModel.authResults.collectAsState(AuthResult.Unauthorized())
    val profileDescription = profileViewModel.description.collectAsState()
    val username = profileViewModel.username.value?: EMPTY_STRING
    val userRole = profileViewModel.userrole.value?: EMPTY_STRING

    val cardHeight = if (authState.value is AuthResult.Authorized) 336.dp else 262.dp
    val isEditing = profileViewModel.isEditing.collectAsState()

    val context = LocalContext.current
    var selectedImageUri by remember {
        mutableStateOf<Uri?>( profileViewModel.profilePictureUri)
    }

    LaunchedEffect(authState.value) {
        Log.i("YourProfile","authState: ${authState.value}")
        if (authState.value is AuthResult.Authorized) {
            profileViewModel.loadProfile()
        } else {
            //profileViewModel.clearProfileIfNeeded() // optional: clear on logout
        }
    }

    val imageCropLauncher = rememberLauncherForActivityResult(
        contract = CropImageContract(),
        onResult = { result ->
            if (result.isSuccessful) {
                result.uriContent?.let { uri ->
                    uri?.let { it ->
                        val imgBytes = context.contentResolver.openInputStream(uri)?.use {
                            it.readBytes()
                        }
                        imgBytes?.let { byteArray ->
                            val originalBitmap =
                                BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                            val scaledBitmap =
                                Bitmap.createScaledBitmap(originalBitmap, 1080, 1080, true)

                            val imagesFolder = File(context.filesDir, "recipe_images")
                            if (!imagesFolder.exists()) {
                                imagesFolder.mkdir()
                            }

                            val file = File(imagesFolder, "${UUID.randomUUID()}.jpg")
                            FileOutputStream(file).use { fos ->
                                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos)
                            }
                            val newUri:Uri = file.toUri()
                            selectedImageUri = newUri
                            profileViewModel.updateLocalProfilePictureAndUpload(context,newUri)
                            settingsViewModel.saveSettings()
                            //TODO profileviewmodel put new uri oder settings ? profil als entity?
                        }
                        //TODO wie lösche ich die Files aus der App wenn die Bilder nicht mehr verwendet werden?
                    }
                }
            } else {
                println("ImageCropping error: ${result.error}")
            }
        }
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            //.height(cardHeight),
            .wrapContentHeight(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(188.dp),
                contentAlignment = Alignment.TopStart
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(128.dp)
                        .clip(shape = RoundedCornerShape(8.dp))
                        .background(Color.Black)

                ){
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(
                            text = stringResource(R.string.profile),
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )
                        IconButton(
                            onClick = {
                                OnMoreVertCklick()
                            },
                            content = {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = MORE_VERT_CONTENT_DESCRIPTION,
                                    tint = Color.White
                                )
                            }
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .align(Alignment.BottomCenter)
                        .background(Color.White)
                )

                if(authState.value is AuthResult.Authorized && profileViewModel.image!=null){
                    EditableImg(
                        modifier = Modifier
                            .offset(y = (+48).dp)
                            .size(96.dp)
                            .clip(CircleShape)
                            .border(4.dp, Color.White, CircleShape)
                            .align(Alignment.Center),
                        imgData = profileViewModel.image,
                        onImageClick = {
                            imageCropLauncher.launch(
                                CropImageContractOptions(
                                    null,
                                    CropImageOptions(
                                        imageSourceIncludeCamera = false,
                                        cropShape = CropImageView.CropShape.OVAL,
                                        aspectRatioX = 1,
                                        aspectRatioY = 1,
                                        fixAspectRatio = true,
                                        outputRequestHeight = 1080,
                                        outputRequestWidth = 1080,
                                        outputCompressQuality = 85
                                    )
                                )
                            )
                        },
                        fallbackDrawableId = profileViewModel.defaultImage
                    )
                }
                else{
                    EditableImg(
                        modifier = Modifier
                            .offset(y = (+48).dp)
                            .size(96.dp)
                            .clip(CircleShape)
                            .border(4.dp, Color.White, CircleShape)
                            .align(Alignment.Center),
                        imgUri = null,
                        onImageClick = {
                            imageCropLauncher.launch(
                                CropImageContractOptions(
                                    null,
                                    CropImageOptions(
                                        imageSourceIncludeCamera = false,
                                        cropShape = CropImageView.CropShape.OVAL,
                                        aspectRatioX = 1,
                                        aspectRatioY = 1,
                                        fixAspectRatio = true,
                                        outputRequestHeight = 1080,
                                        outputRequestWidth = 1080,
                                        outputCompressQuality = 85
                                    )
                                )
                            )
                        },
                        fallbackDrawableId = profileViewModel.defaultImage
                    )
                }

            }
            if (authState.value is AuthResult.Authorized){
                Column(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .background(Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(34.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(
                            text = username,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(20.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(
                            text = userRole,
                            fontSize = 14.sp,
                            style = MaterialTheme.typography.titleMedium,
                            color = Lime600
                        )
                        Text(
                            text = profileViewModel.email,
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate500
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.CenterHorizontally),
                            contentAlignment = Alignment.Center
                        ) {
                            if(!isEditing.value) {
                                    Text(
                                        text = profileDescription.value,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Slate500,
                                        textAlign = TextAlign.Center,
                                    )
                            }
                            else{
                                TextField(
                                    value = profileDescription.value,
                                    onValueChange = {
                                        profileViewModel.changeDescription(it)
                                    },
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .fillMaxWidth(),
                                    textStyle = MaterialTheme.typography.bodySmall,
                                    maxLines = 8,
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
                            // profileViewModel.updateDescription("TEST DESCRIPTION")
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                        ,
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CustomButton(
                            text = stringResource(R.string.logout),
                            onClick = {
                                signInViewModel.onEvent(AuthUiEvent.Logout)
                            },
                            buttonColor = Color.White,
                            textColor = Color.Black,
                            borderColor = Slate300
                        )
                        CustomButton(
                            text = if (isEditing.value) stringResource(R.string.save_profile) else stringResource(R.string.edit_profile),
                            onClick = {
                                onEditProfileClick()
                            },
                            buttonColor = Lime600,
                            textColor = Color.White,
                            borderColor = Slate950
                        )
                    }
                }
            }
            else{
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                ){
                    //Spacer(Modifier.height(24.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom,

                    ) {
                        CustomButton(
                            text = stringResource(R.string.sign_in),
                            onClick = {
                                onSignInClick()
                            },
                            buttonColor = Color.White,
                            textColor = Color.Black,
                            borderColor = Slate300
                        )
                        CustomButton(
                            text = stringResource(R.string.sign_up),
                            onClick = {
                                onSignUpClick()
                            },
                            buttonColor = Lime600,
                            textColor = Color.White,
                            borderColor = Slate950
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Settings(
    settingsViewModel: SettingsViewModel,
){
    DisposableEffect(Unit) {
        onDispose {
            settingsViewModel.saveSettings()
        }
    }
    Box(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .padding(12.dp)
    ){
        val showResetSettingsAlertDialog = settingsViewModel.showResetSettingsAlertDialog.collectAsState()
        if (showResetSettingsAlertDialog.value) {
            CustomAlertDialog(
                onConfirm = {
                    settingsViewModel.resetSettings()
                    settingsViewModel.toggleResetSettingsDialog(false)
                },
                onDismiss = { settingsViewModel.toggleResetSettingsDialog(false) }
            )
        }

        Column{
            SettingsBlock1(settingsViewModel)
            Spacer(modifier = Modifier.height(16.dp))
            SettingsBlock2(settingsViewModel)
            Spacer(modifier = Modifier.height(16.dp))
            SettingsBlock3(settingsViewModel)
        }
    }
}

@Composable
fun SettingsBlock1( settingsViewModel: SettingsViewModel){
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)

    ) {
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
                text = stringResource(R.string.mealplan_settings),
                style = MaterialTheme.typography.bodyMedium)
        }
        IntSetting(
            text = stringResource(R.string.planning_horizon),
            value = settingsViewModel.planningHorizonInDays,
            onValueChange = {settingsViewModel.updatePlanningHorizonInDays(it)},
            valueRange = 1..7
        )
        IntSetting(
            text = stringResource(R.string.meals_per_day),
            value = settingsViewModel.mealsPerDay,
            onValueChange = {settingsViewModel.onMealssperDayValueChange(it)})
        IntSetting(
            text = stringResource(R.string.breakfasts_per_day),
            value = settingsViewModel.breakfastsPerDay,
            onValueChange = {settingsViewModel.onBreakfastsperDayValueChange(it)})
        IntSetting(
            text = stringResource(R.string.snacks_per_day),
            value = settingsViewModel.snacksPerDay,
            onValueChange = {settingsViewModel.onSnacksperDayValueChange(it)},
            isLast = true)
    }
}

@Composable
fun SettingsBlock2( settingsViewModel: SettingsViewModel){
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)

    ) {
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
                text = stringResource(R.string.nutrition_settings),
                style = MaterialTheme.typography.bodyMedium)
        }
        IntSetting(
            text = stringResource(R.string.calories_per_day),
            value = settingsViewModel.calorieRequirement,
            onValueChange = {settingsViewModel.updateCalorieRequirement(it)},
            valueRange = 0..5000
        )
        /*IntSetting(
            text = stringResource(R.string.protein_per_day),
            value = settingsViewModel.proteinRequirement,
            onValueChange = {settingsViewModel.updateProteinRequirement(it)},
            valueRange = 0..300
        )
        IntSetting(
            text = stringResource(R.string.fat_per_day),
            value = settingsViewModel.fatRequirement,
            onValueChange = {settingsViewModel.updateFatRequirement(it)},
            valueRange = 0..300,
            isLast = true
        )*/
    }
}

@Composable
fun SettingsBlock3( settingsViewModel: SettingsViewModel){
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)

    ) {
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
                text = stringResource(R.string.general_settings),
                style = MaterialTheme.typography.bodyMedium)
        }

        CustomBooleanSetting(
            text = "Show original Titles",
            value = settingsViewModel.showOriginalTitle,
            onValueChange = {settingsViewModel.updateShowOriginalTitle(it)},
            isLast = true
        )
    }
}

@Composable
fun ToggleButton(
    isChecked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    val toggleColor = if (isChecked) Slate950 else Slate200// Farbe ändern
    val thumbOffset by animateDpAsState(if (isChecked) 22.dp else 2.dp, label = "Thumb Animation")

    Box(
        modifier = Modifier
            .width(40.dp)
            .height(20.dp)
            .clip(RoundedCornerShape(10.dp)) // Abgerundete Form
            .background(toggleColor)
            .clickable { onToggle(!isChecked) }
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .offset(x = thumbOffset, y = 2.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(1.dp, Color.Gray, CircleShape)
        )
    }
}


@Composable
fun IntSetting(
    text: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    valueRange: IntRange = 0..5,
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
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = Slate500
            )
            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = value.toString(),
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.weight(1f))

                BoxWithConstraints(
                    modifier = Modifier.width((ScreenWidthInDp() / 2.5f).dp)
                ) {
                    val trackHeight = 6.dp
                    val thumbSize = 18.dp

                    val stepsCount = (valueRange.last - valueRange.first).coerceAtLeast(1)
                    val sliderPosition =
                        ((value - valueRange.first).toFloat() / stepsCount.toFloat())
                            .coerceIn(0f, 1f)

                    val widthDp = maxWidth
                    val thumbX = sliderPosition * (widthDp - thumbSize)

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
                            .fillMaxWidth(1f - sliderPosition)
                            .align(Alignment.CenterEnd)
                            .height(trackHeight)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Slate200)
                    )

                    Box(
                        modifier = Modifier
                            .offset(x = thumbX, y = 12.dp)
                            .size(18.dp)
                            .background(Lime600, shape = CircleShape)
                            .border(2.dp, Color.White, shape = CircleShape)
                    )

                    Slider(
                        value = value.toFloat(),
                        onValueChange = { onValueChange(it.toInt()) },
                        valueRange = valueRange.first.toFloat()..valueRange.last.toFloat(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .alpha(0f),
                        colors = SliderDefaults.colors(
                            thumbColor = Color.Transparent,
                            activeTrackColor = Color.Transparent,
                            inactiveTrackColor = Color.Transparent,
                            activeTickColor = Color.Transparent,
                            inactiveTickColor = Color.Transparent
                        )
                    )
                }
            }
        }
    }
}





@Composable
fun ResetSettingsAlertDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Bestätige Zurücksetzen") },
        text = { Text("Möchtest du wirklich alle Einstellungen zurücksetzen?") },
        confirmButton = {
            TextButton(onClick = {
                onConfirm()
                onDismiss()
            }) {
                Text("Bestätigen")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Abbrechen")
            }
        }
    )
}

