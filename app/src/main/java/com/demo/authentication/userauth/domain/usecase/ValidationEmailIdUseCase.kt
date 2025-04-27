package com.demo.authentication.userauth.domain.usecase

import android.util.Patterns
import javax.inject.Inject
import com.demo.authentication.R
import com.demo.authentication.core.domain.utils.ResourceProvider

class ValidationEmailIdUseCase @Inject constructor(private val resourceProvider: ResourceProvider) {
    operator fun invoke(email: String): ValidationResult {
        if (email.isBlank()) {
            return ValidationResult(
                successful = false,
                errorMessage = resourceProvider.getString(R.string.invalid_email_id)
            )
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return ValidationResult(
                successful = false,
                errorMessage = resourceProvider.getString(R.string.invalid_email_format)
            )
        }
        return ValidationResult(
            successful = true
        )
    }
}