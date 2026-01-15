@file:OptIn(ExperimentalMaterial3Api::class)

package com.felix.mealplanner20.Views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.felix.mealplanner20.Meals.Data.helpers.AllDayDetailsWithGlobalDge
import com.felix.mealplanner20.Meals.Data.helpers.DayDetailData
import com.felix.mealplanner20.Meals.Data.helpers.DgeData
import com.felix.mealplanner20.Meals.Data.helpers.dgeGroup
import com.felix.mealplanner20.NUTRITION_COCKPIT_TEST_TAG
import com.felix.mealplanner20.R
import com.felix.mealplanner20.ViewModels.NutritionViewModel
import com.felix.mealplanner20.Views.Components.CircleIndicator
import com.felix.mealplanner20.Views.Mealplan.GLOBAL_CARD_ELEVATION
import com.felix.mealplanner20.ui.theme.Lime100
import com.felix.mealplanner20.ui.theme.Lime600
import com.felix.mealplanner20.ui.theme.Slate200
import com.felix.mealplanner20.ui.theme.Slate300
import com.felix.mealplanner20.ui.theme.Slate500
import com.felix.mealplanner20.ui.theme.Slate950
import com.felix.mealplanner20.ui.theme.TomatoRed
import com.felix.mealplanner20.ui.theme.Yellow400
import com.felix.mealplanner20.ui.theme.egg
import com.felix.mealplanner20.ui.theme.fish
import com.felix.mealplanner20.ui.theme.fruit
import com.felix.mealplanner20.ui.theme.grain
import com.felix.mealplanner20.ui.theme.legume
import com.felix.mealplanner20.ui.theme.meat
import com.felix.mealplanner20.ui.theme.milk
import com.felix.mealplanner20.ui.theme.nutsandseeds
import com.felix.mealplanner20.ui.theme.oil
import com.felix.mealplanner20.ui.theme.other
import com.felix.mealplanner20.ui.theme.potato
import com.felix.mealplanner20.ui.theme.vegetable
import com.felix.mealplanner20.ui.theme.wholegrain
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.felix.mealplanner20.Views.Components.MyCircularProgressIndicator


val dgeColors = mapOf(
    dgeGroup.MILK to milk,
    dgeGroup.FISH to fish,
    dgeGroup.MEAT to meat,
    dgeGroup.EGG to egg,
    dgeGroup.OIL to oil,
    dgeGroup.GRAIN to grain,
    dgeGroup.WHOLEGRAIN to wholegrain,
    dgeGroup.POTATO to potato,
    dgeGroup.FRUIT to fruit,
    dgeGroup.VEGETABLE to vegetable,
    dgeGroup.LEGUME to legume,
    dgeGroup.NUTSANDSEEDS to nutsandseeds,
    dgeGroup.OTHER to other,
   // dgeGroup.OTHERVEGETARIAN to other,
   // dgeGroup.OTHERVEGAN to other
)
@Composable
fun NutritionDashboard(nutritionViewModel: NutritionViewModel) {

    val isLoading by nutritionViewModel.isLoading.collectAsState()
    if (isLoading) {
        Box(
            modifier = Modifier
                .testTag(NUTRITION_COCKPIT_TEST_TAG)
        ) {
            MyCircularProgressIndicator()
        }
    }
    else{
        val nutritionQuality = nutritionViewModel.nutritionQuality.collectAsStateWithLifecycle( initialValue = 42)
        val caloriesAvg = nutritionViewModel.caloriesAvg.collectAsStateWithLifecycle(initialValue = 0f)
        val caloriesPerDay =  nutritionViewModel.caloriesPerDay.collectAsStateWithLifecycle(initialValue = listOf(0f))
        val overallNutrientCompliance = nutritionViewModel.overallNutrientCompliance.collectAsStateWithLifecycle(initialValue = 0)
        val allDayDetailsWithGlobalDgeState = nutritionViewModel.allDayDetailsWithGlobalDgeFlow.collectAsStateWithLifecycle(
            initialValue = AllDayDetailsWithGlobalDge(
                emptyList(), 0f
            )
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Slate200)
                .border(BorderStroke(1.dp, Slate300))
                .testTag(NUTRITION_COCKPIT_TEST_TAG)
        ) {
            /*NutritionOverview(
                nutritionQuality.value,
                caloriesAvg.value,
                overallNutrientCompliance.value,
                allDayDetailsWithGlobalDgeState.value.globalDgeCompliance
            )*/

            Box(modifier = Modifier
                .fillMaxSize()
                .border(BorderStroke(1.dp, Slate300))
                .testTag(NUTRITION_COCKPIT_TEST_TAG)
                .background(Slate200)) {
                DayDetailList(
                    allDayDetailsWithGlobalDgeState.value.dayDetails,
                    caloriesPerDay.value,
                    nutritionQuality.value,
                    caloriesAvg.value,
                    overallNutrientCompliance.value,
                    allDayDetailsWithGlobalDgeState.value.globalDgeCompliance
                )
            }
        }
    }
}

