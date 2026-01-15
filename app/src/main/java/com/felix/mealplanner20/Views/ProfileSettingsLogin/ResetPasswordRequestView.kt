package com.felix.mealplanner20.Views.ProfileSettingsLogin

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.felix.mealplanner20.R
import com.felix.mealplanner20.ViewModels.SignInViewModel
import com.felix.mealplanner20.auth.AuthUiEvent
import com.felix.mealplanner20.ui.theme.Slate300
import com.mealplanner20.jwtauthktorandroid.auth.AuthResult

@Composable
fun ResetPasswordRequestView(
    onSuccessfulPasswordResetRequest: ()->Unit,
    modifier: Modifier = Modifier,
    viewModel: SignInViewModel
) {
    val state = viewModel.state
    val context = LocalContext.current
    LaunchedEffect(viewModel, context) {
        viewModel.authResults.collect { result ->
            when(result) {
                is AuthResult.PasswordResetConfirmed -> {
                    Toast.makeText(
                        context,
                        context.getString(R.string.password_reset_confirmed),
                        Toast.LENGTH_LONG
                    ).show()
                }
                is AuthResult.IOError ->{
                    Toast.makeText(
                        context,
                        context.getString(R.string.io_error),
                        Toast.LENGTH_LONG
                    ).show()
                }
                is AuthResult.SuccessfulPasswordResetRequest ->{
                    onSuccessfulPasswordResetRequest()
                }
                else  -> {
                    /*val errorMessage = context.getString(R.string.an_unknown_error_occurred)
                    Toast.makeText(
                        context,
                        errorMessage,
                        Toast.LENGTH_LONG
                    ).show()*/
                }
            }
        }
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.reset_password),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = state.resetPasswordEmail,
            onValueChange = { viewModel.onEvent(AuthUiEvent.ResetPasswordEmailChanged(it)) },
            label = { Text(stringResource(R.string.email)) },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.outline,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                disabledBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = { viewModel.onEvent(AuthUiEvent.RequestPasswordReset) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        ) {
            Text(stringResource(R.string.send_reset_email))
        }
    }
}
