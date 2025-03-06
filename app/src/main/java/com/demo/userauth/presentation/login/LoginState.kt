package com.demo.userauth.presentation.login

data class LoginState (
    val emailId : String = "",
    val password : String = "",
    val emailIdError : Boolean = false,
    val passwordError : Boolean = false,
    val isLoading : Boolean = false,
    val isSuccess : Boolean = false,
    val errorMessage : String = ""
)