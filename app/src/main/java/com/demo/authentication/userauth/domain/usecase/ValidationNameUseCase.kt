package com.demo.authentication.userauth.domain.usecase

import javax.inject.Inject
import com.demo.authentication.R
import com.demo.authentication.core.domain.utils.ResourceProvider

class ValidationNameUseCase  @Inject constructor(private val resourceProvider: ResourceProvider) {
    operator fun invoke(name: String): ValidationResult {
        if (name.isBlank()) {
            return ValidationResult(
                successful = false,
                errorMessage = resourceProvider.getString(R.string.invalid_name)
            )
        }
        return ValidationResult(
            successful = true
        )
    }
}