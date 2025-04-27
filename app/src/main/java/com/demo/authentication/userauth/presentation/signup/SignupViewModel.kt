package com.demo.authentication.userauth.presentation.signup

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.authentication.core.domain.repository.DataStoreAuthPreferences
import com.demo.authentication.core.domain.utils.AppResult
import com.demo.authentication.userauth.domain.usecase.CredentialRegisterUseCase
import com.demo.authentication.userauth.domain.usecase.ValidationEmailIdUseCase
import com.demo.authentication.userauth.domain.usecase.ValidationMobileNumberUseCase
import com.demo.authentication.userauth.domain.usecase.ValidationNameUseCase
import com.demo.authentication.userauth.domain.usecase.ValidationPasswordUseCase
import com.demo.authentication.userauth.domain.usecase.SignUpUseCase
import com.demo.authentication.userauth.presentation.signup.SignupEvent.EnterConfirmPassword
import com.demo.authentication.userauth.presentation.signup.SignupEvent.EnterEmail
import com.demo.authentication.userauth.presentation.signup.SignupEvent.EnterFullName
import com.demo.authentication.userauth.presentation.signup.SignupEvent.EnterPassword
import com.demo.authentication.userauth.presentation.signup.SignupEvent.EnterPhoneNumber
import com.demo.authentication.userauth.presentation.signup.SignupEvent.ToggleConfirmPasswordVisibility
import com.demo.authentication.userauth.presentation.signup.SignupEvent.TogglePasswordVisibility
import com.demo.authentication.userauth.presentation.signup.SignupEvent.ToggleTnc
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
* in MVI, state changes should be immutable so handled via StateFlow, not MutableState.

* getState is a higher-order function, meaning it takes another function (update: (SignupState) -> SignupState)
 as a parameter.The update function takes a SignupState as input and returns a modified SignupState

* validateName(), validateMobileNumber(), validateEmailId(), and validatePassword() return true for valid input,
 and validateInput() expects  to return true for valid input.

* MutableStateFlow is a hot flow that holds a state and emits updates.
It starts with an initial value, SignupState(), which means it already has a value before any collector subscribes.
* asStateFlow() converts MutableStateFlow into an immutable StateFlow. This ensures that only ViewModel can
modify _signUpState, and the UI (or other classes) can only read from signUpState

* updateFullName and other methods , modifies _signUpState, and since StateFlow is hot, the UI immediately gets updated.

* userRegister returns a cold Flow that emits:
Resource.Loading() (indicating loading state)
Resource.Success("Registration successful") (on success)
Resource.Error("Signup failed") (on failure)
* collect is a terminal operator that starts collecting the flow.
Every time userRegister emits a new value, collect receives it as result.


* UI State (signUpState) is a continuously active state, always holding the latest form data and UI updates.
We use StateFlow (Hot Flow) to ensure that the UI always has the latest values, even after recompositions or re-subscriptions.

* The Signup API (userRegister()) is an on-demand operation that should only execute when explicitly triggered.
 Using Flow (Cold Flow) prevents unnecessary API calls and ensures efficient resource management.

* The @Inject annotation is used for constructor injection. This means that Dagger or Hilt
will automatically provide an instance of UserAuthRepo when creating SignupViewModel.

* StateFlow in ViewModel: Ensures a single source of truth for UI state.
✔️ collectAsState() in Compose: Converts StateFlow into State<T> to trigger recomposition.
✔️ Why use asStateFlow()? Prevents modification of _loginState outside ViewModel
* */

