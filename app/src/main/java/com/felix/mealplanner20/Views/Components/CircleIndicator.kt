package com.felix.mealplanner20.Views.Components


import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.felix.mealplanner20.Meals.Data.Recipe
import com.felix.mealplanner20.R
import com.felix.mealplanner20.Views.Mealplan.GLOBAL_CARD_ELEVATION
import com.felix.mealplanner20.ui.theme.Slate950
import com.felix.mealplanner20.ui.theme.light_grey
import com.felix.mealplanner20.ui.theme.nq_above95
import com.felix.mealplanner20.ui.theme.nq_below30
import com.felix.mealplanner20.ui.theme.nq_below45
import com.felix.mealplanner20.ui.theme.nq_below60
import com.felix.mealplanner20.ui.theme.nq_below70
import com.felix.mealplanner20.ui.theme.nq_below80
import com.felix.mealplanner20.ui.theme.nq_below90
import com.felix.mealplanner20.ui.theme.nq_below95
import kotlin.math.max
import kotlin.math.min

@Composable
fun CircleIndicator(
    canvasSize: Dp = 152.dp,
    indicatorValue:Int = 0,
    maxIndicatorValue:Int = 100,
    backgroundIndicatorColor: Color = light_grey,
    backgroundIndicatorStrokeWidth:Float = 32f,
    foregroundIndicatorColor: Color = getColorFromIndicatorValue(indicatorValue),
    foregroundIndicatorStrokeWidth:Float = 32f,
    bigTextColor: Color = foregroundIndicatorColor
)
{
    var animatedIndicatorValue by remember { mutableStateOf(0f) }
    LaunchedEffect(key1 = indicatorValue) {
        animatedIndicatorValue = indicatorValue.toFloat()
    }
    val percentage = max(0.0f,min((animatedIndicatorValue / maxIndicatorValue)*100,100.0f))
    val sweepAngle by animateFloatAsState(
        targetValue = (percentage * 3.6).toFloat(),
        animationSpec = tween(700)
    )
    val receivedValue by animateIntAsState(
        targetValue = indicatorValue,
        animationSpec = tween(700)
    )

    Box(
        modifier = Modifier
            .size(canvasSize)
            .drawBehind {
                val componentSize = size / 1.25f
                backgroundIndicator(
                    componentSize = componentSize,
                    indicatorColor = backgroundIndicatorColor,
                    indicatorStrokeWidth = backgroundIndicatorStrokeWidth
                )
                foregroundIndicator(
                    sweepAngle = sweepAngle,
                    componentSize = componentSize,
                    indicatorColor = foregroundIndicatorColor,
                    indicatorStrokeWidth = foregroundIndicatorStrokeWidth
                )
            }
    ) {

        Text(
            text = "$receivedValue",
            color = bigTextColor,
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.align(Alignment.Center)
        )
        NutritionScoreBadge(
            modifier = Modifier.fillMaxWidth().padding(16.dp)
                .height(33.dp)
                .clip(RoundedCornerShape(16.dp))
                .align(Alignment.BottomCenter)
                .background(color = Slate950.copy(alpha = 0.65f))
        )
    }
}

fun getColorFromIndicatorValue(indicatorValue: Int): Color {
    return when {
        indicatorValue < 30 -> nq_below30
        /*indicatorValue < 45 -> nq_below45
        indicatorValue < 60 -> nq_below60
        indicatorValue < 70 -> nq_below70
        indicatorValue < 80 -> nq_below80
        indicatorValue < 90 -> nq_below90
        indicatorValue < 95 -> nq_below95*/
        else -> nq_above95
    }
}


fun DrawScope.backgroundIndicator(
    componentSize: Size,
    indicatorColor: Color,
    indicatorStrokeWidth:Float
){
    drawCircle(
        color = indicatorColor,
        radius = componentSize.width / 2,  // Nehme die Hälfte der Breite als Radius
        center = Offset(size.width / 2, size.height / 2), // Kreis zentrieren
        style = Stroke(
            width = indicatorStrokeWidth,
            cap = StrokeCap.Round
        )
    )
}
fun DrawScope.foregroundIndicator(
    sweepAngle:Float,
    componentSize: Size,
    indicatorColor: Color,
    indicatorStrokeWidth:Float
){
    drawArc(
        size = componentSize,
        color = indicatorColor,
        startAngle = 270f,
        sweepAngle = sweepAngle,
        useCenter = false,
        style = Stroke(
            width = indicatorStrokeWidth,
            cap = StrokeCap.Round
        ),
        topLeft = Offset(
            x = (size.width -componentSize.width)/(2f),
            y = (size.height -componentSize.height)/(2f)
        )
    )
}

@Composable
fun EmbeddedElements(
    bigText:Int,
    bigTextFontSize:TextUnit,
    bigTextColor:Color,
    bigTextSuffix: String,
    smallText:String,
    smallTextFontSize:TextUnit,
    smallTextColor:Color
){
    Box(){


    }
}
@Composable
fun NutritionScoreBadge(modifier:Modifier,text: String = stringResource(R.string.nutrition_score)) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier

    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Slate950.copy(alpha = 0.3f))
                .blur(32.dp)
        )

        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.labelSmall
        )
    }
}
@Composable
@Preview(showBackground = true)
fun CustomComposablePreview(){
    CircleIndicator(indicatorValue = 20)
}