package com.demo.authentication.userauth.data.repository

import android.content.Context
import androidx.credentials.CreatePasswordRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetPasswordOption
import androidx.credentials.PasswordCredential
import com.demo.authentication.core.domain.utils.AppResult
import com.demo.authentication.core.domain.utils.NetworkError
import com.demo.authentication.userauth.domain.repository.CredentialManagementRepository
import javax.inject.Inject

class CredentialManagementRepositoryImpl
    @Inject
    constructor() : CredentialManagementRepository {
        override suspend fun launchCreateCredential(
            context: Context,
            emailId: String,
            password: String,
            onCreateCredentialReceived: (AppResult<Boolean, NetworkError>) -> Unit,
        ) {
            val credentialManager by lazy { CredentialManager.create(context) }
            try {
                credentialManager.createCredential(
                    context = context,
                    request =
                        CreatePasswordRequest(
                            id = emailId,
                            password = password,
                        ),
                )
                onCreateCredentialReceived(AppResult.Success(true))
            } catch (e: Exception) {
                onCreateCredentialReceived(AppResult.Error(NetworkError.SERVER_ERROR(e.message)))
            }
        }

        override suspend fun launchGetCredential(
            context: Context,
            onGetCredentialReceived: (AppResult<PasswordCredential, NetworkError>) -> Unit,
        ) {
            val credentialManager = CredentialManager.create(context)

            try {
                val result =
                    credentialManager.getCredential(
                        context = context,
                        request =
                            GetCredentialRequest(
                                credentialOptions = listOf(GetPasswordOption()),
                            ),
                    )

                val credential = result.credential as PasswordCredential
                onGetCredentialReceived(AppResult.Success(credential))
            } catch (e: Exception) {
                onGetCredentialReceived(AppResult.Error(NetworkError.SERVER_ERROR(e.message)))
            }
        }
    }
