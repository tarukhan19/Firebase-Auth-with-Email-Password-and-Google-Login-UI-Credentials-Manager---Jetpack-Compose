package com.demo.userauth.presentation.signup

sealed interface SignupIntent {
    data class EnterFullName (val fullName: String) : SignupIntent
    data class EnterPhoneNumber (val phoneNumber : String) : SignupIntent
    data class EnterEmail(val emailId: String) : SignupIntent
    data class EnterPassword(val password: String)  : SignupIntent
    data class EnterConfirmPassword(val confirmPassword: String)  : SignupIntent
    data object Submit : SignupIntent
}