@Composable
fun DayDetailList(
    dayDetails: List<DayDetailData>,
    caloriesPerDay: List<Float>,
    nutritionQuality: Int,
    caloriesAvg:Float,
    overallNutrientCompliance:Int,
    globalDge:Float
) {
    LazyColumn(
        modifier = Modifier
            .wrapContentSize()
            .clip(RoundedCornerShape(12.dp))
    ) {

        item{
            NutritionOverview(
                nutritionQuality,
                caloriesAvg,
                overallNutrientCompliance,
                globalDge
            )
        }

        itemsIndexed(
            items = dayDetails,
            key = { index, dayDetail -> dayDetail.dayName }
        ) { index, dayDetail ->
            DayDetailCard(
                dayDetails = dayDetail,
                calories = caloriesPerDay.getOrNull(index) ?: 0f
            )
        }
    }
}


@Composable
fun NutritionOverview(
    nutritionQuality: Int,
    caloriesAvg:Float,
    overallNutrientCompliance:Int,
    globalDge:Float
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ){
        Column(modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally
        ){
            CircleIndicator(
                canvasSize = 200.dp,
                indicatorValue = nutritionQuality,
                maxIndicatorValue = 100,
            )
            CaloriesIndicator(caloriesAvg,overallNutrientCompliance,globalDge)
        }
    }
}

@Composable
fun CaloriesIndicator(caloriesAvg:Float,overallNutrientCompliance:Int,globalDge:Float) {
    Column(
        modifier = Modifier.padding( start = 16.dp),
        Arrangement.Center,
        Alignment.CenterHorizontally
    ){
        Row (
            modifier = Modifier.padding(start = 16.dp, top = 16.dp,end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = "${caloriesAvg.toInt()} kCal",
                style = MaterialTheme.typography.labelMedium.copy(fontSize = 32.sp)
            )
            Spacer(Modifier.width(8.dp))
            Text(stringResource(R.string.per_day), style = MaterialTheme.typography.titleSmall)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly){
            Text("${(globalDge*100).toInt()}"+" %"+" "+stringResource(R.string.dge_compliance), style = MaterialTheme.typography.titleMedium.copy(color = Slate500))

            Text("$overallNutrientCompliance"+" %"+" "+stringResource(R.string.macro_compliance), style = MaterialTheme.typography.titleMedium.copy(color = Slate500))
        }
    }
}

@Composable
fun BoxWithRecommendation(
    value: Float,
    recommendation: Float?,
    maxValue: Float = 300f,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(6.dp)
        //.padding(vertical = 5.dp)
        .clip(RoundedCornerShape(12.dp))
        .background(Slate200)
) {
    val barHeight = 6.dp
    val backgroundColor = Slate200
    val mainColor = Lime600
    val deficitColor = Yellow400
    val excessColor = TomatoRed
    val recommendationColor = Lime600

    if (maxValue <= 0f) return

    Box(
        modifier = modifier
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            // Calculate the width percentages based on the maxValue
            val valueX = (width * (value / maxValue)).coerceIn(0f, width)
            val recommendationX = recommendation?.let { (width * (it / maxValue)).coerceIn(0f, width) } ?: valueX

            // Draw the main progress bar
            drawRect(
                color = mainColor,
                topLeft = Offset.Zero,
                size = androidx.compose.ui.geometry.Size(width = valueX, height = size.height)
            )

            if (recommendation != null) {
                // Draw the bar between value and recommendation
                if (value < recommendation) {
                    drawRect(
                        color = deficitColor,
                        topLeft = Offset(x = valueX, y = 0f),
                        size = androidx.compose.ui.geometry.Size(width = recommendationX - valueX, height = size.height)
                    )
                }

                if (value > recommendation) {
                    drawRect(
                        color = excessColor,
                        topLeft = Offset(x = recommendationX, y = 0f),
                        size = androidx.compose.ui.geometry.Size(width = valueX - recommendationX, height = size.height)
                    )
                }

                /*drawLine(
                    color = recommendationColor,
                    start = Offset(recommendationX, 0f),
                    end = Offset(recommendationX, size.height),
                    strokeWidth = 4f
                )*/
            }
        }
    }
}

