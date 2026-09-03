# MealPlanner design-system export

A self-contained slice of MealPlanner's visual language — color tokens,
typography, shapes, spacing, and the `CustomButton` family — for reuse in another
Android / Jetpack Compose project.

## Contents

| File | What it gives you |
|------|-------------------|
| `Color.kt` | Slate + Lime ramps, accent colors, food-group + nutrition-score semantic colors (top-level `val`s, used directly as `Slate200`, `Lime600`, …) |
| `Type.kt` | `poppinsFontFamily`, `monaSansFamily`, `AppTypography` (Material 3 `Typography`) |
| `Shape.kt` | `AppShapes` + `ButtonCorner` / `CardCorner` / `PillCorner` constants |
| `Dimens.kt` | `space4…space24`, `GlobalCardElevation` |
| `Theme.kt` | `MealPlannerDesignTheme { }` — light-only `MaterialTheme` wrapper |
| `Buttons.kt` | `CustomButton`, `CustomFullWidthButton`, `CustomSliderListItem` (Material 3) |
| `DesignSystemCatalog.kt` | `@Preview` reference — palette, type scale, buttons |

## How to drop it into another project

1. **Copy the folder.** Copy every `.kt` file here into a package in the target
   project, e.g. `app/src/main/java/<your>/<pkg>/designsystem/`.

2. **Rename the package.** Change the `package com.felix.mealplanner20.designsystem`
   line at the top of each file to your package. In `Type.kt` also change the
   `import com.felix.mealplanner20.R` line to the target project's own `R`.

3. **Copy the fonts.** Copy these files from MealPlanner's
   `app/src/main/res/font/` into the target's `res/font/` (keep the exact names —
   they are referenced by `R.font.*` in `Type.kt`):

   ```
   monasans_regular.ttf   monasans_light.ttf   monasans_medium.ttf
   monasans_semibold.ttf  monasans_italic.ttf
   poppins_regular.ttf     poppins_medium.ttf   poppins_semibold.ttf
   poppins_bold.ttf        poppins_blackitalic.ttf   poppins_lightitalic.ttf
   ```

4. **Dependencies.** Only Jetpack Compose with Material 3 is required. MealPlanner
   builds against Compose BOM `2024.02.00`; any recent BOM works. No other
   MealPlanner dependency is needed for tokens + buttons.

   ```kotlin
   implementation(platform("androidx.compose:compose-bom:2024.02.00"))
   implementation("androidx.compose.material3:material3")
   // for the @Preview catalog:
   debugImplementation("androidx.compose.ui:ui-tooling")
   ```

5. **Use it.** Wrap your app content in the theme, then reference tokens directly
   the way MealPlanner does (most screens bypass `MaterialTheme.colorScheme`):

   ```kotlin
   MealPlannerDesignTheme {
       Surface(color = Slate200) {
           Card(shape = AppShapes.medium, /* … */) { /* … */ }
           CustomFullWidthButton(text = "Save", onClick = { /* … */ })
       }
   }
   ```

## Notes / deviations from the original

- **Light only.** No dynamic color, no dark scheme, no window-inset side effects
  (the original `MealPlanner20Theme` carried a dead dark scheme and an
  `@RequiresApi(Q)` annotation).
- **Type colors stripped.** MealPlanner hardcoded `color = Slate950` on every
  text style; here text inherits `LocalContentColor` so it adapts to context.
- **Buttons are Material 3.** The original `CustomButton` mixed Material 2 and 3
  imports; public parameter lists are unchanged, so call sites port as-is.
- **Lean palette.** The legacy Material 2 tonal ramps (`old_gold*`, `Green*`,
  `Violet*`, `GreenGrey*`, …) were dropped. Add them back from MealPlanner's
  `ui/theme/Color.kt` if you need them.

## Not included (possible follow-ups)

Cards, dialogs (`CustomAlertDialog`), `CircleIndicator` gauge,
`SwipeableItemWithActions`, top app bars, `ToggleButton`, `StyledSearchBar`.
These live in `Views/Components/` and feature screen files in MealPlanner.
