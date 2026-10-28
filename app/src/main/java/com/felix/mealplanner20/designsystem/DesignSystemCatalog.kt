package com.felix.mealplanner20.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Visual reference for the extracted design system. Render these previews in
 * Android Studio to eyeball the tokens and buttons.
 */

private val paletteSwatches: List<Pair<String, Color>> = listOf(
    "Slate100" to Slate100, "Slate200" to Slate200, "Slate300" to Slate300,
    "Slate400" to Slate400, "Slate500" to Slate500, "Slate950" to Slate950,
    "Lime100" to Lime100, "Lime300" to Lime300, "Lime400" to Lime400,
    "Lime500" to Lime500, "Lime600" to Lime600,
    "TomatoRed" to TomatoRed, "Yellow400" to Yellow400, "Orange300" to Orange300,
)

private val foodGroupSwatches: List<Pair<String, Color>> = listOf(
    "milk" to milk, "fish" to fish, "meat" to meat, "egg" to egg, "oil" to oil,
    "grain" to grain, "wholegrain" to wholegrain, "potato" to potato,
    "fruit" to fruit, "vegetable" to vegetable, "legume" to legume,
    "nutsandseeds" to nutsandseeds, "spice" to spice, "other" to other,
)

private val typeSamples: List<Pair<String, TextStyle>>
    @Composable get() = with(MaterialTheme.typography) {
        listOf(
            "displayMedium" to displayMedium,
            "headlineSmall" to headlineSmall,
            "titleLarge" to titleLarge,
            "titleMedium" to titleMedium,
            "titleSmall" to titleSmall,
            "labelMedium" to labelMedium,
            "labelLarge" to labelLarge,
            "labelSmall" to labelSmall,
            "bodyMedium" to bodyMedium,
            "bodySmall" to bodySmall,
            "bodyLarge" to bodyLarge,
        )
    }

@Composable
private fun Swatch(name: String, color: Color) {
    Row(
        modifier = Modifier.width(120.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(28.dp)
                .background(color, RoundedCornerShape(6.dp)),
        )
        Text(name, Modifier.padding(start = 8.dp), style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun SwatchGrid(swatches: List<Pair<String, Color>>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        swatches.chunked(3).forEach { rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowItems.forEach { (name, color) -> Swatch(name, color) }
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
fun PaletteCatalogPreview() {
    MealPlannerDesignTheme {
        Column(
            Modifier
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text("Neutrals & accents", style = MaterialTheme.typography.titleMedium)
            SwatchGrid(paletteSwatches)
            Text("Food groups & score", style = MaterialTheme.typography.titleMedium)
            SwatchGrid(foodGroupSwatches)
        }
    }
}

@Preview(showBackground = true, heightDp = 700)
@Composable
fun TypographyCatalogPreview() {
    MealPlannerDesignTheme {
        Column(
            Modifier.background(Color.White).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            typeSamples.forEach { (name, style) ->
                Text("$name — Almost before we knew it", style = style)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ButtonsCatalogPreview() {
    MealPlannerDesignTheme {
        Column(
            Modifier.fillMaxWidth().background(Color.White).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            CustomButton(text = "Custom", onClick = {})
            CustomButton(text = "Disabled", onClick = {}, enabled = false)
            CustomButton(
                text = "Lime",
                onClick = {},
                buttonColor = Lime600,
                textColor = Color.White,
                borderColor = Lime600,
            )
            CustomFullWidthButton(text = "Full width CTA", onClick = {})
            CustomSliderListItem {
                Text("Slider list item", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
