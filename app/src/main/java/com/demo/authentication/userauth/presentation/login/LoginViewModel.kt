package com.demo.authentication.userauth.presentation.login

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.demo.authentication.core.domain.repository.DataStoreAuthPreferences
import com.demo.authentication.core.domain.utils.AppResult
import com.demo.authentication.core.domain.utils.NetworkError
import com.demo.authentication.core.domain.utils.onError
import com.demo.authentication.core.domain.utils.onSuccess
import com.demo.authentication.core.presentation.utils.isValidEmail
import com.demo.authentication.core.presentation.utils.isValidPassword
import com.demo.authentication.userauth.domain.repository.AuthRepository
import com.demo.authentication.userauth.domain.repository.CredentialManagement
import com.demo.authentication.userauth.presentation.login.LoginEvent.EnterEmail
import com.demo.authentication.userauth.presentation.login.LoginEvent.EnterPassword
import com.demo.authentication.userauth.presentation.login.LoginEvent.GoogleLogin
import com.demo.authentication.userauth.presentation.login.LoginEvent.Submit
import com.demo.authentication.userauth.presentation.login.LoginEvent.TogglePasswordVisibility
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(
    val authRepository: AuthRepository,
    val credentialManagement: CredentialManagement,
    private val dataStoreAuthPreferences: DataStoreAuthPreferences,
) : ViewModel() {

    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val _loginResult = MutableSharedFlow<AppResult<FirebaseUser, NetworkError>>()
    val loginResult = _loginResult.asSharedFlow()

    val coroutineExceptionHandler: CoroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            Log.e("CoroutineError", "Exception caught: ${throwable.localizedMessage}")
        }

    suspend fun saveLoginStatus(loginStatus: Boolean) {
        dataStoreAuthPreferences.saveLoginStatus(loginStatus)
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
                // googleSignIn()
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

    private fun submitLogin() {
        viewModelScope.launch(coroutineExceptionHandler) {
            getState { it.copy(isLoading = true) }

            if (isValidateInput()) {

                authRepository.signIn(_loginState.value.emailId, _loginState.value.password)
                    .onSuccess { user ->
                        getState {
                            it.copy(
                                isLoading = false,
                            )
                        }
                        _loginResult.emit(AppResult.Success(user))
                        saveLoginStatus(true)
                    }
                    .onError { error ->
                        getState {
                            it.copy(
                                isLoading = false,
                            )
                        }
                        _loginResult.emit(AppResult.Error(error))
                    }
            } else {
                getState { it.copy(isLoading = false) }
            }
        }
    }
}