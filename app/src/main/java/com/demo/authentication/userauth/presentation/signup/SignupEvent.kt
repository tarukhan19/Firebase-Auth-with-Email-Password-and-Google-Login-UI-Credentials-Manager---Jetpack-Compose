package com.demo.authentication.userauth.presentation.signup

sealed interface SignupEvent {

    data class EnterFullName (val fullName: String) : SignupEvent
    data class EnterPhoneNumber (val phoneNumber : String) : SignupEvent
    data class EnterEmail(val emailId: String) : SignupEvent
    data class EnterPassword(val password: String)  : SignupEvent
    data class EnterConfirmPassword(val confirmPassword: String)  : SignupEvent

    object Submit : SignupEvent
    object ToggleTnc : SignupEvent

    object TogglePasswordVisibility : SignupEvent
    object ToggleConfirmPasswordVisibility : SignupEvent

}