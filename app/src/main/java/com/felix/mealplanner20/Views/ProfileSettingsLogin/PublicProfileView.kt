package com.felix.mealplanner20.Views.ProfileSettingsLogin

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.felix.mealplanner20.R
import com.felix.mealplanner20.Screen
import com.felix.mealplanner20.ViewModels.PublicProfileViewModel
import com.felix.mealplanner20.Views.Components.MyCircularProgressIndicator
import com.felix.mealplanner20.Views.Recipes.BigRecipeCardItem
import com.felix.mealplanner20.Views.Recipes.EditableImg
import com.felix.mealplanner20.caching.ImageUrlBuilder
import com.felix.mealplanner20.di.BASE_URL
import com.felix.mealplanner20.ui.theme.Slate500

@Composable
fun PublicProfileView(
    username: String,
    navController: NavController,
    publicProfileViewModel: PublicProfileViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(username) {
        publicProfileViewModel.loadProfile(username)
    }

    val displayUsername by publicProfileViewModel.username.collectAsState()
    val pictureUri by publicProfileViewModel.pictureUri.collectAsState()
    val description by publicProfileViewModel.description.collectAsState()
    val recipeCount by publicProfileViewModel.recipeCount.collectAsState()
    val isProfileLoading by publicProfileViewModel.isProfileLoading.collectAsState()
    val profileNotFound by publicProfileViewModel.profileNotFound.collectAsState()

    val recipes by publicProfileViewModel.recipes.collectAsState()
    val isLoading by publicProfileViewModel.isLoading.collectAsState()
    val showOriginalTitle by publicProfileViewModel.showOriginalTitle.collectAsState(false)

    if (isProfileLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            MyCircularProgressIndicator()
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        item {
            PublicProfileHeader(
                username = displayUsername,
                pictureUri = pictureUri,
                description = description,
                recipeCount = recipeCount
            )
        }

        if (profileNotFound) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.public_profile_not_found),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Slate500,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    MyCircularProgressIndicator()
                }
            }
        } else {
            if (recipes.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.public_profile_no_recipes),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Slate500,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(
                    items = recipes,
                    key = { recipe -> "${recipe.id}" }
                ) { recipe ->
                    BigRecipeCardItem(
                        recipe = recipe,
                        showOriginalTitle = showOriginalTitle,
                        onClick = {
                            navController.navigate(Screen.DiscoverRecipesSingleViewScreen(context = context).passId(recipe.id))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PublicProfileHeader(
    username: String,
    pictureUri: String?,
    description: String,
    recipeCount: Int
) {
    val imageUrl = remember(pictureUri, username) {
        if (pictureUri.isNullOrBlank()) null else ImageUrlBuilder.profile(BASE_URL, username, verify = false, expiresSeconds = 900)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
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
                    .height(140.dp),
                contentAlignment = Alignment.TopStart
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(88.dp)
                        .clip(shape = RoundedCornerShape(8.dp))
                        .background(Color.Black)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .align(Alignment.BottomCenter)
                        .background(Color.White)
                )

                EditableImg(
                    modifier = Modifier
                        .offset(y = (+36).dp)
                        .size(80.dp)
                        .clip(CircleShape)
                        .border(3.dp, Color.White, CircleShape)
                        .align(Alignment.Center),
                    imgUri = imageUrl?.let { Uri.parse(it) },
                    onImageClick = {},
                    fallbackDrawableId = R.drawable.baseline_account_circle_24
                )
            }

            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = username,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = description.ifBlank { stringResource(R.string.no_profile_description) },
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate500,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(Modifier.height(8.dp))
                val recipeCountText = LocalContext.current.resources.getQuantityString(
                    R.plurals.public_profile_recipe_count, recipeCount, recipeCount
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = recipeCountText,
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate500
                    )
                }
            }
        }
    }
}
