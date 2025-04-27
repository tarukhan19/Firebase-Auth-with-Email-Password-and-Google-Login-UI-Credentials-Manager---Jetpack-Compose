package com.demo.authentication.userauth.domain.usecase

import android.content.Context
import com.demo.authentication.core.domain.utils.AppResult
import com.demo.authentication.core.domain.utils.NetworkError
import com.demo.authentication.userauth.domain.model.User
import com.demo.authentication.userauth.domain.repository.CredentialManagementRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CredentialSignInUseCase @Inject constructor(
    private val credentialManagementRepository: CredentialManagementRepository
) {
    suspend operator fun invoke(context: Context) : Flow<AppResult<User, NetworkError>> {
        return credentialManagementRepository.launchGetCredential(context)
    }
}