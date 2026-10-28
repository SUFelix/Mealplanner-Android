package com.felix.mealplanner20.Views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.felix.mealplanner20.Meals.Data.DTO.IngredientDTO
import com.felix.mealplanner20.Meals.Data.DTO.IngredientMatchReviewDTO
import com.felix.mealplanner20.R
import com.felix.mealplanner20.ViewModels.FoodAdminAllowedUnitViewModel
import com.felix.mealplanner20.ViewModels.FoodAdminMatchReviewViewModel
import com.felix.mealplanner20.ViewModels.FoodAdminReviewViewModel
import com.felix.mealplanner20.ViewModels.PendingAllowedUnitUi
import com.felix.mealplanner20.Views.Components.CustomButton
import com.felix.mealplanner20.ui.theme.Lime600
import com.felix.mealplanner20.ui.theme.Slate500
import java.util.Locale

@Composable
fun FoodAdminReviewView(
    foodAdminReviewViewModel: FoodAdminReviewViewModel,
    foodAdminAllowedUnitViewModel: FoodAdminAllowedUnitViewModel,
    foodAdminMatchReviewViewModel: FoodAdminMatchReviewViewModel
) {
    var selectedTabIndex by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTabIndex) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = { Text(stringResource(R.string.pending_ingredients_tab)) }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = { Text(stringResource(R.string.pending_units_tab)) }
            )
            Tab(
                selected = selectedTabIndex == 2,
                onClick = { selectedTabIndex = 2 },
                text = { Text(stringResource(R.string.pending_matches_tab)) }
            )
        }
        when (selectedTabIndex) {
            0 -> PendingIngredientsTab(foodAdminReviewViewModel)
            1 -> PendingAllowedUnitsTab(foodAdminAllowedUnitViewModel)
            2 -> PendingMatchesTab(foodAdminMatchReviewViewModel)
        }
    }
}

@Composable
fun PendingIngredientsTab(
    foodAdminReviewViewModel: FoodAdminReviewViewModel
) {
    val pendingIngredients by foodAdminReviewViewModel.pendingIngredients.collectAsState()
    val isLoading by foodAdminReviewViewModel.isLoading.collectAsState()
    val processingId by foodAdminReviewViewModel.processingId.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        if (isLoading && pendingIngredients.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center)) {
                CircularProgressIndicator()
            }
        } else if (pendingIngredients.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center)) {
                Text(
                    text = stringResource(R.string.no_pending_ingredients),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Slate500
                )
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(items = pendingIngredients, key = { it.id }) { ingredient ->
                    PendingIngredientItem(
                        ingredient = ingredient,
                        isProcessing = processingId == ingredient.id,
                        onApprove = { foodAdminReviewViewModel.approve(ingredient.id) },
                        onReject = { foodAdminReviewViewModel.reject(ingredient.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun PendingIngredientItem(
    ingredient: IngredientDTO,
    isProcessing: Boolean,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    val isGerman = Locale.getDefault().language == "de"
    val displayName = if (isGerman) ingredient.germanName else ingredient.englishName ?: ingredient.germanName

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = displayName, style = MaterialTheme.typography.titleMedium)
                if (ingredient.aiEstimated) {
                    Text(
                        text = stringResource(R.string.ai_estimated),
                        style = MaterialTheme.typography.labelSmall,
                        color = Lime600
                    )
                }
            }
            Text(
                text = ingredient.dgeType,
                style = MaterialTheme.typography.labelSmall,
                color = Slate500
            )
            Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    LabeledValueBox(stringResource(R.string.calories), ingredient.calories)
                }
                Box(modifier = Modifier.weight(1f)) {
                    LabeledValueBox(stringResource(R.string.fat), ingredient.fat)
                }
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.weight(1f)) {
                    LabeledValueBox(stringResource(R.string.carbs), ingredient.carbs)
                }
                Box(modifier = Modifier.weight(1f)) {
                    LabeledValueBox(stringResource(R.string.protein), ingredient.protein)
                }
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.weight(1f)) {
                    LabeledValueBox(stringResource(R.string.saturated_fat), ingredient.saturatedFat)
                }
                Box(modifier = Modifier.weight(1f)) {
                    LabeledValueBox(stringResource(R.string.sugar), ingredient.sugar)
                }
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.weight(1f)) {
                    LabeledValueBox(stringResource(R.string.fibre), ingredient.fibre)
                }
                Box(modifier = Modifier.weight(1f)) {
                    LabeledValueBox(stringResource(R.string.alcohol), ingredient.alcohol)
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CustomButton(
                    text = stringResource(R.string.reject),
                    onClick = onReject,
                    buttonColor = Color.White,
                    textColor = Color.Black,
                    enabled = !isProcessing
                )
                CustomButton(
                    text = stringResource(R.string.approve),
                    onClick = onApprove,
                    buttonColor = Lime600,
                    textColor = Color.White,
                    enabled = !isProcessing
                )
            }
        }
    }
}

