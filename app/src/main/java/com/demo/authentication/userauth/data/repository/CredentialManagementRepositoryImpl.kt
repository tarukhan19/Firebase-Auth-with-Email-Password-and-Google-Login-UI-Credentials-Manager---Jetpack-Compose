package com.demo.authentication.userauth.data.repository

import android.content.Context
import androidx.credentials.CreatePasswordRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetPasswordOption
import androidx.credentials.PasswordCredential
import com.demo.authentication.core.domain.utils.AppResult
import com.demo.authentication.core.domain.utils.NetworkError
import com.demo.authentication.userauth.data.networking.safeCall
import com.demo.authentication.userauth.domain.model.User
import com.demo.authentication.userauth.domain.repository.CredentialManagementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CredentialManagementRepositoryImpl @Inject constructor() : CredentialManagementRepository {
    override suspend fun launchCreateCredential(
        context: Context,
        emailId: String,
        password: String
    ): Flow<AppResult<Unit, NetworkError>> = flow {
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

            emit(AppResult.Success(Unit))
        } catch (e: Exception) {
            emit(AppResult.Error(NetworkError.SERVER_ERROR(e.message)))
        }
    }

    override suspend fun launchGetCredential(context: Context): Flow<AppResult<User, NetworkError>> = flow{
        val credentialManager by lazy { CredentialManager.create(context) }
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
            val user = User(
                email = credential.id,
                password = credential.password
            )
            emit(AppResult.Success(user))
        } catch (e: Exception) {
            emit(AppResult.Error(NetworkError.SERVER_ERROR(e.message)))
        }
    }
}
