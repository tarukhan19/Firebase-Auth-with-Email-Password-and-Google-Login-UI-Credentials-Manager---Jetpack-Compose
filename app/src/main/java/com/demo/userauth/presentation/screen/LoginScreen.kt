package com.demo.userauth.presentation.screen

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.demo.userauth.R
import com.demo.userauth.presentation.components.CircularProgressBar
import com.demo.userauth.presentation.components.CustomButton
import com.demo.userauth.presentation.components.CustomImage
import com.demo.userauth.presentation.components.CustomTextFieldForm
import com.demo.userauth.presentation.components.CustomTextForm
import com.demo.userauth.presentation.components.GoogleSignInButton
import com.demo.userauth.presentation.components.ScaffoldUi
import com.demo.userauth.presentation.intent.LoginIntent.EnterEmail
import com.demo.userauth.presentation.intent.LoginIntent.EnterPassword
import com.demo.userauth.presentation.intent.LoginIntent.GoogleLogin
import com.demo.userauth.presentation.intent.LoginIntent.Submit
import com.demo.userauth.presentation.intent.LoginIntent.TogglePasswordVisibility
import com.demo.userauth.presentation.theme.primaryColor
import com.demo.userauth.presentation.viewmodel.LoginViewModel
import com.demo.userauth.repository.GoogleAuthUiClient
import com.demo.userauth.utils.Resource

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = hiltViewModel(),
    onSignUpNavigate: () -> Unit,
    onHomeNavigate: () -> Unit,
    ) {
    val loginState = loginViewModel.loginState.collectAsState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current  // Get focus manager
    loginViewModel.googleAuthUiClient = remember {
        GoogleAuthUiClient(context as ComponentActivity)
    }

    LaunchedEffect(loginState.value.loginResult) {
        loginState.value.loginResult.let { result ->
            when (result) {
                is Resource.Success -> {
                    Toast.makeText(context, result.data, Toast.LENGTH_SHORT).show()
                    loginViewModel.saveLoginStatus(true)
                    onHomeNavigate()
                }

                is Resource.Error -> {
                    Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                }

                else -> {} // do nothing
            }
        }
    }

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
            value = loginState.value.emailId,
            onValueChange = { loginViewModel.handleIntent(EnterEmail(it)) },
            label = R.string.email_id,
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 5.dp),
            singleLine = true,
            isError = loginState.value.emailIdError,
            keyboardType = KeyboardType.Email,
            placeholder = R.string.email_id_placeholder,
            leadingIcon = Icons.Filled.Email,
            contentDescription = R.string.email_id_placeholder,
        )

        CustomTextFieldForm(
            value = loginState.value.password,
            onValueChange = { loginViewModel.handleIntent(EnterPassword(it)) },
            label = R.string.password,
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 5.dp),
            singleLine = true,
            isError = loginState.value.passwordError,
            keyboardType = KeyboardType.Password,
            placeholder = R.string.password_placeholder,
            leadingIcon = Icons.Filled.Lock,
            trailingIcon = if (loginState.value.showPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
            contentDescription = R.string.password_placeholder,
            leadingContentDescription = if (loginState.value.showPassword) R.string.show_password else R.string.hide_password,
            onTrailingIconClicked = { loginViewModel.handleIntent(TogglePasswordVisibility) },
            visualTransformation = if (loginState.value.showPassword) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
        )

        CustomButton(
            isButtonEnabled = loginViewModel.isValidateInput(),
            onClick = {
                focusManager.clearFocus()  // Hide keyboard
                loginViewModel.handleIntent(Submit)
            },
            icon = Icons.Filled.CheckCircleOutline,
            buttonContent = stringResource(R.string.sign_in)
        )

        Spacer(modifier = Modifier.padding(top = 10.dp))

        GoogleSignInButton (onClick = { loginViewModel.handleIntent(GoogleLogin) })

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
                    .clickable { onSignUpNavigate() },
                fontSize = 16.sp,
                color = primaryColor,
                fontWeight = FontWeight.Bold
            )
        }
    }

    if (loginState.value.isLoading) {
        CircularProgressBar()
    }
}