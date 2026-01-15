package com.felix.mealplanner20.Views.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.felix.mealplanner20.ui.theme.Lime600
import com.felix.mealplanner20.ui.theme.Slate200

@Composable
fun MyCircularProgressIndicator() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Slate200)
        ,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = Lime600
        )
    }
}


@Preview(showBackground = true)
@Composable
fun MyCircularProgressIndicatorPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        MyCircularProgressIndicator()
    }
}
