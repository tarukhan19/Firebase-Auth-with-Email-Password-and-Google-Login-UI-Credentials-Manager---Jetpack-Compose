package com.demo.authentication.userauth.presentation.login

data class LoginState(
    val emailId: String = "",
    val password: String = "",
    val emailIdError: Boolean = false,
    val passwordError: Boolean = false,
    val showPassword: Boolean = false,
    val isLoading: Boolean = false,
)
