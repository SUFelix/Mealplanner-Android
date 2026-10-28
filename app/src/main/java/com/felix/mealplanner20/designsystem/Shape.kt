package com.felix.mealplanner20.designsystem

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Shape scale for the design system.
 *
 * MealPlanner has no central shape definition — corner radii are hardcoded
 * inline. These are the conventions observed across the app, codified.
 */

val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp),
)

// Named constants for direct use where a raw radius is needed.
val ButtonCorner = 4.dp
val CardCorner = 12.dp
val PillCorner = 24.dp
