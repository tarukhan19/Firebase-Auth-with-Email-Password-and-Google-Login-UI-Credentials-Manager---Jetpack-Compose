package com.demo.authentication.features.presentation.login

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.demo.authentication.R
import com.demo.authentication.core.domain.utils.Resource
import com.demo.authentication.features.data.repository.GoogleAuthUiClientImpl
import com.demo.authentication.features.presentation.components.CircularProgressBar
import com.demo.authentication.features.presentation.components.CustomButton
import com.demo.authentication.features.presentation.components.CustomImage
import com.demo.authentication.features.presentation.components.CustomTextFieldForm
import com.demo.authentication.features.presentation.components.CustomTextForm
import com.demo.authentication.features.presentation.components.Divider
import com.demo.authentication.features.presentation.components.GoogleSignInButton
import com.demo.authentication.features.presentation.components.ScaffoldUi
import com.demo.authentication.features.presentation.login.LoginEvent.EnterEmail
import com.demo.authentication.features.presentation.login.LoginEvent.EnterPassword
import com.demo.authentication.features.presentation.login.LoginEvent.GoogleLogin
import com.demo.authentication.features.presentation.login.LoginEvent.Submit
import com.demo.authentication.features.presentation.login.LoginEvent.TogglePasswordVisibility

/*
If we are passing , hiltViewModel in login screen, preview don't work
so that' why we split it into two parts LoginScreenRoot and LoginScreen

in LoginScreenRoot -> we implement login viewmodel,event, action and state
in login screen we design the ui
 */
@Composable
fun LoginScreenRoot(
    loginViewModel: LoginViewModel = hiltViewModel(),
    onSignUpNavigate: () -> Unit,
    onHomeNavigate: () -> Unit,
) {
    val loginState = loginViewModel.loginState.collectAsState()
    val context = LocalContext.current

    // Set up GoogleAuthUiClient
    loginViewModel.googleAuthUiClient = remember {
        GoogleAuthUiClientImpl(context as ComponentActivity, loginViewModel.userAuthRepo)
    }

    // Auto-login using Credential Manager
    LaunchedEffect(Unit) {
        loginViewModel.userCredentialManagerLogin()
    }

    LaunchedEffect(loginState.value.loginResult) {

        loginState.value.loginResult.let { result ->
            when (result) {
                is Resource.Success -> {
                    Toast.makeText(context, result.data, Toast.LENGTH_SHORT).show()
                    loginViewModel.saveLoginStatus(true)
                    loginViewModel.clearSignInResult()

                    onHomeNavigate()
                }
                is Resource.Error -> {
                    Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                    loginViewModel.clearSignInResult()

                }
                else -> {} // do nothing
            }
        }
    }

    val loginAction = LoginAction (
        onEmailChange = { loginViewModel.handleIntent(EnterEmail(it)) },
        onPasswordChange = { loginViewModel.handleIntent(EnterPassword(it)) },
        onTogglePasswordVisibility = { loginViewModel.handleIntent(TogglePasswordVisibility) },
        onSubmit = { loginViewModel.handleIntent(Submit) },
        onGoogleLogin = { loginViewModel.handleIntent(GoogleLogin) },
        onSignUpNavigate = onSignUpNavigate,
        isButtonEnabled = loginViewModel.isValidateInput()
    )
    LoginScreen(
        loginState = loginState.value,
        loginAction = loginAction
    )
}

@Composable
fun LoginScreen(
    loginState: LoginState,
    loginAction: LoginAction
) {

    val focusManager = LocalFocusManager.current  // Get focus manager

    ScaffoldUi(showToolBar = false) {
        Spacer(modifier = Modifier.padding(top = 20.dp))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        )
        {
            CustomImage(
                imageInt = R.drawable.app_icon,
                contentDescription = R.string.app_icon,
                modifier = Modifier
                    .size(width = 150.dp, height = 150.dp)
                    .padding(10.dp)
            )
        }
        Spacer(modifier = Modifier.padding(top = 40.dp))
        CustomTextFieldForm(
            value = loginState.emailId,
            onValueChange = { loginAction.onEmailChange(it) },
            label = R.string.email_id,
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 5.dp),
            singleLine = true,
            isError = loginState.emailIdError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,  // Normal text input
                imeAction = ImeAction.Next  // Move to next field
            ),
            placeholder = R.string.email_id_placeholder,
            leadingIcon = Icons.Filled.Email,
            contentDescription = R.string.email_id_placeholder,
        )

        CustomTextFieldForm(
            value = loginState.password,
            onValueChange = { loginAction.onPasswordChange(it) },
            label = R.string.password,
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 5.dp),
            singleLine = true,
            isError = loginState.passwordError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,  // Normal text input
                imeAction = ImeAction.Done  // Move to next field
            ),
            placeholder = R.string.password_placeholder,
            leadingIcon = Icons.Filled.Lock,
            trailingIcon = if (loginState.showPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
            contentDescription = R.string.password_placeholder,
            leadingContentDescription = if (loginState.showPassword) R.string.show_password else R.string.hide_password,
            onTrailingIconClicked = { loginAction.onTogglePasswordVisibility() },
            visualTransformation = if (loginState.showPassword) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
        )

        CustomButton(
            isButtonEnabled = loginAction.isButtonEnabled,
            onClick = {
                focusManager.clearFocus()  // Hide keyboard
                loginAction.onSubmit()
            },
            icon = Icons.Filled.CheckCircleOutline,
            buttonContent = stringResource(R.string.sign_in)
        )
        Divider()
        GoogleSignInButton(onClick = { loginAction.onGoogleLogin() })

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        )
        {
            CustomTextForm(text = R.string.dont_have_acc)
            CustomTextForm(
                text = R.string.sign_up,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .clickable {
                        loginAction.onSignUpNavigate()
                    },
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }

    if (loginState.isLoading) {
        CircularProgressBar()
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    LoginScreen(
        loginState = LoginState(),
        loginAction = LoginAction(),
    )
}