@Composable
fun DayDetailCard(
    dayDetails: DayDetailData,
    calories: Float
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = GLOBAL_CARD_ELEVATION.dp)
    ) {
        Column(
            modifier = Modifier
                .background(Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()

                    .height(55.dp)
                    .padding(horizontal = 1.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 12.dp,  // Oben links abgerundet
                            topEnd = 12.dp,    // Oben rechts abgerundet
                            bottomStart = 0.dp, // Unten eckig
                            bottomEnd = 0.dp   // Unten eckig
                        )
                    ),

                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Text(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterVertically),
                    text = dayDetails.dayName,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 16.sp)
                )
                Text(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterVertically),
                    text = "${calories.toInt()}"+" kCal",
                    style = MaterialTheme.typography.titleLarge
                )
            }
            LazyVerticalGrid(
                modifier = Modifier
                    .wrapContentHeight()
                    .heightIn(max = 400.dp),
                columns = GridCells.Fixed(2)
            ) {
                itemsIndexed(
                    items = dayDetails.nutrients.keys.toList(),
                    key = { index,nutrient -> nutrient }
                ) { index,nutrient ->

                    val isLast = index == nutrient.lastIndex

                    NutrientItem(
                        nutrient = nutrient,
                        value = dayDetails.nutrients[nutrient] ?:0f,
                        recommendation = dayDetails.recommendations[nutrient],
                        isLast
                    )
                }
                item {
                    var showDialog by remember { mutableStateOf(false)}

                    if (showDialog) {
                        DetailStatsAlertDialog(
                            onDismiss = { showDialog = false },
                            pieChart = {DetailStatsAlertDialogPieChart(dayDetails.dgeData,dayDetails.dgeMapping)},
                            dgeMapping= dayDetails.dgeMapping
                        )
                    }

                    Row(
                        modifier = Modifier
                            .height(82.dp)
                            .fillMaxWidth()
                            .background(Lime100)
                            .border(
                                BorderStroke(1.dp, Slate200)
                            )
                            .padding(16.dp)
                            .clickable {

                                showDialog = true

                            }
                        ,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Column(modifier = Modifier.weight(1.75f)) {
                            Text(
                                style = MaterialTheme.typography.bodySmall,
                                text = stringResource(R.string.dge_compliance)
                            )
                            Text(
                                style = MaterialTheme.typography.titleLarge.copy(color = Lime600),//TODO Farbe abhängig von dge compliance?
                                text =dayDetails.compliancePercentage.toInt().toString() +" %")
                        }
                        IconButton(onClick = { showDialog = true }, modifier = Modifier.weight(0.5f)) {
                            androidx.compose.material3.Icon(
                                painter = painterResource(id = R.drawable.arrow_icon),
                                contentDescription = "Go to dge deteils",
                                tint = Slate950
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun PieChartLegend(dgeMapping:List<Pair<DgeData, DgeData?>>) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .padding(top = 8.dp, start = 3.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .verticalScroll(scrollState)

    ) {
        dgeColors.forEach { (group, color) ->

            var (actualValue, recommendedValue) = dgeMapping
                .find { it.first.group == group }
                ?.let { it.first.percentage to (it.second?.percentage ?: 0f) }
                ?: (0.01f to 0.01f)
            if(color == other) {
                actualValue += dgeMapping
                    .find { it.first.group == dgeGroup.OTHERVEGETARIAN }?.first?.percentage?:0f
                actualValue += dgeMapping
                    .find { it.first.group == dgeGroup.OTHERVEGAN }?.first?.percentage?:0f
            }

            DGEItem(name = group.name, color = color, actualValue = actualValue, recommenedValue = recommendedValue)

        }
    }
}

@Composable
fun DGEItem(
    color: Color = Color.Red,
    name:String = "NUTSANDSEEDS",
    recommenedValue:Float = 0.2f,
    actualValue:Float = 0.1f
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .height(123.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Slate200)
    ){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .weight(1f)
                .padding(top = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Text(
                text = name,
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Slate950)
            )
            Box(modifier = Modifier
                .wrapContentSize()
                .padding(end = 16.dp)){
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(color, shape = RoundedCornerShape(8.dp))
                )
            }
        }
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            color = Slate200,
            thickness = 1.dp
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .fillMaxHeight()
        ){
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.BottomCenter){
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ){
                    Text(
                        text = stringResource(R.string.actual),
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, color = Slate500)
                        )
                    Text(
                        text = "${String.format("%.0f", actualValue*100)}%",
                        style = MaterialTheme.typography.labelLarge.copy(fontSize = 14.sp, color = Slate950)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(Slate200)
            )

            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.BottomCenter){

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ){
                    Text(
                        text = stringResource(R.string.recommended),
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, color = Slate500)
                    )
                    Text(
                        text =  "${String.format("%.0f", recommenedValue*100)}%",
                        style = MaterialTheme.typography.labelLarge.copy(fontSize = 14.sp, color = Slate950)
                    )
                }
            }

        }
    }
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewDGEItem() {
    MaterialTheme {
        DGEItem(actualValue = 0.23f)
    }
}

