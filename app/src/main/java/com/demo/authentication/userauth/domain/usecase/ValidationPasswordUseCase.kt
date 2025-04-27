package com.demo.authentication.userauth.domain.usecase

import javax.inject.Inject
import com.demo.authentication.R
import com.demo.authentication.core.domain.utils.ResourceProvider

class ValidationPasswordUseCase @Inject constructor(private val resourceProvider: ResourceProvider) {
    operator fun invoke(password: String, confPassword : String = ""): ValidationResult {
        if (password.length < 6) {
            return ValidationResult(
                successful = false,
                errorMessage = resourceProvider.getString(R.string.invalid_password_length)
            )
        }
        val containsDigit = password.any { it.isDigit() }
        if (!containsDigit) {
            return ValidationResult(
                successful = false,
                errorMessage = resourceProvider.getString(R.string.invalid_password_digit)
            )
        }
        val containsUppercase = password.any { it.isUpperCase() }
        if (!containsUppercase) {
            return ValidationResult(
                successful = false,
                errorMessage = resourceProvider.getString(R.string.invalid_password_uppercase)
            )
        }

        if (confPassword.isNotEmpty() && password != confPassword) {
            return ValidationResult(
                successful = false,
                errorMessage = resourceProvider.getString(R.string.password_mismatch)
            )
        }
        return ValidationResult(
            successful = true
        )
    }
}