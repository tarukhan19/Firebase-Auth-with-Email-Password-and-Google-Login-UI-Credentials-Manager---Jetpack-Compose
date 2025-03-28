package com.demo.authentication.features.presentation.login

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.demo.authentication.features.data.repository.GoogleAuthUiClient
import com.demo.authentication.features.data.repository.UserAuthRepo
import com.demo.authentication.core.domain.utils.Resource
import com.demo.authentication.core.presentation.utils.isValidEmail
import com.demo.authentication.core.presentation.utils.isValidPassword
import com.demo.authentication.features.data.datastore.UserPreferences
import com.demo.authentication.features.presentation.login.LoginEvent.EnterEmail
import com.demo.authentication.features.presentation.login.LoginEvent.EnterPassword
import com.demo.authentication.features.presentation.login.LoginEvent.GoogleLogin
import com.demo.authentication.features.presentation.login.LoginEvent.Submit
import com.demo.authentication.features.presentation.login.LoginEvent.TogglePasswordVisibility
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(
    val userAuthRepo: UserAuthRepo,
    private val userPreferences: UserPreferences,
) : ViewModel() {

    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    lateinit var googleAuthUiClient: GoogleAuthUiClient

    val coroutineExceptionHandler: CoroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            Log.e("CoroutineError", "Exception caught: ${throwable.localizedMessage}")
        }

    suspend fun saveLoginStatus(loginStatus: Boolean) {
        userPreferences.saveLoginStatus(loginStatus)
    }

    fun handleIntent(loginIntent: LoginEvent) {
        when (loginIntent) {
            is EnterEmail -> {
                getState {
                    it.copy(
                        emailId = loginIntent.email,
                        emailIdError = loginIntent.email.isValidEmail()
                    )
                }
            }

            is EnterPassword -> {
                getState {
                    it.copy(
                        password = loginIntent.password,
                        passwordError = loginIntent.password.isValidPassword()
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

            is GoogleLogin -> {
                googleSignIn()
            }
        }
    }

    private fun getState(update: (LoginState) -> LoginState) {
        _loginState.update {
            update(_loginState.value)
        }
    }

    fun isValidateInput(): Boolean {
        val state = _loginState.value
        return !state.emailId.isValidEmail() && !state.password.isValidPassword()
    }

    private fun googleSignIn() {
        getState { it.copy(isLoading = true) }

        viewModelScope.launch(coroutineExceptionHandler) {
            val result = googleAuthUiClient.googleSignIn()
            getState { it.copy(isLoading = false, loginResult = result) }
        }
    }

    fun userCredentialManagerLogin() {
        getState { it.copy(isLoading = true) }

        viewModelScope.launch(coroutineExceptionHandler) {
            val result = googleAuthUiClient.userCredentialManagerLogin()
            getState { it.copy(isLoading = false, loginResult = result) }
        }
    }

    private fun submitLogin() {
        viewModelScope.launch(coroutineExceptionHandler) {
            getState { it.copy(isLoading = true) }

            if (isValidateInput()) {
                userAuthRepo.userDatabaseLogin(_loginState.value.emailId, _loginState.value.password)
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

    fun clearSignInResult() {
        getState { it.copy(loginResult = null) }
    }
}