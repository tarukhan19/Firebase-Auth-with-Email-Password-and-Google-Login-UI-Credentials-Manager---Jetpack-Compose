package com.demo.authentication.userauth.presentation.login

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.authentication.core.domain.repository.DataStoreAuthPreferences
import com.demo.authentication.core.domain.utils.AppResult
import com.demo.authentication.core.domain.utils.NetworkError
import com.demo.authentication.core.domain.utils.onError
import com.demo.authentication.core.domain.utils.onSuccess
import com.demo.authentication.userauth.domain.model.User
import com.demo.authentication.userauth.domain.usecase.CredentialSignInUseCase
import com.demo.authentication.userauth.domain.usecase.ValidationEmailIdUseCase
import com.demo.authentication.userauth.domain.usecase.GoogleSignInUseCase
import com.demo.authentication.userauth.domain.usecase.ValidationPasswordUseCase
import com.demo.authentication.userauth.domain.usecase.SignInUseCase
import com.demo.authentication.userauth.presentation.login.LoginEvent.EnterEmail
import com.demo.authentication.userauth.presentation.login.LoginEvent.EnterPassword
import com.demo.authentication.userauth.presentation.login.LoginEvent.TogglePasswordVisibility
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
@Inject
constructor(
    private val dataStoreAuthPreferences: DataStoreAuthPreferences,
    private val signInUseCase: SignInUseCase,
    private val credentialSignInUseCase: CredentialSignInUseCase,
    private val googleSignInUseCase: GoogleSignInUseCase,
    private val emailValidationUseCase: ValidationEmailIdUseCase,
    private val passwordValidationUseCase: ValidationPasswordUseCase
) : ViewModel() {

    private val _loginState = MutableStateFlow(LoginState())
    val loginState = _loginState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        LoginState()
    )

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

                val emailResult = emailValidationUseCase(loginIntent.email)
                getState {
                    it.copy(
                        emailId = loginIntent.email,
                        emailIdError = if (emailResult.successful) null else emailResult.errorMessage,
                        isLoginButtonEnabled = validateInputs()
                    )
                }
            }

            is EnterPassword -> {
                val passwordResult = passwordValidationUseCase(loginIntent.password)
                getState {
                    it.copy(
                        password = loginIntent.password,
                        passwordError = if (passwordResult.successful) null else passwordResult.errorMessage,
                        isLoginButtonEnabled = validateInputs()
                    )
                }
            }

            is TogglePasswordVisibility -> {
                getState {
                    it.copy(showPassword = !it.showPassword)
                }
            }
        }
    }

    private fun getState(update: (LoginState) -> LoginState) {
        _loginState.update {
            update(_loginState.value)
        }
    }

    fun validateInputs(): Boolean {
        val state = _loginState.value

        val emailResult = emailValidationUseCase(state.emailId)
        val passwordResult = passwordValidationUseCase(state.password)

        return emailResult.successful && passwordResult.successful
    }

     fun submitLogin() {
        viewModelScope.launch(coroutineExceptionHandler) {
            getState { it.copy(isLoading = true) }
            signInUseCase(_loginState.value.emailId, _loginState.value.password).collect { result ->
                handleAuthResult(result)
            }
        }
    }


    fun signInWithGoogle(context: Context) {
        viewModelScope.launch(coroutineExceptionHandler) {
            getState { it.copy(isLoading = true) }

            googleSignInUseCase(context).collect { result ->
                handleAuthResult(result)

            }
        }
    }

    fun getCredential(context: Context) {
        viewModelScope.launch(coroutineExceptionHandler) {
            getState { it.copy(isLoading = true) }

            credentialSignInUseCase(
                context
            ).collect { result ->
                result.onSuccess { user ->
                    getState {
                        it.copy(emailId = user.email, password = user.password, isLoading = false)
                    }
                    submitLogin()
                }.onError {
                    getState { it.copy(isLoading = false) }
                }
            }
        }
    }

    suspend fun handleAuthResult(result: AppResult<User, NetworkError>) {

        when (result) {
            is AppResult.Success -> {
                getState {
                    it.copy(
                        isLoading = false,
                        loginResult = AppResult.Success(result.data),
                    )
                }
                saveLoginStatus(true)
            }

            is AppResult.Error -> {
                getState {
                    it.copy(
                        isLoading = false,
                        loginResult = AppResult.Error(result.error),
                    )
                }
            }
        }
    }
}
