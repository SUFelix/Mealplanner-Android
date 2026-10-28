package com.felix.mealplanner20.designsystem

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
// When copying into another project, replace this with that project's R class.
import com.felix.mealplanner20.R

/**
 * Type scale extracted from MealPlanner.
 *
 * Differences from the original `ui/theme/Type.kt`:
 *  - `Typography` is a plain `val` (computed once), not a recomputed `get()`.
 *  - The hardcoded `color = Slate950` was stripped from every style, so text
 *    inherits `LocalContentColor` / the surrounding theme.
 * All font families, weights, sizes, line heights and letter spacing are
 * unchanged.
 *
 * Requires the Poppins + Mona Sans `.ttf` files in `res/font/` (see README).
 */

val poppinsFontFamily = FontFamily(
    Font(R.font.poppins_blackitalic, FontWeight.Black),
    Font(R.font.poppins_bold, FontWeight.Bold),
    Font(R.font.poppins_semibold, FontWeight.SemiBold),
    Font(R.font.poppins_medium, FontWeight.Medium),
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_lightitalic, FontWeight.Light),
)

val monaSansFamily = FontFamily(
    Font(R.font.monasans_semibold, FontWeight.SemiBold),
    Font(R.font.monasans_medium, FontWeight.Medium),
    Font(R.font.monasans_italic, FontWeight.Thin),
    Font(R.font.monasans_regular, FontWeight.Normal),
    Font(R.font.monasans_light, FontWeight.Light),
)

val AppTypography = Typography(
    titleLarge = TextStyle(
        fontFamily = monaSansFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = monaSansFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = monaSansFamily,
        fontWeight = FontWeight.Thin,
        fontSize = 12.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = monaSansFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = monaSansFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = monaSansFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = monaSansFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = monaSansFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = monaSansFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 28.sp,
        letterSpacing = 1.5.sp,
    ),
    displayMedium = TextStyle(
        fontFamily = monaSansFamily,
        fontWeight = FontWeight.Light,
        fontSize = 64.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = poppinsFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp,
    ),
)