@Composable
fun NutrientItem(
    nutrient: String,
    value: Float,
    recommendation: Float? = null,
    isLast:Boolean = false
) {
    val cornerShape = RoundedCornerShape(
        topStart = 0.dp,
        topEnd = 0.dp,
        bottomStart =if(isLast) 12.dp else 0.dp,
        bottomEnd = 0.dp
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(82.dp)
            .border(BorderStroke(1.dp, Slate200))
            .clip(cornerShape)
    ){
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = nutrient,
                style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
            )
            Text(
                text = "%.0f".format(value),
                style = MaterialTheme.typography.titleLarge
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            BoxWithRecommendation(
                value = value,
                recommendation = recommendation,
                maxValue = recommendation!!*1.8f,
                modifier= Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Slate200)
            )
        }
    }
}

@Composable
fun DetailStatsAlertDialogPieChart(
    dgeData: List<DgeData>,
    dgeMapping:List<Pair<DgeData,
            DgeData?>>
) {

    val canvasSize = 200.dp

    Canvas(modifier = Modifier
        .size(canvasSize)
    ) {
        val total = dgeData.fold(0f) { acc, data -> acc + data.percentage }
        var startAngle = 0f

        dgeMapping.forEach { data ->
            val sweepAngle = 360f * (data.first.percentage / total)
            drawArc(
                color = dgeColors[data.first.group] ?: Color.Gray,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                style = Fill
            )
            val radius = 277.dp.value
            val path = Path().apply {
                moveTo(center.x, center.y)
                arcTo(
                    rect = Rect(center - Offset(radius, radius), center + Offset(radius, radius)),
                    startAngleDegrees = startAngle,
                    sweepAngleDegrees = sweepAngle,
                    forceMoveTo = false
                )
                close()
            }
            drawPath(
                path = path,
                color = Color.White, // Slate200
                style = Stroke(width = 5f)
            )
            startAngle += sweepAngle
        }
    }
}
@Preview
@Composable
fun PreviewDetailStatsAlertDialogPieChart() {
    val sampleDgeData = listOf(
        DgeData(dgeGroup.MILK, 15f),
        DgeData(dgeGroup.LEGUME, 10f),
        DgeData(dgeGroup.MEAT, 20f),
        DgeData(dgeGroup.EGG, 5f),
        DgeData(dgeGroup.VEGETABLE, 25f),
        DgeData(dgeGroup.FRUIT, 15f),
        DgeData(dgeGroup.OTHER, 10f)
    )

    val sampleDgeMapping = sampleDgeData.map { it to null }

    DetailStatsAlertDialogPieChart(
        dgeData = sampleDgeData,
        dgeMapping = sampleDgeMapping
    )
}

@Composable
fun DetailStatsAlertDialog(
    onDismiss: () -> Unit,
    pieChart: @Composable () -> Unit,
    dgeMapping: List<Pair<DgeData, DgeData?>>
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .clip(RoundedCornerShape(12.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                Box(
                    modifier = Modifier
                        .wrapContentHeight()
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp)
                ) {
                    pieChart()
                }
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 4000.dp)) {

                    PieChartLegend(dgeMapping)
                }
            }
        }
    }
}