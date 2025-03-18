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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MobileFriendly
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.demo.userauth.R
import com.demo.userauth.presentation.components.CircularProgressBar
import com.demo.userauth.presentation.components.CustomButton
import com.demo.userauth.presentation.components.CustomImage
import com.demo.userauth.presentation.components.CustomTextFieldForm
import com.demo.userauth.presentation.components.CustomTextForm
import com.demo.userauth.presentation.components.ScaffoldUi
import com.demo.userauth.presentation.intent.SignupIntent
import com.demo.userauth.presentation.intent.SignupIntent.EnterConfirmPassword
import com.demo.userauth.presentation.intent.SignupIntent.EnterEmail
import com.demo.userauth.presentation.intent.SignupIntent.EnterFullName
import com.demo.userauth.presentation.intent.SignupIntent.EnterPassword
import com.demo.userauth.presentation.intent.SignupIntent.EnterPhoneNumber
import com.demo.userauth.presentation.intent.SignupIntent.Submit
import com.demo.userauth.presentation.theme.primaryColor
import com.demo.userauth.presentation.viewmodel.SignupViewModel
import com.demo.userauth.repository.GoogleAuthUiClient
import com.demo.userauth.utils.Resource

@Composable
fun SignupScreen(
    signupViewModel: SignupViewModel = hiltViewModel(),
    onLogInNavigate: () -> Unit
) {
    val signupState = signupViewModel.signUpState.collectAsState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    signupViewModel.googleAuthUiClient = remember {
        GoogleAuthUiClient(context as ComponentActivity, signupViewModel.userAuthRepo)
    }

    LaunchedEffect(signupState.value.signupResult) {
        signupState.value.signupResult.let { result ->
            when (result) {
                is Resource.Success -> {
                    signupViewModel.userCredentialManagerRegister()
                }

                is Resource.Error -> {
                    Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                    signupViewModel.clearSignupResult()
                }

                else -> {}// do nothing
            }
        }
    }

    LaunchedEffect(signupState.value.credentialSignupResult) {
        signupState.value.credentialSignupResult.let { result ->
            when (result) {
                is Resource.Success -> {
                    Toast.makeText(context, result.data, Toast.LENGTH_SHORT).show()
                    signupViewModel.clearSignupResult()
                }

                is Resource.Error -> {
                    Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                    signupViewModel.clearSignupResult()
                }

                else -> {}// do nothing
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

        Spacer(modifier = Modifier.padding(top = 10.dp))

        CustomTextFieldForm(
            value = signupState.value.fullName,
            onValueChange = { signupViewModel.handleIntent(EnterFullName(it)) },
            label = R.string.full_name,
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 5.dp),
            singleLine = true,
            isError = signupState.value.fullNameError,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,  // Capitalize first letter
                keyboardType = KeyboardType.Text,  // Normal text input
                imeAction = ImeAction.Next  // Move to next field
            ),
            placeholder = R.string.full_name_placeholder,
            leadingIcon = Icons.Filled.Person,
            contentDescription = R.string.full_name_placeholder,
        )

        CustomTextFieldForm(
            value = signupState.value.emailId,
            onValueChange = { signupViewModel.handleIntent(EnterEmail(it)) },
            label = R.string.email_id,
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 5.dp),
            singleLine = true,
            isError = signupState.value.emailIdError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,  // Normal text input
                imeAction = ImeAction.Next  // Move to next field
            ),
            placeholder = R.string.email_id_placeholder,
            leadingIcon = Icons.Filled.Email,
            contentDescription = R.string.email_id_placeholder,
        )

        CustomTextFieldForm(
            value = signupState.value.password,
            onValueChange = { signupViewModel.handleIntent(EnterPassword(it)) },
            label = R.string.password,
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 5.dp),
            singleLine = true,
            isError = signupState.value.passwordError || signupState.value.passwordMismatchError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,  // Normal text input
                imeAction = ImeAction.Next  // Move to next field
            ),
            placeholder = R.string.password_placeholder,
            leadingIcon = Icons.Filled.Lock,
            trailingIcon = if (signupState.value.showPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
            contentDescription = R.string.password_placeholder,
            leadingContentDescription = if (signupState.value.showPassword) R.string.show_password else R.string.hide_password,
            onTrailingIconClicked = {
                signupViewModel.handleIntent(SignupIntent.TogglePasswordVisibility)
            },
            visualTransformation = if (signupState.value.showPassword) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
        )

        CustomTextFieldForm(
            value = signupState.value.confirmPassword,
            onValueChange = { signupViewModel.handleIntent(EnterConfirmPassword(it)) },
            label = R.string.confirm_password,
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 5.dp),
            singleLine = true,
            isError = signupState.value.confPasswordError || signupState.value.passwordMismatchError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,  // Normal text input
                imeAction = ImeAction.Next  // Move to next field
            ),
            placeholder = R.string.conf_password_placeholder,
            leadingIcon = Icons.Filled.Lock,
            trailingIcon = if (signupState.value.showConfirmPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
            contentDescription = R.string.password_placeholder,
            leadingContentDescription = if (signupState.value.showConfirmPassword) R.string.show_password else R.string.hide_password,
            onTrailingIconClicked = {
                signupViewModel.handleIntent(SignupIntent.ToggleConfirmPasswordVisibility)
            },
            visualTransformation = if (signupState.value.showConfirmPassword) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
        )

        CustomTextFieldForm(
            value = signupState.value.phoneNumber,
            onValueChange = { signupViewModel.handleIntent(EnterPhoneNumber(it)) },
            label = R.string.phone_number,
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 5.dp),
            singleLine = true,
            isError = signupState.value.phoneNumberError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,  // Normal text input
                imeAction = ImeAction.Done  // Move to next field
            ),
            placeholder = R.string.phone_number_placeholder,
            leadingIcon = Icons.Filled.MobileFriendly,
            contentDescription = R.string.phone_number_placeholder,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = signupState.value.isTncAccepted,
                onCheckedChange = { signupViewModel.handleIntent(SignupIntent.ToggleTnc) }
            )
            CustomTextForm(text = R.string.tnc_text)
        }

        CustomButton(
            isButtonEnabled = signupViewModel.validateInput(),
            onClick = {
                focusManager.clearFocus()
                signupViewModel.handleIntent(Submit)
            },
            icon = Icons.Filled.CheckCircleOutline,
            buttonContent = stringResource(R.string.sign_up)
        )

        Spacer(modifier = Modifier.padding(top = 10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        )
        {
            CustomTextForm(text = R.string.already_have_acc)
            CustomTextForm(
                text = R.string.sign_in,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .clickable { onLogInNavigate() },
                fontSize = 16.sp,
                color = primaryColor,
                fontWeight = FontWeight.Bold
            )
        }
    }

    if (signupState.value.isLoading) {
        CircularProgressBar()
    }
}