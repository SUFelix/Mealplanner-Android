package com.felix.mealplanner20.Views.ProfileSettingsLogin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.felix.mealplanner20.R
import com.felix.mealplanner20.ViewModels.SignInViewModel

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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.felix.mealplanner20.auth.AuthUiEvent
import com.felix.mealplanner20.ui.theme.Slate300
import com.mealplanner20.jwtauthktorandroid.auth.AuthResult

@Composable
fun ResetPasswordConfirmView(
    onPasswordSubmitSuccess:()->Unit,
    modifier: Modifier = Modifier,
    viewModel: SignInViewModel
) {
    val state = viewModel.state
    val context = LocalContext.current

    LaunchedEffect(viewModel, context) {
        viewModel.authResults.collect { result ->
            when(result) {
                is AuthResult.PasswordResetConfirmed -> {
                    onPasswordSubmitSuccess()
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
                is AuthResult.PasswordToShort ->{
                    Toast.makeText(
                        context,
                        context.getString(R.string.password_to_short),
                        Toast.LENGTH_LONG
                    ).show()
                }
                is AuthResult.InvalidTokenOrExpired ->{
                    Toast.makeText(
                        context,
                        context.getString(R.string.invalid_token),
                        Toast.LENGTH_LONG
                    ).show()
                }

                else  -> {
                  /*  val errorMessage = context.getString(R.string.an_unknown_error_occurred)
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
            text = stringResource(R.string.set_new_password),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = state.resetPasswordToken,
            onValueChange = { viewModel.onEvent(AuthUiEvent.ResetPasswordTokenChanged(it)) },
            label = { Text(stringResource(R.string.reset_token)) },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.resetPasswordNewPassword,
            onValueChange = { viewModel.onEvent(AuthUiEvent.ResetPasswordNewPasswordChanged(it)) },
            label = { Text(stringResource(R.string.new_password)) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = { viewModel.onEvent(AuthUiEvent.ConfirmPasswordReset) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        ) {
            Text(stringResource(R.string.set_password))
        }
    }
}

