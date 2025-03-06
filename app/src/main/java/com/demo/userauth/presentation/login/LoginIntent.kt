package com.demo.userauth.presentation.login

sealed interface LoginIntent {
    data class EnterEmail(val email: String) : LoginIntent
    data class EnterPassword(val password: String)  : LoginIntent
    data object Submit : LoginIntent
}