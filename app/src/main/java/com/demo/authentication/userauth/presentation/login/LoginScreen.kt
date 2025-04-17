package com.demo.authentication.userauth.presentation.login

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.demo.authentication.R
import com.demo.authentication.core.domain.utils.AppResult
import com.demo.authentication.core.domain.utils.onError
import com.demo.authentication.core.domain.utils.onSuccess
import com.demo.authentication.core.presentation.utils.ObserveAsEvents
import com.demo.authentication.core.presentation.utils.toUserFriendlyMessage
import com.demo.authentication.userauth.presentation.components.CircularProgressBar
import com.demo.authentication.userauth.presentation.components.CustomButton
import com.demo.authentication.userauth.presentation.components.CustomImage
import com.demo.authentication.userauth.presentation.components.CustomTextFieldForm
import com.demo.authentication.userauth.presentation.components.CustomTextForm
import com.demo.authentication.userauth.presentation.components.Divider
import com.demo.authentication.userauth.presentation.components.GoogleSignInButton
import com.demo.authentication.userauth.presentation.components.ScaffoldUi
import com.demo.authentication.userauth.presentation.login.LoginEvent.EnterEmail
import com.demo.authentication.userauth.presentation.login.LoginEvent.EnterPassword
import com.demo.authentication.userauth.presentation.login.LoginEvent.Submit
import com.demo.authentication.userauth.presentation.login.LoginEvent.TogglePasswordVisibility
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

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
    val loginState = loginViewModel.loginState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val loginAction =
        LoginAction(
            onEmailChange = { loginViewModel.handleIntent(EnterEmail(it)) },
            onPasswordChange = { loginViewModel.handleIntent(EnterPassword(it)) },
            onTogglePasswordVisibility = { loginViewModel.handleIntent(TogglePasswordVisibility) },
            onSubmit = { loginViewModel.handleIntent(Submit) },
            onGoogleLogin = { loginViewModel.signInWithGoogle(context) },
            onSignUpNavigate = onSignUpNavigate,
            isButtonEnabled = loginViewModel.isValidateInput(),
        )

    LaunchedEffect(Unit) {
        loginViewModel.credentialManagement.launchGetCredential(context) { response ->
            response.onSuccess { credential ->
                loginAction.onEmailChange(credential.id)
                loginAction.onPasswordChange(credential.password)
                loginAction.onSubmit()
            }
            response.onError { error ->
                Toast
                    .makeText(
                        context,
                        error.toUserFriendlyMessage(),
                        Toast.LENGTH_SHORT,
                    ).show()
            }
        }
    }

    ObserveAsEvents(loginViewModel.googleSignInResult) { result ->
        when (result) {
            is AppResult.Success -> {
                Toast
                    .makeText(context, "Login Successful!", Toast.LENGTH_SHORT)
                    .show()
                onHomeNavigate()
            }
            is AppResult.Error -> {
                Toast
                    .makeText(context, result.error.toUserFriendlyMessage(), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    ObserveAsEvents(loginViewModel.loginResult) { result ->
        when (result) {
            is AppResult.Success -> {
                coroutineScope.launch {
                    loginViewModel.credentialManagement.launchCreateCredential(
                        context = context,
                        email = loginState.value.emailId,
                        password = loginState.value.password,
                    ) { response ->
                        response
                            .onSuccess {
                                Toast
                                    .makeText(context, "Login Successful!", Toast.LENGTH_SHORT)
                                    .show()
                                onHomeNavigate()
                            }.onError {
                                Toast
                                    .makeText(
                                        context,
                                        it.toUserFriendlyMessage(),
                                        Toast.LENGTH_SHORT,
                                    ).show()
                            }
                    }
                }
            }

            is AppResult.Error -> {
                Toast
                    .makeText(context, result.error.toUserFriendlyMessage(), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    LoginScreen(
        loginState = loginState.value,
        loginAction = loginAction,
    )
}

@Composable
fun LoginScreen(
    loginState: LoginState,
    loginAction: LoginAction,
) {
    val focusManager = LocalFocusManager.current // Get focus manager

    ScaffoldUi(showToolBar = false) {
        Spacer(modifier = Modifier.padding(top = 20.dp))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            CustomImage(
                imageInt = R.drawable.app_icon,
                contentDescription = R.string.app_icon,
                modifier =
                    Modifier
                        .size(width = 150.dp, height = 150.dp)
                        .padding(10.dp),
            )
        }
        Spacer(modifier = Modifier.padding(top = 40.dp))
        CustomTextFieldForm(
            value = loginState.emailId,
            onValueChange = { loginAction.onEmailChange(it) },
            label = R.string.email_id,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(all = 5.dp),
            singleLine = true,
            isError = loginState.emailIdError,
            keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Email, // Normal text input
                    imeAction = ImeAction.Next, // Move to next field
                ),
            placeholder = R.string.email_id_placeholder,
            leadingIcon = Icons.Filled.Email,
            contentDescription = R.string.email_id_placeholder,
        )

        CustomTextFieldForm(
            value = loginState.password,
            onValueChange = { loginAction.onPasswordChange(it) },
            label = R.string.password,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(all = 5.dp),
            singleLine = true,
            isError = loginState.passwordError,
            keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Password, // Normal text input
                    imeAction = ImeAction.Done, // Move to next field
                ),
            placeholder = R.string.password_placeholder,
            leadingIcon = Icons.Filled.Lock,
            trailingIcon = if (loginState.showPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
            contentDescription = R.string.password_placeholder,
            leadingContentDescription = if (loginState.showPassword) R.string.show_password else R.string.hide_password,
            onTrailingIconClicked = { loginAction.onTogglePasswordVisibility() },
            visualTransformation =
                if (loginState.showPassword) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
        )

        CustomButton(
            isButtonEnabled = loginAction.isButtonEnabled,
            onClick = {
                focusManager.clearFocus() // Hide keyboard
                loginAction.onSubmit()
            },
            icon = Icons.Filled.CheckCircleOutline,
            buttonContent = stringResource(R.string.sign_in),
        )
        Divider()
        GoogleSignInButton(onClick = { loginAction.onGoogleLogin() })

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            CustomTextForm(text = R.string.dont_have_acc)
            CustomTextForm(
                text = R.string.sign_up,
                modifier =
                    Modifier
                        .padding(start = 10.dp)
                        .clickable {
                            loginAction.onSignUpNavigate()
                        },
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
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
