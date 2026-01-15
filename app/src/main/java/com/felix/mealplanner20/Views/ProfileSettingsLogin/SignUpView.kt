package com.felix.mealplanner20.Views.ProfileSettingsLogin


import android.widget.Toast
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
import com.mealplanner20.jwtauthktorandroid.auth.AuthResult


@Composable
fun SignUpView(
    onSignUpSuccess: ()->Unit,
    modifier: Modifier = Modifier,
    signInViewModel: SignInViewModel
) {

    val state = signInViewModel.state
    val context = LocalContext.current
    LaunchedEffect(signInViewModel, context) {
        signInViewModel.authResults.collect { result ->
            when(result) {
                is AuthResult.Authorized -> {
                    onSignUpSuccess()
                }
                is AuthResult.Unauthorized -> {
                   /* Toast.makeText(
                        context,
                        context.getString(R.string.you_re_not_authorized),
                        Toast.LENGTH_LONG
                    ).show()*/
                }
                is AuthResult.FieldsEmpty -> {
                    Toast.makeText(
                         context,
                         context.getString(R.string.fields_empty),
                         Toast.LENGTH_LONG
                     ).show()
                }
                is AuthResult.SuccessfullySignUp -> {
                     Toast.makeText(
                         context,
                         context.getString(R.string.successfully_sign_up),
                         Toast.LENGTH_LONG
                     ).show()
                    onSignUpSuccess()
                }
                is AuthResult.PasswordToShort -> {
                     Toast.makeText(
                         context,
                         context.getString(R.string.password_to_short),
                         Toast.LENGTH_LONG
                     ).show()
                }
                is AuthResult.UsernameTaken -> {
                    Toast.makeText(
                        context,
                        context.getString(R.string.username_taken),
                        Toast.LENGTH_LONG
                    ).show()
                }
                is AuthResult.EmailTaken -> {
                    Toast.makeText(
                        context,
                        context.getString(R.string.email_taken),
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
                is AuthResult.UserNotFound ->{
                    Toast.makeText(
                        context,
                        context.getString(R.string.user_not_found),
                        Toast.LENGTH_LONG
                    ).show()
                }

                else  -> {
                    val errorMessage = context.getString(R.string.an_unknown_error_occurred)
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
            text = stringResource(R.string.sign_up),//"Sign Up",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = state.signUpUsername,
            onValueChange = { signInViewModel.onEvent(AuthUiEvent.SignUpUsernameChanged(it)) },
            label = { Text(stringResource(R.string.username))},
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
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
            value = state.signUpEmail,
            onValueChange = { signInViewModel.onEvent(AuthUiEvent.SignUpEmailChanged(it)) },
            label = { Text(stringResource(R.string.email)) },
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
            value = state.signUpPassword,
            onValueChange = { signInViewModel.onEvent(AuthUiEvent.SignUpPasswordChanged(it)) },
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

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = { signInViewModel.onEvent(AuthUiEvent.SignUp) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        ) {
            Text(stringResource(R.string.sign_up))
        }
    }
}
