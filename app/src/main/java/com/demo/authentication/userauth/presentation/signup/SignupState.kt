package com.demo.authentication.userauth.presentation.signup

data class SignupState (
    val fullName : String = "",
    val emailId : String = "",
    val password : String = "",
    val confirmPassword : String = "",
    val phoneNumber : String = "",

    val fullNameError : Boolean = false,
    val emailIdError : Boolean = false,
    val passwordError : Boolean = false,
    val confPasswordError : Boolean = false,
    val passwordMismatchError : Boolean = false,
    val phoneNumberError: Boolean = false,

    val showPassword : Boolean = false,
    val showConfirmPassword : Boolean = false,

    val isTncAccepted: Boolean = false,

    val isLoading: Boolean = false,

)