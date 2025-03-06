package com.demo.userauth.presentation.login

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import android.util.Patterns
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.demo.userauth.presentation.login.LoginIntent.EnterEmail
import com.demo.userauth.presentation.login.LoginIntent.EnterPassword
import com.demo.userauth.presentation.login.LoginIntent.Submit
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(savedStateHandle: SavedStateHandle) : ViewModel() {

    var _loginState by mutableStateOf(LoginState())
        private set
    var showPassword: Boolean by mutableStateOf(false)

    fun handleIntent(loginIntent: LoginIntent) {
        when (loginIntent) {
            is EnterEmail -> {
                updateEmailId(loginIntent.email)
            }

            is EnterPassword -> {
                updatePassword(loginIntent.password)
            }

            is Submit -> {
                submitLogin()
            }
        }
    }

    private fun updateEmailId(emailId: String) {
        validateEmailId(emailId).let { isValid ->
            _loginState = _loginState.copy(emailId = emailId, emailIdError = isValid)
        }
    }

    private fun updatePassword(password: String) {
        validatePassword(password).let { isValid ->
            _loginState = _loginState.copy(password = password, passwordError = isValid)
        }
    }

    private fun validateEmailId(emailId: String): Boolean {
        return (emailId.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailId).matches())
    }

    private fun validatePassword(password: String): Boolean {
        return password.length <= 6
    }

    private fun submitLogin() {
        viewModelScope.launch {
            val emailIdError = validateEmailId(_loginState.emailId)
            val passwordError = validatePassword(_loginState.password)

            if (emailIdError && passwordError) {
                _loginState = _loginState.copy(isLoading = true)
                delay(2000)

                _loginState = _loginState.copy(isLoading = false, isSuccess = true)
            } else {
                _loginState =
                    _loginState.copy(emailIdError = emailIdError, passwordError = passwordError)
            }
        }
    }
}