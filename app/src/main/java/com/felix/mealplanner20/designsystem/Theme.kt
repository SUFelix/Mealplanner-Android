package com.felix.mealplanner20.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Design-system theme wrapper.
 *
 * Trimmed from MealPlanner's `MealPlanner20Theme`: light-only, no dynamic color,
 * no `@RequiresApi`, no window/navigation-bar side effects. The color-role
 * mapping follows the app's `LightColorScheme2`, restricted to the lean palette
 * (roles that pointed at dropped tonal ramps are substituted with the nearest
 * lean token).
 *
 * Note: most MealPlanner screens paint with the raw tokens (`Slate200`,
 * `Lime600`, ...) rather than `MaterialTheme.colorScheme`. The color scheme
 * here mainly drives stock Material components.
 */

private val LightColors = lightColorScheme(
    primary = Slate200,
    onPrimary = Slate950,
    primaryContainer = Lime600,
    onPrimaryContainer = Slate200,

    secondary = Slate300,
    onSecondary = Color.White,
    secondaryContainer = Slate300,
    onSecondaryContainer = Slate950,

    tertiary = Lime500,
    onTertiary = Slate950,
    tertiaryContainer = Lime100,
    onTertiaryContainer = Slate950,

    error = TomatoRed,
    onError = Color.White,

    background = Slate200,
    onBackground = Slate950,
    surface = Color.White,
    onSurface = Slate950,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate500,
    outline = Slate300,
)

@Composable
fun MealPlannerDesignTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = AppTypography,
        shapes = AppShapes,
        content = content,
    )
}
