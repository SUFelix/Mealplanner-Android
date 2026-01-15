package com.felix.mealplanner20.Meals.Data.helpers

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import com.felix.mealplanner20.R
import kotlinx.serialization.Serializable


@Serializable
enum class UnitOfMeasure {
    GRAM,
    MILLILITER,
    PIECE,
    LITER,
    CUP,
    TABLESPOON,
    TEASPOON,
    CLOVE;
    fun toUOMshoppingListshortcut(context: Context): String {
        return when (this) {
            GRAM -> "g"
            MILLILITER -> "ml"
            PIECE -> " "
            LITER -> " L"
            CUP -> context.getString(R.string.cup)
            TABLESPOON -> context.getString(R.string.tbsp)
            TEASPOON -> context.getString(R.string.teasp)
            CLOVE-> context.getString(R.string.clove)
        }
    }
    fun toUOMshortcutString5(context: Context): String {
        return when (this) {
            GRAM -> "gram"
            MILLILITER -> "ml"
            PIECE -> context.getString(R.string.piece)
            LITER -> " liter"
            CUP -> context.getString(R.string.cup)
            TABLESPOON -> context.getString(R.string.tbsp)
            TEASPOON -> context.getString(R.string.teasp)
            CLOVE -> context.getString(R.string.clove)
        }
    }
}