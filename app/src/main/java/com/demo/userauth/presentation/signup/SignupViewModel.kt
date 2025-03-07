package com.demo.userauth.presentation.signup

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.demo.userauth.data.local.entity.UserEntity
import com.demo.userauth.presentation.signup.SignupIntent.EnterConfirmPassword
import com.demo.userauth.presentation.signup.SignupIntent.EnterEmail
import com.demo.userauth.presentation.signup.SignupIntent.EnterFullName
import com.demo.userauth.presentation.signup.SignupIntent.EnterPassword
import com.demo.userauth.presentation.signup.SignupIntent.EnterPhoneNumber
import com.demo.userauth.presentation.signup.SignupIntent.Submit
import com.demo.userauth.presentation.signup.SignupIntent.ToggleConfirmPasswordVisibility
import com.demo.userauth.presentation.signup.SignupIntent.TogglePasswordVisibility
import com.demo.userauth.repository.UserAuthRepo
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
class SignupViewModel @Inject constructor(private val userAuthRepo: UserAuthRepo) : ViewModel() {

    private val _signUpState = MutableStateFlow(SignupState())
    val signUpState: StateFlow<SignupState> = _signUpState.asStateFlow()

    val coroutineExceptionHandler: CoroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            Log.e("CoroutineError", "Exception caught: ${throwable.localizedMessage}")
        }

    fun handleIntent(signupIntent: SignupIntent) {
        when (signupIntent) {
            is EnterFullName -> {
                getState {
                    it.copy(
                        fullName = signupIntent.fullName,
                        fullNameError = validateName(signupIntent.fullName)
                    )
                }
            }

            is EnterEmail -> {
                getState {
                    it.copy(
                        emailId = signupIntent.emailId,
                        emailIdError = validateEmailId(signupIntent.emailId)
                    )
                }
            }

            is EnterPassword -> {
                getState {
                    val confirmPassword = _signUpState.value.confirmPassword
                    it.copy(
                        password = signupIntent.password,
                        passwordError = validatePassword(signupIntent.password),
                        passwordMismatchError = validatePasswordMatch(
                            signupIntent.password,
                            confirmPassword
                        )
                    )
                }
            }

            is EnterConfirmPassword -> {
                getState {
                    val password = _signUpState.value.password
                    it.copy(
                        confirmPassword = signupIntent.confirmPassword,
                        confPasswordError = validatePassword(signupIntent.confirmPassword),
                        passwordMismatchError = validatePasswordMatch(
                            password,
                            signupIntent.confirmPassword
                        )
                    )
                }
            }

            is EnterPhoneNumber -> {
                getState {
                    it.copy(
                        phoneNumber = signupIntent.phoneNumber,
                        phoneNumberError = validateMobileNumber(signupIntent.phoneNumber)
                    )
                }
            }

            is TogglePasswordVisibility -> {
                getState {
                    it.copy(
                        showPassword = !it.showPassword
                    )
                }
            }

            is ToggleConfirmPasswordVisibility -> {
                getState {
                    it.copy(
                        showConfirmPassword = !it.showConfirmPassword
                    )
                }
            }

            is Submit -> {
                registerUser()
            }

        }
    }

    private fun registerUser() {
        viewModelScope.launch(coroutineExceptionHandler) {

            getState { it.copy(isLoading = true) }
            if (validateInput()) {
                userAuthRepo.userRegister(
                    UserEntity(
                        emailId = _signUpState.value.emailId,
                        phoneNumber = _signUpState.value.phoneNumber,
                        fullName = _signUpState.value.fullName,
                        password = _signUpState.value.password
                    )
                ).collect { result ->
                    getState { it.copy(isLoading = false, signupResult = result) }
                }
            } else {
                getState { it.copy(isLoading = false) }
            }
        }
    }

    private fun getState(update: (SignupState) -> SignupState) {
        _signUpState.value = update(_signUpState.value)
    }

    fun validateInput(): Boolean {
        val state = _signUpState.value
        return (!validateName(state.fullName)
                && !validateMobileNumber(state.phoneNumber)
                && !validateEmailId(state.emailId)
                && !validatePassword(state.password))
                && !validatePasswordMatch(state.password, state.confirmPassword)
    }

    private fun validatePasswordMatch(password: String, confirmPassword: String): Boolean {
        return (password.length > 6 && confirmPassword.length > 6 && password != confirmPassword)
    }

    private fun validateName(fullName: String): Boolean {
        return (fullName.isEmpty() || fullName.length <= 4)
    }

    private fun validateMobileNumber(phoneNumber: String): Boolean {
        return (phoneNumber.isEmpty() || phoneNumber.length < 9)
    }

    private fun validateEmailId(emailId: String): Boolean {
        return (emailId.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailId).matches())
    }

    private fun validatePassword(password: String): Boolean {
        return (password.length <= 6)
    }
}