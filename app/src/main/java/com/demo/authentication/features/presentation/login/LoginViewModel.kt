package com.demo.authentication.features.presentation.login

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.demo.authentication.core.domain.utils.AppResult
import com.demo.authentication.core.domain.utils.onError
import com.demo.authentication.core.domain.utils.onSuccess
import com.demo.authentication.core.presentation.utils.isValidEmail
import com.demo.authentication.core.presentation.utils.isValidPassword
import com.demo.authentication.features.data.datastore.UserPreferences
import com.demo.authentication.features.data.repository.GoogleAuthUiClientImpl
import com.demo.authentication.features.domain.usecase.SignInUseCase
import com.demo.authentication.features.presentation.login.LoginEvent.EnterEmail
import com.demo.authentication.features.presentation.login.LoginEvent.EnterPassword
import com.demo.authentication.features.presentation.login.LoginEvent.GoogleLogin
import com.demo.authentication.features.presentation.login.LoginEvent.Submit
import com.demo.authentication.features.presentation.login.LoginEvent.TogglePasswordVisibility
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(
    val signInUseCase: SignInUseCase,
    private val userPreferences: UserPreferences,
) : ViewModel() {

    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

   // lateinit var googleAuthUiClient: GoogleAuthUiClientImpl

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

//    private fun googleSignIn() {
//        getState { it.copy(isLoading = true) }
//
//        viewModelScope.launch(coroutineExceptionHandler) {
//            val result = googleAuthUiClient.googleSignIn()
//            getState { it.copy(isLoading = false, loginResult = result) }
//        }
//    }

//    fun userCredentialManagerLogin() {
//        getState { it.copy(isLoading = true) }
//
//        viewModelScope.launch(coroutineExceptionHandler) {
//            val result = googleAuthUiClient.userCredentialManagerLogin()
//            getState { it.copy(isLoading = false, loginResult = result) }
//        }
//    }

    private fun submitLogin() {
        viewModelScope.launch(coroutineExceptionHandler) {
            getState { it.copy(isLoading = true) }

            if (isValidateInput()) {

                signInUseCase(_loginState.value.emailId, _loginState.value.password)
                    .onSuccess { user ->
                        getState {
                            it.copy(
                                isLoading = false,
                                loginResult = AppResult.Success(user)
                            )
                        }
                    }
                    .onError { error ->
                        getState {
                            it.copy(
                                isLoading = false,
                                loginResult = AppResult.Error(error)
                            )
                        }
                    }
            } else {
                getState { it.copy(isLoading = false) }
            }
        }
    }


}