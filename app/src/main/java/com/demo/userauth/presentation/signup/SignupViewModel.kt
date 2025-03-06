package com.demo.userauth.presentation.signup

import android.util.Log
import android.util.Patterns
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.demo.userauth.presentation.signup.SignupIntent.EnterConfirmPassword
import com.demo.userauth.presentation.signup.SignupIntent.EnterEmail
import com.demo.userauth.presentation.signup.SignupIntent.EnterFullName
import com.demo.userauth.presentation.signup.SignupIntent.EnterPassword
import com.demo.userauth.presentation.signup.SignupIntent.EnterPhoneNumber
import com.demo.userauth.presentation.signup.SignupIntent.Submit
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

@HiltViewModel
class SignupViewModel @Inject constructor() : ViewModel() {
    var _signUpState by mutableStateOf(SignupState())
        private set
    var showPassword: Boolean by mutableStateOf(false)
    var showConfPassword: Boolean by mutableStateOf(false)
    val coroutineExceptionHandler: CoroutineExceptionHandler = CoroutineExceptionHandler { _,throwable ->
        Log.e("CoroutineError", "Exception caught: ${throwable.localizedMessage}")
    }

    fun handleIntent(signupIntent: SignupIntent) {
        when (signupIntent) {
            is EnterFullName -> {
                updateFullName(signupIntent.fullName)
            }

            is EnterEmail -> {
                updateEmail(signupIntent.emailId)
            }

            is EnterPassword -> {
                updatePassword(signupIntent.password)
            }

            is EnterConfirmPassword -> {
                updateConfPassword(signupIntent.confirmPassword)
            }

            is EnterPhoneNumber -> {
                updatePhoneNumber(signupIntent.phoneNumber)
            }

            is Submit -> {
                registerUser()
            }
        }
    }

    private fun registerUser() {
        viewModelScope.launch(coroutineExceptionHandler) {
            val emailIdError = validateEmailId(_signUpState.emailId)
            val passwordError = validatePassword(_signUpState.password)
            val fullNameError = validateName(_signUpState.fullName)
            val phoneNumber = validateMobileNumber(_signUpState.phoneNumber)
            
//            if (!emailIdError && !passwordError && !fullNameError && !phoneNumber){
//                val userEntity= UserEntity(
//                    emailId = _signUpState.emailId,
//                    phoneNumber = _signUpState.phoneNumber,
//                    fullName = _signUpState.fullName,
//                    password = _signUpState.password
//                )
//                val result = userDao.insertUser(userEntity)
//            }
        }
    }

    private fun updateFullName(fullName: String) {
        validateName(fullName).let { isValid ->
            _signUpState = _signUpState.copy(fullName = fullName, fullNameError = isValid)
        }
    }

    private fun updateEmail(emailId: String) {
        validateEmailId(emailId).let { isValid ->
            _signUpState = _signUpState.copy(emailId = emailId, emailIdError = isValid)
        }
    }

    private fun updatePassword(password: String) {
        validatePassword(password).let { isValid ->
            _signUpState = _signUpState.copy(password = password, passwordError = isValid)
        }

        validatePasswordMatch()
    }

    private fun updateConfPassword(password: String) {
        validatePassword(password).let { isValid ->
            _signUpState = _signUpState.copy(confirmPassword = password, confPasswordError = isValid)
        }
        validatePasswordMatch()
    }

    private fun updatePhoneNumber(phoneNumber: String) {
        validateMobileNumber(phoneNumber).let { isValid ->
            _signUpState = _signUpState.copy(phoneNumber = phoneNumber , phoneNumberError =  isValid)
        }
    }

    private fun validatePasswordMatch() {
        if (_signUpState.confirmPassword.length > 6 && _signUpState.password.length > 6)
        { _signUpState = _signUpState.copy(
            passwordMismatchError = _signUpState.password != _signUpState.confirmPassword
        )}
    }

    private fun validateName(fullName: String): Boolean { return (fullName.isEmpty() || fullName.length <= 4) }

    private fun validateMobileNumber(phoneNumber: String): Boolean { return (phoneNumber.isEmpty() || phoneNumber.length < 9) }

    private fun validateEmailId(emailId: String): Boolean { return (emailId.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailId).matches()) }

    private fun validatePassword(password: String): Boolean { return (password.length <= 6) }
}