@Composable
fun PendingAllowedUnitsTab(
    foodAdminAllowedUnitViewModel: FoodAdminAllowedUnitViewModel
) {
    val pendingUnits by foodAdminAllowedUnitViewModel.pendingAllowedUnits.collectAsState()
    val isLoading by foodAdminAllowedUnitViewModel.isLoading.collectAsState()
    val processingKey by foodAdminAllowedUnitViewModel.processingKey.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        if (isLoading && pendingUnits.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center)) {
                CircularProgressIndicator()
            }
        } else if (pendingUnits.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center)) {
                Text(
                    text = stringResource(R.string.no_pending_units),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Slate500
                )
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(
                    items = pendingUnits,
                    key = { foodAdminAllowedUnitViewModel.key(it.ingredientId, it.unitOfMeasure) }
                ) { unit ->
                    PendingAllowedUnitItem(
                        unit = unit,
                        isProcessing = processingKey == foodAdminAllowedUnitViewModel.key(unit.ingredientId, unit.unitOfMeasure),
                        onApprove = { foodAdminAllowedUnitViewModel.approve(unit.ingredientId, unit.unitOfMeasure) },
                        onReject = { foodAdminAllowedUnitViewModel.reject(unit.ingredientId, unit.unitOfMeasure) }
                    )
                }
            }
        }
    }
}

@Composable
fun PendingMatchesTab(
    foodAdminMatchReviewViewModel: FoodAdminMatchReviewViewModel
) {
    val pendingMatches by foodAdminMatchReviewViewModel.pendingMatches.collectAsState()
    val isLoading by foodAdminMatchReviewViewModel.isLoading.collectAsState()
    val processingTaskId by foodAdminMatchReviewViewModel.processingTaskId.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        if (isLoading && pendingMatches.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center)) {
                CircularProgressIndicator()
            }
        } else if (pendingMatches.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center)) {
                Text(
                    text = stringResource(R.string.no_pending_matches),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Slate500
                )
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(items = pendingMatches, key = { it.taskId }) { match ->
                    PendingMatchItem(
                        match = match,
                        isProcessing = processingTaskId == match.taskId,
                        onConfirm = { foodAdminMatchReviewViewModel.confirm(match.taskId) },
                        onReject = { foodAdminMatchReviewViewModel.reject(match.taskId) }
                    )
                }
            }
        }
    }
}

@Composable
fun PendingMatchItem(
    match: IngredientMatchReviewDTO,
    isProcessing: Boolean,
    onConfirm: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = match.extractedName, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = String.format(Locale.getDefault(), "%.0f%%", match.confidence * 100),
                    style = MaterialTheme.typography.labelSmall,
                    color = Lime600
                )
            }
            match.originalText?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate500
                )
            }
            match.recipeTitle?.let {
                Text(
                    text = stringResource(R.string.match_recipe_prefix, it),
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate500,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            if (match.matches.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.match_suggested_label),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
                match.matches.forEach { suggestion ->
                    Text(
                        text = "• ${suggestion.matchedText} (#${suggestion.ingredientId})",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            match.reasoning?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate500,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CustomButton(
                    text = stringResource(R.string.match_reject),
                    onClick = onReject,
                    buttonColor = Color.White,
                    textColor = Color.Black,
                    enabled = !isProcessing
                )
                CustomButton(
                    text = stringResource(R.string.match_confirm),
                    onClick = onConfirm,
                    buttonColor = Lime600,
                    textColor = Color.White,
                    enabled = !isProcessing
                )
            }
        }
    }
}

@Composable
fun PendingAllowedUnitItem(
    unit: PendingAllowedUnitUi,
    isProcessing: Boolean,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    val isGerman = Locale.getDefault().language == "de"
    val displayName = if (isGerman) unit.germanName else unit.englishName ?: unit.germanName

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = displayName, style = MaterialTheme.typography.titleMedium)
            Text(
                text = unit.unitOfMeasure,
                style = MaterialTheme.typography.labelSmall,
                color = Slate500
            )
            Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    LabeledValueBox(stringResource(R.string.grams_per_unit), unit.gramsPerUnit)
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CustomButton(
                    text = stringResource(R.string.reject),
                    onClick = onReject,
                    buttonColor = Color.White,
                    textColor = Color.Black,
                    enabled = !isProcessing
                )
                CustomButton(
                    text = stringResource(R.string.approve),
                    onClick = onApprove,
                    buttonColor = Lime600,
                    textColor = Color.White,
                    enabled = !isProcessing
                )
            }
        }
    }
}
