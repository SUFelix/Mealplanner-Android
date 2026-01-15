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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
fun LoginView(
    onAuthSuccess: ()->Unit,
    onForgotPasswordClick: () -> Unit,
    modifier: Modifier = Modifier,
    signInViewModel: SignInViewModel
) {

    val state = signInViewModel.state
    val context = LocalContext.current
    LaunchedEffect(signInViewModel, context) {
        signInViewModel.authResults.collect { result ->
            when(result) {
                is AuthResult.Authorized -> {
                    onAuthSuccess()
                }
                is AuthResult.Unauthorized -> {
                   /* Toast.makeText(
                        context,
                        R.string.you_re_not_authorized,
                        Toast.LENGTH_LONG
                    ).show() */
                }
                is AuthResult.IncorrectPasswordOrUsername -> {
                     Toast.makeText(
                         context,
                         R.string.incorrect_password_or_username,
                         Toast.LENGTH_LONG
                     ).show()
                }
                is AuthResult.IOError ->{
                    Toast.makeText(
                        context,
                        "IO Error",
                        Toast.LENGTH_LONG
                    ).show()
                }
                else  -> {
                    val errorMessage = "An unknown error occurred"
                    Toast.makeText(
                        context,
                        errorMessage,
                        Toast.LENGTH_LONG
                    ).show()
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
            text = stringResource(R.string.sign_in),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = state.signInEmailOrUsername,
            onValueChange = { signInViewModel.onEvent(AuthUiEvent.SignInUsernameChanged(it)) },
            label = { Text("Email or username") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.outline,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                disabledBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.signInPassword,
            onValueChange = { signInViewModel.onEvent(AuthUiEvent.SignInPasswordChanged(it)) },
            label = { Text(stringResource(R.string.password)) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.outline,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                disabledBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedButton(
            onClick = { signInViewModel.onEvent(AuthUiEvent.SignIn) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.sign_in))
        }
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = {  onForgotPasswordClick() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.forgot_password))
        }
    }
}