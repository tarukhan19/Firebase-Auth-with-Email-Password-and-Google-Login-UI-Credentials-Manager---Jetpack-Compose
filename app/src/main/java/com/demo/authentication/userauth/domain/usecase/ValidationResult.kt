package com.demo.authentication.userauth.domain.usecase

data class ValidationResult(
    val successful: Boolean,
    val errorMessage: String = ""
)