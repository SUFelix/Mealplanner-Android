package com.felix.mealplanner20.designsystem

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * The `CustomButton` family from MealPlanner, ported to Material 3 only
 * (the original mixed Material 2 + Material 3 imports). Public parameter lists
 * are unchanged so call sites port over as-is.
 */

@Composable
fun CustomButton(
    text: String,
    onClick: () -> Unit,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
    buttonColor: Color = Color.White,
    textColor: Color = Color.Black,
    borderColor: Color = Slate300,
    width: Dp = 166.dp,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
        .width(width)
        .height(50.dp),
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(ButtonCorner),
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor,
            contentColor = textColor,
            disabledContainerColor = Color.LightGray,
            disabledContentColor = Color.DarkGray,
        ),
        elevation = null,
        border = BorderStroke(1.dp, borderColor),
        modifier = modifier,
    ) {
        Text(text = text, color = textColor, style = textStyle)
    }
}

@Composable
fun CustomFullWidthButton(
    text: String,
    onClick: () -> Unit,
    buttonColor: Color = Color.White,
    textColor: Color = Color.Black,
    borderColor: Color = Lime600,
    verticalPadding: Dp = 16.dp,
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(ButtonCorner),
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor,
            contentColor = textColor,
        ),
        elevation = null,
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = verticalPadding)
            .height(58.dp),
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
fun CustomSliderListItem(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(84.dp)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        shape = RoundedCornerShape(0.dp),
        border = BorderStroke(2.dp, Slate200),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            content = content,
        )
    }
}
