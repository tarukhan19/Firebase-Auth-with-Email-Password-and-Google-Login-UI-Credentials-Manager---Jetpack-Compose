package com.demo.userauth.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import android.util.Patterns
import androidx.lifecycle.viewModelScope
import com.demo.userauth.data.datastore.UserPreferences
import com.demo.userauth.presentation.intent.LoginIntent
import com.demo.userauth.presentation.intent.LoginIntent.EnterEmail
import com.demo.userauth.presentation.intent.LoginIntent.EnterPassword
import com.demo.userauth.presentation.intent.LoginIntent.Submit
import com.demo.userauth.presentation.intent.LoginIntent.TogglePasswordVisibility
import com.demo.userauth.presentation.state.LoginState
import com.demo.userauth.repository.UserAuthRepo
import com.demo.userauth.utils.Resource
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userAuthRepo: UserAuthRepo,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    val coroutineExceptionHandler: CoroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            Log.e("CoroutineError", "Exception caught: ${throwable.localizedMessage}")
        }

    suspend fun saveLoginStatus(loginStatus : Boolean) {
        userPreferences.saveLoginStatus(loginStatus)
    }

    fun handleIntent(loginIntent: LoginIntent) {
        when (loginIntent) {
            is EnterEmail -> {
                getState {
                    it.copy(
                        emailId = loginIntent.email,
                        emailIdError = validateEmailId(loginIntent.email)
                    )
                }
            }

            is EnterPassword -> {
                getState {
                    it.copy(
                        password = loginIntent.password,
                        passwordError = validatePassword(loginIntent.password)
                    )
                }
            }

            is TogglePasswordVisibility -> {
                getState {
                    it.copy(showPassword = !it.showPassword)
                }
            }

            is Submit -> {
                submitLogin()
            }
        }
    }

    private fun getState(update: (LoginState) -> LoginState) {
        _loginState.value = update(_loginState.value)
    }

    /*

    Returns true if:
    The email is empty (emailId.isEmpty()).
    The email format is incorrect (!Patterns.EMAIL_ADDRESS.matcher(emailId).matches()).
    Returns false if the email is valid.
    */

    private fun validateEmailId(emailId: String): Boolean {
        return (emailId.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailId).matches())
    }

    /*
    Returns true if:
    The password has 6 or fewer characters (invalid password).
    Returns false if the password is strong enough.
     */

    private fun validatePassword(password: String): Boolean {
        return password.length <= 6
    }

    /*
    Returns true if:
    The email is valid (!validateEmailId(state.emailId) → email check passes).
    The password is valid (!validatePassword(state.password) → password check passes).
    Returns false if either email or password is invalid.
     */

    fun isValidateInput(): Boolean {
        val state = _loginState.value
        return !validateEmailId(state.emailId) && !validatePassword(state.password)
    }

    private fun submitLogin() {
        viewModelScope.launch(coroutineExceptionHandler) {
            getState { it.copy(isLoading = true) }

            if (isValidateInput()) {
                userAuthRepo.userLogin(_loginState.value.emailId, _loginState.value.password)
                    .catch { e ->
                        getState {
                            it.copy(
                                isLoading = false,
                                loginResult = Resource.Error("Login failed: ${e.localizedMessage}")
                            )
                        }
                    }
                    .collect { result ->
                        getState { it.copy(isLoading = false, loginResult = result) }
                    }
            } else {
                getState { it.copy(isLoading = false) }
            }
        }
    }
}