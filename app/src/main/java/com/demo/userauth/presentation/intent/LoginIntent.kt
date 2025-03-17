package com.demo.userauth.presentation.intent

sealed interface LoginIntent {
    data class EnterEmail(val email: String) : LoginIntent
    data class EnterPassword(val password: String)  : LoginIntent

    object TogglePasswordVisibility : LoginIntent
    object Submit : LoginIntent
    object GoogleLogin : LoginIntent
}