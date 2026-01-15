package com.felix.mealplanner20

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.felix.mealplanner20.Meals.Data.IngredientRepository
import com.felix.mealplanner20.Views.MainView
import com.felix.mealplanner20.WorkManager.CustomWorkerFactory
import com.felix.mealplanner20.WorkManager.SynchronizeIngredientsWorkManager
import com.felix.mealplanner20.ui.theme.MealPlanner20Theme
import com.felix.mealplanner20.ui.theme.Slate950
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        val workRequest = OneTimeWorkRequestBuilder<SynchronizeIngredientsWorkManager>()
            .build()

        WorkManager.getInstance(applicationContext).enqueue(workRequest)


        enableEdgeToEdge(
            navigationBarStyle = SystemBarStyle.dark(
                Slate950.toArgb()
            )
        )

        setContent {
            MealPlanner20Theme(dynamicColor = false) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainView()
                }
            }
        }
    }
}