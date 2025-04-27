package com.demo.authentication.userauth.domain.usecase

import android.content.Context
import com.demo.authentication.core.domain.utils.AppResult
import com.demo.authentication.core.domain.utils.NetworkError
import com.demo.authentication.userauth.domain.model.User
import com.demo.authentication.userauth.domain.repository.AuthRepository
import com.demo.authentication.userauth.domain.repository.GoogleAuthUiClientRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val repository: AuthRepository
) {
   suspend operator fun invoke(email: String, password: String): Flow<AppResult<User, NetworkError>> {
        return repository.signIn(email, password)
    }
}