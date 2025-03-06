package com.demo.userauth.presentation.signup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MobileFriendly
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.demo.userauth.R
import com.demo.userauth.presentation.components.CustomButton
import com.demo.userauth.presentation.components.CustomImage
import com.demo.userauth.presentation.components.CustomTextFieldForm
import com.demo.userauth.presentation.components.CustomTextForm
import com.demo.userauth.presentation.components.ScaffoldUi
import com.demo.userauth.presentation.theme.primaryColor
import com.demo.userauth.presentation.signup.SignupIntent.EnterConfirmPassword
import com.demo.userauth.presentation.signup.SignupIntent.EnterPassword
import com.demo.userauth.presentation.signup.SignupIntent.EnterEmail
import com.demo.userauth.presentation.signup.SignupIntent.EnterFullName
import com.demo.userauth.presentation.signup.SignupIntent.EnterPhoneNumber
import com.demo.userauth.presentation.signup.SignupIntent.Submit

@Composable
fun SignupScreen(signupViewModel: SignupViewModel = hiltViewModel(),
                 onLogInNavigate : () -> Unit) {
    val signupState = signupViewModel._signUpState

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
                onValueChange = { signupViewModel.handleIntent(EnterFullName(it)) },
                label = R.string.full_name,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 5.dp),
                singleLine = true,
                isError = signupState.fullNameError,
                keyboardType = KeyboardType.Text,
                placeholder = R.string.full_name_placeholder,
                leadingIcon = Icons.Filled.Person,
                contentDescription = R.string.full_name_placeholder,
            )

            CustomTextFieldForm(
                value = signupState.emailId,
                onValueChange = { signupViewModel.handleIntent(EnterEmail(it)) },
                label = R.string.email_id,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 5.dp),
                singleLine = true,
                isError = signupState.emailIdError,
                keyboardType = KeyboardType.Email,
                placeholder = R.string.email_id_placeholder,
                leadingIcon = Icons.Filled.Email,
                contentDescription = R.string.email_id_placeholder,
            )

            CustomTextFieldForm(
                value = signupState.password,
                onValueChange = { signupViewModel.handleIntent(EnterPassword(it)) },
                label = R.string.password,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 5.dp),
                singleLine = true,
                isError = signupState.passwordError || signupState.passwordMismatchError,
                keyboardType = KeyboardType.Password,
                placeholder = R.string.password_placeholder,
                leadingIcon = Icons.Filled.Lock,
                trailingIcon = if (signupViewModel.showPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                contentDescription = R.string.password_placeholder,
                leadingContentDescription = if (signupViewModel.showPassword) R.string.show_password else R.string.hide_password,
                onTrailingIconClicked = {
                    signupViewModel.showPassword = !signupViewModel.showPassword
                },
                visualTransformation = if (signupViewModel.showPassword) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
            )

            CustomTextFieldForm(
                value = signupState.confirmPassword,
                onValueChange = { signupViewModel.handleIntent(EnterConfirmPassword(it)) },
                label = R.string.confirm_password,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 5.dp),
                singleLine = true,
                isError = signupState.confPasswordError || signupState.passwordMismatchError,
                keyboardType = KeyboardType.Password,
                placeholder = R.string.conf_password_placeholder,
                leadingIcon = Icons.Filled.Lock,
                trailingIcon = if (signupViewModel.showConfPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                contentDescription = R.string.password_placeholder,
                leadingContentDescription = if (signupViewModel.showConfPassword) R.string.show_password else R.string.hide_password,
                onTrailingIconClicked = {
                    signupViewModel.showConfPassword = !signupViewModel.showConfPassword
                },
                visualTransformation = if (signupViewModel.showConfPassword) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
            )

            CustomTextFieldForm(
                value = signupState.phoneNumber,
                onValueChange = { signupViewModel.handleIntent(EnterPhoneNumber(it)) },
                label = R.string.phone_number,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 5.dp),
                singleLine = true,
                isError = signupState.phoneNumberError,
                keyboardType = KeyboardType.Number,
                placeholder = R.string.phone_number_placeholder,
                leadingIcon = Icons.Filled.MobileFriendly,
                contentDescription = R.string.phone_number_placeholder,
            )

            CustomButton(
                isButtonEnabled = !signupState.passwordMismatchError && !signupState.emailIdError &&
                        !signupState.fullNameError && !signupState.phoneNumberError &&
                        signupState.emailId.isNotEmpty() && signupState.password.isNotEmpty(),
                onClick = { signupViewModel.handleIntent(Submit) },
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
                    modifier = Modifier.padding(start = 10.dp).clickable{ onLogInNavigate() },
                    fontSize = 16.sp,
                    color = primaryColor,
                    fontWeight = FontWeight.Bold
                )
            }
    }
}