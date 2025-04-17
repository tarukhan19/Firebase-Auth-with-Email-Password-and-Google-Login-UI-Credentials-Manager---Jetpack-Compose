package com.demo.authentication.userauth.presentation.signup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.authentication.core.domain.utils.AppResult
import com.demo.authentication.core.domain.utils.NetworkError
import com.demo.authentication.core.domain.utils.onError
import com.demo.authentication.core.domain.utils.onSuccess
import com.demo.authentication.core.presentation.utils.isValidEmail
import com.demo.authentication.core.presentation.utils.isValidName
import com.demo.authentication.core.presentation.utils.isValidPassword
import com.demo.authentication.core.presentation.utils.isValidPhoneNumber
import com.demo.authentication.core.presentation.utils.matchesPassword
import com.demo.authentication.userauth.domain.repository.AuthRepository
import com.demo.authentication.userauth.domain.repository.CredentialManagementRepository
import com.demo.authentication.userauth.presentation.signup.SignupEvent.EnterConfirmPassword
import com.demo.authentication.userauth.presentation.signup.SignupEvent.EnterEmail
import com.demo.authentication.userauth.presentation.signup.SignupEvent.EnterFullName
import com.demo.authentication.userauth.presentation.signup.SignupEvent.EnterPassword
import com.demo.authentication.userauth.presentation.signup.SignupEvent.EnterPhoneNumber
import com.demo.authentication.userauth.presentation.signup.SignupEvent.Submit
import com.demo.authentication.userauth.presentation.signup.SignupEvent.ToggleConfirmPasswordVisibility
import com.demo.authentication.userauth.presentation.signup.SignupEvent.TogglePasswordVisibility
import com.demo.authentication.userauth.presentation.signup.SignupEvent.ToggleTnc
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
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
        val authRepository: AuthRepository,
        val credentialManagement: CredentialManagementRepository,
    ) : ViewModel() {
        private val _signUpState = MutableStateFlow(SignupState())
        val signUpState: StateFlow<SignupState> = _signUpState.asStateFlow()

        private val _signUpResult = MutableSharedFlow<AppResult<FirebaseUser, NetworkError>>()
        val signUpResult = _signUpResult.asSharedFlow()

        val coroutineExceptionHandler: CoroutineExceptionHandler =
            CoroutineExceptionHandler { _, throwable ->
                Log.e("CoroutineError", "Exception caught: ${throwable.localizedMessage}")
            }

        fun handleIntent(signupIntent: SignupEvent) {
            when (signupIntent) {
                is EnterFullName -> {
                    getState {
                        it.copy(
                            fullName = signupIntent.fullName,
                            fullNameError = (signupIntent.fullName.isValidName()),
                        )
                    }
                }

                is EnterEmail -> {
                    getState {
                        it.copy(
                            emailId = signupIntent.emailId,
                            emailIdError = (signupIntent.emailId.isValidEmail()),
                        )
                    }
                }

                is EnterPassword -> {
                    getState {
                        val confirmPassword = _signUpState.value.confirmPassword
                        it.copy(
                            password = signupIntent.password,
                            passwordError = (signupIntent.password.isValidPassword()),
                            passwordMismatchError =
                                signupIntent.password.matchesPassword(
                                    confirmPassword,
                                ),
                        )
                    }
                }

                is EnterConfirmPassword -> {
                    getState {
                        val password = _signUpState.value.password
                        it.copy(
                            confirmPassword = signupIntent.confirmPassword,
                            confPasswordError = signupIntent.confirmPassword.isValidPassword(),
                            passwordMismatchError =
                                signupIntent.confirmPassword.matchesPassword(
                                    password,
                                ),
                        )
                    }
                }

                is EnterPhoneNumber -> {
                    getState {
                        it.copy(
                            phoneNumber = signupIntent.phoneNumber,
                            phoneNumberError = (signupIntent.phoneNumber.isValidPhoneNumber()),
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

                is Submit -> {
                    registerUser()
                }
            }
        }

//    fun userCredentialManagerRegister() {
//        getState { it.copy(isLoading = true) }
//
//        viewModelScope.launch(coroutineExceptionHandler) {
//            val result = googleAuthUiClient.userCredentialManagerRegister(
//                _signUpState.value.emailId,
//                _signUpState.value.password
//            )
//            getState { it.copy(isLoading = false, credentialSignupResult = result) }
//        }
//    }

        private fun registerUser() {
            viewModelScope.launch(coroutineExceptionHandler) {
                getState { it.copy(isLoading = true) }
                if (validateInput()) {
                    authRepository
                        .signUp(
                            _signUpState.value.emailId,
                            _signUpState.value.password,
                            _signUpState.value.fullName,
                            _signUpState.value.phoneNumber,
                        ).onSuccess { user ->
                            getState {
                                it.copy(
                                    isLoading = false,
                                )
                            }
                            _signUpResult.emit(AppResult.Success(user))
                        }.onError { error ->
                            getState {
                                it.copy(
                                    isLoading = false,
                                )
                            }
                            _signUpResult.emit(AppResult.Error(error))
                        }
                } else {
                    getState { it.copy(isLoading = false) }
                }
            }
        }

        private fun getState(update: (SignupState) -> SignupState) {
            _signUpState.update {
                update(_signUpState.value)
            }
        }

        fun validateInput(): Boolean {
            val state = _signUpState.value
            return (
                !state.fullName.isValidName() &&
                    !state.phoneNumber.isValidPhoneNumber() &&
                    !state.emailId.isValidEmail() &&
                    !state.password.isValidPassword() &&
                    !state.password.matchesPassword(state.confirmPassword) &&
                    state.isTncAccepted
            )
        }
    }
