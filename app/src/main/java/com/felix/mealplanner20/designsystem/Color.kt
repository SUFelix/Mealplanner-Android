package com.felix.mealplanner20.designsystem

import androidx.compose.ui.graphics.Color

/**
 * Design-system color tokens extracted from MealPlanner.
 *
 * "Lean + semantic" set: the neutrals and accents the app actually paints with,
 * plus the food-group and nutrition-score semantic colors. The legacy
 * Material2-style tonal ramps (old_gold*, Green*, Violet*, GreenGrey* ...) and
 * the unused dark scheme are intentionally omitted.
 *
 * Screens reference these vals directly (e.g. `Slate200`, `Lime600`) rather than
 * going through `MaterialTheme.colorScheme` — mirror that usage in the new app.
 */

// --- Neutrals: Slate ramp (Tailwind Slate) ---
val Slate100 = Color(0xFFF1F5F9)
val Slate200 = Color(0xFFE2E8F0)
val Slate300 = Color(0xFFCBD5E1)
val Slate400 = Color(0xFF94A3B8)
val Slate500 = Color(0xFF64748B)
val Slate950 = Color(0xFF020617)

// --- Primary accent / CTA: Lime ramp (Tailwind Lime) ---
val Lime100 = Color(0xFFECFCCB)
val Lime300 = Color(0xFFBEF264)
val Lime400 = Color(0xFFA3E635)
val Lime500 = Color(0xFF84CC16)
val Lime600 = Color(0xFF65A30D)

// --- Misc accents ---
val TomatoRed = Color(0xFFDC4C3E)
val Yellow400 = Color(0xFFFACC15)
val Orange300 = Color(0xFFFDBA74)

// --- Semantic: food-group colors (nutrition / plant-diversity visualizations) ---
val milk = Color(0xFFF7E7A2)
val fish = Color(0xFF0088CC)
val meat = Color(0xFFD32F2F)
val egg = Color(0xFFFFD54F)
val oil = Lime300
val grain = Color(0xFFC2A679)
val wholegrain = Color(0xFFA67C52)
val potato = Color(0xFFE9963A)
val fruit = Color(0xFF9C27B0)
val vegetable = Lime500
val legume = Slate400
val nutsandseeds = Orange300
val spice = Color(0xFFB8531D)
val other = Color(0xFF00BCD4)

// --- Semantic: nutrition-score thresholds ---
val nq_below30 = Color(0xFFBA1B1B)
val nq_below45 = Color(0xFFFFEB3B)
val nq_below60 = Lime300
val nq_below70 = Lime400
val nq_below80 = Lime500
val nq_below90 = Lime500
val nq_below95 = Lime600
val nq_above95 = Lime600
