package com.demo.authentication.userauth.domain.usecase

import android.content.Context
import com.demo.authentication.core.domain.utils.AppResult
import com.demo.authentication.core.domain.utils.NetworkError
import com.demo.authentication.userauth.domain.repository.CredentialManagementRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CredentialRegisterUseCase @Inject constructor(
    private val credentialManagementRepository: CredentialManagementRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        context: Context
    ) : Flow<AppResult<Unit, NetworkError>>{
       return credentialManagementRepository.launchCreateCredential(context, email, password)
    }
}