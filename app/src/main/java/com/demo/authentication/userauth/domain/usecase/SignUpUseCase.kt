package com.demo.authentication.userauth.domain.usecase

import com.demo.authentication.core.domain.utils.AppResult
import com.demo.authentication.core.domain.utils.NetworkError
import com.demo.authentication.userauth.domain.model.User
import com.demo.authentication.userauth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        name: String,
        mobileNumber: String
    ): Flow<AppResult<User, NetworkError>> {
        return repository.signUp(email, password, name, mobileNumber)
    }
}