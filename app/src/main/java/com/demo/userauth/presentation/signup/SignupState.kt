package com.demo.userauth.presentation.signup

import com.demo.userauth.utils.Resource

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

    val isLoading: Boolean = false,
    val signupResult: Resource<String>? = null
)