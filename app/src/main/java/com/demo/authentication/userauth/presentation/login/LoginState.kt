package com.demo.authentication.userauth.presentation.login

import com.demo.authentication.core.domain.utils.AppResult
import com.demo.authentication.core.domain.utils.NetworkError
import com.demo.authentication.userauth.domain.model.User

data class LoginState(
    val emailId: String = "",
    val password: String = "",
    val emailIdError: String? = "",
    val passwordError: String? = "",
    val showPassword: Boolean = false,
    val isLoading: Boolean = false,
    val isLoginButtonEnabled: Boolean = false,
    val loginResult: AppResult<User, NetworkError>? = null,
)
