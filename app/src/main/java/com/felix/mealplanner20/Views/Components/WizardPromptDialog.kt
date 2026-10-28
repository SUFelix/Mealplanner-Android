package com.felix.mealplanner20.Views.Components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.felix.mealplanner20.R
import com.felix.mealplanner20.ViewModels.WIZARD_PROMPT_MAX_LENGTH
import com.felix.mealplanner20.ViewModels.WizardPromptController
import com.felix.mealplanner20.ViewModels.isValidWizardPrompt
import com.felix.mealplanner20.ui.theme.Lime600
import com.felix.mealplanner20.ui.theme.Slate300

@Composable
fun WizardPromptDialog(
    controller: WizardPromptController,
    isRefine: Boolean = false
) {
    val isOpen by controller.isOpen.collectAsState()
    if (!isOpen) return

    val prompt by controller.prompt.collectAsState()
    val isGenerating by controller.isGenerating.collectAsState()
    val errorRes by controller.errorRes.collectAsState()
    val length = prompt.trim().length

    AlertDialog(
        containerColor = Slate300,
        onDismissRequest = controller::dismiss,
        properties = DialogProperties(
            dismissOnBackPress = !isGenerating,
            dismissOnClickOutside = !isGenerating
        ),
        title = {
            Text(
                style = MaterialTheme.typography.titleLarge,
                text = stringResource(
                    if (isRefine) R.string.wizard_prompt_refine_title else R.string.wizard_prompt_title
                )
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = prompt,
                    onValueChange = controller::onPromptChange,
                    enabled = !isGenerating,
                    minLines = 3,
                    maxLines = 6,
                    placeholder = {
                        Text(
                            stringResource(
                                if (isRefine) R.string.wizard_prompt_refine_hint else R.string.wizard_prompt_hint
                            )
                        )
                    },
                    supportingText = {
                        Text(stringResource(R.string.wizard_prompt_counter, length, WIZARD_PROMPT_MAX_LENGTH))
                    },
                    isError = length > WIZARD_PROMPT_MAX_LENGTH
                )
                if (!isRefine) {
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            R.string.wizard_suggestion_vegan_curry,
                            R.string.wizard_suggestion_quick_pasta,
                            R.string.wizard_suggestion_protein_breakfast
                        ).forEach { res ->
                            val suggestion = stringResource(res)
                            SuggestionChip(
                                onClick = { controller.onPromptChange(suggestion) },
                                enabled = !isGenerating,
                                label = { Text(suggestion) }
                            )
                        }
                    }
                }
                errorRes?.let {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = stringResource(it),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                if (isGenerating) {
                    Spacer(Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Lime600, strokeWidth = 2.dp)
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = stringResource(R.string.wizard_generating),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = controller::submit,
                enabled = !isGenerating && isValidWizardPrompt(prompt)
            ) {
                Text(
                    style = MaterialTheme.typography.labelLarge,
                    text = stringResource(R.string.wizard_prompt_send)
                )
            }
        },
        dismissButton = {
            TextButton(onClick = controller::dismiss, enabled = !isGenerating) {
                Text(
                    style = MaterialTheme.typography.labelLarge,
                    text = stringResource(R.string.cancel)
                )
            }
        }
    )
}
