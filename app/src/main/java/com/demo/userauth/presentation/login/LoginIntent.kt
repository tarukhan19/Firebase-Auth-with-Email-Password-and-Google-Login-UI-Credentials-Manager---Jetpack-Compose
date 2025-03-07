package com.demo.userauth.presentation.login

import com.demo.userauth.presentation.signup.SignupIntent

sealed interface LoginIntent {
    data class EnterEmail(val email: String) : LoginIntent
    data class EnterPassword(val password: String)  : LoginIntent

    object TogglePasswordVisibility : LoginIntent
    object Submit : LoginIntent

}