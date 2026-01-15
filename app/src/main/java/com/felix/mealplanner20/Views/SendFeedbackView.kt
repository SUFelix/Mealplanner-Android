package com.felix.mealplanner20.Views

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.felix.mealplanner20.R
import com.felix.mealplanner20.ViewModels.FeedbackError
import com.felix.mealplanner20.ViewModels.FeedbackStatus
import com.felix.mealplanner20.ViewModels.FeedbackViewModel
import com.felix.mealplanner20.Views.Components.MyCircularProgressIndicator

private const val MAX_MESSAGE_LENGTH = 2000

@Composable
fun SendFeedbackView(
    feedbackViewModel: FeedbackViewModel,
    navController: NavController
    ) {
    var text by remember { mutableStateOf("") }

    val context = LocalContext.current
    val status by feedbackViewModel.status.collectAsState(initial = FeedbackStatus.Idle)

    val canSend = text.isNotBlank() && status !is FeedbackStatus.Sending
    val charCountText = "${text.length}/$MAX_MESSAGE_LENGTH"

    LaunchedEffect(status) {
        when (status) {
            is FeedbackStatus.Success -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.feedback_sent_successfully),
                    Toast.LENGTH_SHORT
                ).show()
                navController.navigateUp()
                feedbackViewModel.resetStatus()
            }
            is FeedbackStatus.Error -> {
                val err = (status as FeedbackStatus.Error).error
                val message = when (err) {
                    FeedbackError.NotAuthenticated ->
                        "Bitte melde dich an, um Feedback zu senden."
                    FeedbackError.Forbidden ->
                        "Du hast keine Berechtigung, Feedback zu senden."
                    FeedbackError.NetworkUnavailable ->
                        "Keine Internetverbindung."
                    FeedbackError.Timeout ->
                        "Zeitüberschreitung beim Senden."
                    FeedbackError.RateLimited ->
                        "Zu viele Anfragen. Bitte versuche es später erneut."
                    FeedbackError.PayloadTooLarge ->
                        "Dein Feedback ist zu lang (max. $MAX_MESSAGE_LENGTH Zeichen)."
                    FeedbackError.Validation ->
                        "Bitte gib eine Nachricht ein."
                    FeedbackError.NotFound ->
                        "Service momentan nicht verfügbar."
                    FeedbackError.ServerError ->
                        "Serverfehler. Bitte später erneut versuchen."
                    is FeedbackError.Unknown ->
                        "Unerwarteter Fehler$${err.message?.let { ": $$it" } ?: ""}"
                }
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                feedbackViewModel.resetStatus()
            }
            else -> Unit
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.feedback_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = text,
            onValueChange = { input ->
                // Hartes Limit: Max. 2000 Zeichen
                text = if (input.length <= MAX_MESSAGE_LENGTH) input else input.take(MAX_MESSAGE_LENGTH)
            },
            label = { Text(stringResource(R.string.your_feedback)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.outline,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                disabledBorderColor = MaterialTheme.colorScheme.outline
            ),
            maxLines = Int.MAX_VALUE,
            supportingText = {
                Text(
                    text = charCountText,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        )

        Spacer(Modifier.height(16.dp))

        when (status) {
            is FeedbackStatus.Sending -> {
                MyCircularProgressIndicator()
            }
            else -> {
                OutlinedButton(
                    onClick = { feedbackViewModel.sendFeedback(text) },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    enabled = canSend
                ) {
                    Text(stringResource(R.string.send))
                }
            }
        }
    }
}