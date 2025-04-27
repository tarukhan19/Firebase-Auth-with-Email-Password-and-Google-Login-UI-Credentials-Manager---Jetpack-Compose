package com.demo.authentication.userauth.domain.usecase

import com.demo.authentication.R
import com.demo.authentication.core.domain.utils.ResourceProvider
import javax.inject.Inject

class ValidationMobileNumberUseCase @Inject constructor(private val resourceProvider: ResourceProvider) {
    operator fun invoke(mobileNumber: String): ValidationResult {
        if (mobileNumber.length < 9) {
            return ValidationResult(
                successful = false,
                errorMessage = resourceProvider.getString(R.string.invalid_mobile_number_length)
            )
        }
        return ValidationResult(
            successful = true
        )
    }
}