package com.demo.authentication.userauth.presentation.login

sealed interface LoginEvent {
    data class EnterEmail(
        val email: String,
    ) : LoginEvent

    data class EnterPassword(
        val password: String,
    ) : LoginEvent

    object TogglePasswordVisibility : LoginEvent
}
