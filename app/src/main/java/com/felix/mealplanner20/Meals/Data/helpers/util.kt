package com.felix.mealplanner20.Meals.Data.helpers

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import com.felix.mealplanner20.Meals.Data.Ingredient
import java.io.File


fun getAdjustedQuantity(ingredient: Ingredient, quantity: Float): Float {
    return when (ingredient.unitOfMeasure) {
        UnitOfMeasure.GRAM, UnitOfMeasure.MILLILITER -> quantity / 100
        else -> quantity
    }
}
/*
@Composable
@ExperimentalMaterial3Api
fun PullToRefreshBox(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    state: PullToRefreshState = rememberPullToRefreshState(),
    contentAlignment: Alignment = Alignment.TopStart,
    indicator: @Composable BoxScope.() -> Unit = {
        Indicator(
            modifier = Modifier.align(Alignment.TopCenter),
            isRefreshing = isRefreshing,
            state = state
        )
    },
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier.pullToRefresh(
            state = state,
            isRefreshing = isRefreshing,
            onRefresh = onRefresh
        ),
        contentAlignment = contentAlignment
    ) {
        content()
        indicator()
    }
}*/

fun Context.importCroppedImageToInternal(uri: Uri, subDir: String): Uri {
    val dir = File(filesDir, subDir).apply { mkdirs() }
    val out = File(dir, "img_${System.currentTimeMillis()}.jpg")
    contentResolver.openInputStream(uri).use { input ->
        out.outputStream().use { output ->
            requireNotNull(input) { "InputStream null for $uri" }
            input.copyTo(output)
        }
    }
    return out.toUri()
}

fun Context.deleteInternalImageIfExists(uri: Uri?) {
    if (uri == null) return
    if (uri.scheme == "file") {
        runCatching { File(uri.path ?: "").takeIf { it.exists() }?.delete() }
    }
}