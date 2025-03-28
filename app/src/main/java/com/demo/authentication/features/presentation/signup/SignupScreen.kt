package com.demo.authentication.features.presentation.signup

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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.demo.authentication.R
import com.demo.authentication.features.data.repository.GoogleAuthUiClient
import com.demo.authentication.core.domain.utils.Resource
import com.demo.authentication.features.presentation.components.CircularProgressBar
import com.demo.authentication.features.presentation.components.CustomButton
import com.demo.authentication.features.presentation.components.CustomImage
import com.demo.authentication.features.presentation.components.CustomTextFieldForm
import com.demo.authentication.features.presentation.components.CustomTextForm
import com.demo.authentication.features.presentation.components.ScaffoldUi
import com.demo.authentication.features.presentation.signup.SignupIntent.EnterConfirmPassword
import com.demo.authentication.features.presentation.signup.SignupIntent.EnterEmail
import com.demo.authentication.features.presentation.signup.SignupIntent.EnterFullName
import com.demo.authentication.features.presentation.signup.SignupIntent.EnterPassword
import com.demo.authentication.features.presentation.signup.SignupIntent.EnterPhoneNumber
import com.demo.authentication.features.presentation.signup.SignupIntent.Submit
import com.demo.authentication.features.presentation.signup.SignupIntent.TogglePasswordVisibility
import com.demo.authentication.features.presentation.signup.SignupIntent.ToggleTnc

@Composable
fun SignUpRoot(
    signupViewModel: SignupViewModel = hiltViewModel(),
    onLogInNavigate: () -> Unit
) {
    val signupState = signupViewModel.signUpState.collectAsState()
    val context = LocalContext.current

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

    val signupAction = SignupAction(
        onFullNameChange = { signupViewModel.handleIntent(EnterFullName(it)) },
        onEmailChange = { signupViewModel.handleIntent(EnterEmail(it)) },
        onPasswordChange = { signupViewModel.handleIntent(EnterPassword(it)) },
        onConfPasswordChange = { signupViewModel.handleIntent(EnterConfirmPassword(it)) },
        onTogglePasswordVisibility = { signupViewModel.handleIntent(TogglePasswordVisibility) },
        onToggleConfPasswordVisibility = { signupViewModel.handleIntent(SignupIntent.ToggleConfirmPasswordVisibility) },
        onMobileNoChange = { signupViewModel.handleIntent(EnterPhoneNumber(it)) },
        onSubmit = { signupViewModel.handleIntent(Submit) },
        onTncCheck = { signupViewModel.handleIntent(ToggleTnc)} ,
        onSignInNavigate = onLogInNavigate,
        isButtonEnabled = signupViewModel.validateInput(),
    )

    SignupScreen(
        signupState = signupState.value,
        signupAction = signupAction
    )
}

@Composable
fun SignupScreen(
    signupAction: SignupAction,
    signupState : SignupState
) {

    val focusManager = LocalFocusManager.current
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
            value = signupState.fullName,
            onValueChange = { signupAction.onFullNameChange(it) },
            label = R.string.full_name,
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 5.dp),
            singleLine = true,
            isError = signupState.fullNameError,
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
            value = signupState.emailId,
            onValueChange = { signupAction.onEmailChange(it) },
            label = R.string.email_id,
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 5.dp),
            singleLine = true,
            isError = signupState.emailIdError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,  // Normal text input
                imeAction = ImeAction.Next  // Move to next field
            ),
            placeholder = R.string.email_id_placeholder,
            leadingIcon = Icons.Filled.Email,
            contentDescription = R.string.email_id_placeholder,
        )

        CustomTextFieldForm(
            value = signupState.password,
            onValueChange = { signupAction.onPasswordChange(it) },
            label = R.string.password,
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 5.dp),
            singleLine = true,
            isError = signupState.passwordError || signupState.passwordMismatchError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,  // Normal text input
                imeAction = ImeAction.Next  // Move to next field
            ),
            placeholder = R.string.password_placeholder,
            leadingIcon = Icons.Filled.Lock,
            trailingIcon = if (signupState.showPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
            contentDescription = R.string.password_placeholder,
            leadingContentDescription = if (signupState.showPassword) R.string.show_password else R.string.hide_password,
            onTrailingIconClicked = {
                signupAction.onTogglePasswordVisibility()
            },
            visualTransformation = if (signupState.showPassword) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
        )

        CustomTextFieldForm(
            value = signupState.confirmPassword,
            onValueChange = { signupAction.onConfPasswordChange(it) },
            label = R.string.confirm_password,
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 5.dp),
            singleLine = true,
            isError = signupState.confPasswordError || signupState.passwordMismatchError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,  // Normal text input
                imeAction = ImeAction.Next  // Move to next field
            ),
            placeholder = R.string.conf_password_placeholder,
            leadingIcon = Icons.Filled.Lock,
            trailingIcon = if (signupState.showConfirmPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
            contentDescription = R.string.password_placeholder,
            leadingContentDescription = if (signupState.showConfirmPassword) R.string.show_password else R.string.hide_password,
            onTrailingIconClicked = { signupAction.onToggleConfPasswordVisibility() },
            visualTransformation = if (signupState.showConfirmPassword) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
        )

        CustomTextFieldForm(
            value = signupState.phoneNumber,
            onValueChange = { signupAction.onMobileNoChange(it) },
            label = R.string.phone_number,
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 5.dp),
            singleLine = true,
            isError = signupState.phoneNumberError,
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
                checked = signupState.isTncAccepted,
                onCheckedChange = { signupAction.onTncCheck(it) }
            )
            CustomTextForm(text = R.string.tnc_text)
        }

        CustomButton(
            isButtonEnabled = signupAction.isButtonEnabled,
            onClick = {
                focusManager.clearFocus()
                signupAction.onSubmit()
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
                    .clickable { signupAction.onSignInNavigate() },
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }

    if (signupState.isLoading) {
        CircularProgressBar()
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSignupScreen() {
    SignupScreen(
        signupState = SignupState(),
        signupAction = SignupAction(),
    )
}