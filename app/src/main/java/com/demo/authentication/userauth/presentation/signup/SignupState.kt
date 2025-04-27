package com.demo.authentication.userauth.presentation.signup

import com.demo.authentication.core.domain.utils.AppResult
import com.demo.authentication.core.domain.utils.NetworkError
import com.demo.authentication.userauth.domain.model.User

data class SignupState(
    val fullName: String = "",
    val emailId: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val phoneNumber: String = "",
    val fullNameError: String? = "",
    val emailIdError: String? = "",
    val passwordError: String? = "",
    val confPasswordError: String? = "",
    val passwordMismatchError: String? = "",
    val phoneNumberError: String? = "",
    val showPassword: Boolean = false,
    val showConfirmPassword: Boolean = false,
    val isTncAccepted: Boolean = false,
    val isLoading: Boolean = false,
    val isSignupButtonEnabled: Boolean = false,
    val signupResult: AppResult<Any, NetworkError>? = null,

    )
