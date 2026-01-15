package com.felix.mealplanner20.Views.Components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.ButtonDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomButton(
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
    text: String,
    onClick: () -> Unit,
    buttonColor: Color = Color.White,  // Standardfarbe Weiß
    textColor: Color = Color.Black,    // Standardfarbe Schwarz
    borderColor: Color = Color(0xFFCBD5E1),
    width: Dp = 166.dp,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
        .width(width)
        .height(50.dp)

    ) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = buttonColor,
            contentColor = textColor,
            disabledBackgroundColor = Color.LightGray,
            disabledContentColor = Color.DarkGray
        ),
        border = BorderStroke(1.dp, borderColor),
        modifier = modifier
    ) {
        Text(
            text = text,
            //modifier = Modifier.padding(top = 10.dp, bottom = 10.dp, start = 16.dp,end = 16.dp),
            color = textColor,
            style = textStyle,
            fontSize = 14.sp
        )
    }
}
@Composable
fun CustomFullWidthButton(
    text: String,
    onClick: () -> Unit,
    buttonColor: Color = Color.White,
    textColor: Color = Color.Black,
    borderColor: Color = Color(0xFF65A30D),
    verticalPadding:Dp = 16.dp
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.buttonColors(buttonColor),
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp,vertical = verticalPadding)
            .height(58.dp)

    ) {
        Text(
            text = text,
            //modifier = Modifier.padding(top = 10.dp, bottom = 10.dp, start = 16.dp,end = 16.dp),
            color = textColor,
            style = MaterialTheme.typography.titleMedium,
            fontSize = 14.sp
        )
    }
}

@Composable
fun CustomSliderListItem(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(84.dp)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        shape = RoundedCornerShape(0.dp),
        border = BorderStroke(2.dp, Color(0xFFE2E8F0)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}