@HiltViewModel
class SignupViewModel
@Inject
constructor(
    private val dataStoreAuthPreferences: DataStoreAuthPreferences,
    private val signUpUseCase: SignUpUseCase,
    private val credentialRegisterUseCase: CredentialRegisterUseCase,
    private val emailValidationUseCase: ValidationEmailIdUseCase,
    private val passwordValidationUseCase: ValidationPasswordUseCase,
    private val nameValidationUseCase: ValidationNameUseCase,
    private val mobileNumberValidationUseCase: ValidationMobileNumberUseCase
) : ViewModel() {
    private val _signUpState = MutableStateFlow(SignupState())
    val signUpState = _signUpState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        SignupState()
    )

    val coroutineExceptionHandler: CoroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            Log.e("CoroutineError", "Exception caught: ${throwable.localizedMessage}")
        }

    fun handleIntent(signupIntent: SignupEvent) {
        when (signupIntent) {
            is EnterFullName -> {
                val nameResult = nameValidationUseCase(signupIntent.fullName)
                getState {
                    it.copy(
                        fullName = signupIntent.fullName,
                        fullNameError = if (nameResult.successful) null else nameResult.errorMessage,
                        isSignupButtonEnabled = validateInputs()
                    )
                }
            }

            is EnterEmail -> {
                val emailIdResult = emailValidationUseCase(signupIntent.emailId)
                getState {
                    it.copy(
                        emailId = signupIntent.emailId,
                        emailIdError = if (emailIdResult.successful) null else emailIdResult.errorMessage,
                        isSignupButtonEnabled = validateInputs()
                    )
                }
            }

            is EnterPassword -> {
                val passwordResult = passwordValidationUseCase(
                    signupIntent.password,
                    _signUpState.value.confirmPassword
                )
                getState {
                    it.copy(
                        password = signupIntent.password,
                        passwordError = if (passwordResult.successful) null else passwordResult.errorMessage,
                        isSignupButtonEnabled = validateInputs()
                    )
                }
            }

            is EnterConfirmPassword -> {
                val confPasswordResult = passwordValidationUseCase(
                    signupIntent.confirmPassword,
                    _signUpState.value.password
                )
                getState {
                    it.copy(
                        confirmPassword = signupIntent.confirmPassword,
                        confPasswordError = if (confPasswordResult.successful) null else confPasswordResult.errorMessage,
                        isSignupButtonEnabled = validateInputs()
                    )
                }
            }

            is EnterPhoneNumber -> {
                val mobileNumberResult = mobileNumberValidationUseCase(signupIntent.phoneNumber)

                getState {
                    it.copy(
                        phoneNumber = signupIntent.phoneNumber,
                        phoneNumberError = if (mobileNumberResult.successful) null else mobileNumberResult.errorMessage,
                        isSignupButtonEnabled = validateInputs()

                    )
                }
            }

            is TogglePasswordVisibility -> {
                getState {
                    it.copy(
                        showPassword = !it.showPassword,
                    )
                }
            }

            is ToggleConfirmPasswordVisibility -> {
                getState {
                    it.copy(
                        showConfirmPassword = !it.showConfirmPassword,
                    )
                }
            }

            is ToggleTnc -> {
                getState { it.copy(isTncAccepted = !it.isTncAccepted) }
            }
        }
    }

    fun validateInputs(): Boolean {
        val state = _signUpState.value

        val nameResult = nameValidationUseCase(state.fullName)
        val mobileNumberResult = mobileNumberValidationUseCase(state.phoneNumber)
        val emailResult = emailValidationUseCase(state.emailId)
        val passwordResult = passwordValidationUseCase(state.password)
        val confPasswordResult = passwordValidationUseCase(state.confirmPassword)
        val passwordsMatchResult = passwordValidationUseCase(
            state.password,
            state.confirmPassword
        )
        val isTncAccepted = state.isTncAccepted

        return isTncAccepted && nameResult.successful && mobileNumberResult.successful && emailResult.successful &&
                passwordResult.successful && confPasswordResult.successful && passwordsMatchResult.successful
    }

    fun registerUser(context: Context) {
        viewModelScope.launch(coroutineExceptionHandler) {
            getState { it.copy(isLoading = true) }

            signUpUseCase(
                _signUpState.value.emailId,
                _signUpState.value.password,
                _signUpState.value.fullName,
                _signUpState.value.phoneNumber
            ).collect { result ->
                when (result) {
                    is AppResult.Success -> {
                        saveCredential(context)
                        getState {
                            it.copy(
                                isLoading = false,
                                signupResult = AppResult.Success(result.data),
                            )
                        }
                    }

                    is AppResult.Error -> {
                        getState {
                            it.copy(
                                isLoading = false,
                                signupResult = AppResult.Error(result.error),
                            )
                        }
                    }
                }
            }
        }
    }

    private fun saveCredential(context: Context) {
        viewModelScope.launch(coroutineExceptionHandler) {
            credentialRegisterUseCase(
                _signUpState.value.emailId,
                _signUpState.value.password,
                context
            ).collect { result ->
                saveLoginStatus(true)
            }
        }
    }

    suspend fun saveLoginStatus(loginStatus: Boolean) {
        dataStoreAuthPreferences.saveLoginStatus(loginStatus)
    }

    private fun getState(update: (SignupState) -> SignupState) {
        _signUpState.update {
            update(_signUpState.value)
        }
    }
}
