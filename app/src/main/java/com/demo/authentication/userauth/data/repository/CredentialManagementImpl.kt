package com.demo.authentication.userauth.data.repository

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.CreatePasswordRequest
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetPasswordOption
import androidx.credentials.PasswordCredential
import androidx.credentials.exceptions.CreateCredentialCancellationException
import androidx.credentials.exceptions.CreateCredentialException
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.demo.authentication.MyApplication
import com.demo.authentication.core.domain.utils.AppResult
import com.demo.authentication.core.domain.utils.NetworkError
import com.demo.authentication.core.presentation.utils.toUserFriendlyMessage
import com.demo.authentication.userauth.data.networking.safeFirebaseCall
import com.demo.authentication.userauth.domain.repository.CredentialManagement
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CredentialManagementImpl @Inject constructor() : CredentialManagement {

    override suspend fun launchCreateCredential(
        context: Context,
        emailId: String,
        password: String,
        onCreateCredentialReceived: (AppResult<Boolean, NetworkError>) -> Unit
    ) {
        val credentialManager by lazy { CredentialManager.create(context) }
        try {
            credentialManager.createCredential(
                context = context,
                request = CreatePasswordRequest(
                    id = emailId,
                    password = password
                )
            )
            onCreateCredentialReceived(AppResult.Success(true))
        } catch (e: Exception) {
            when (e) {
                is CreateCredentialCancellationException -> onCreateCredentialReceived(
                    AppResult.Error(
                        NetworkError.CREDENTIAL_CANCELLATION
                    )
                )

                is CreateCredentialException -> onCreateCredentialReceived(
                    AppResult.Error(
                        NetworkError.CREATE_CREDENTIAL_EXCEPTION
                    )
                )
            }
        }
    }

    override suspend fun launchGetCredential(
        context: Context,
        onGetCredentialReceived: (AppResult<PasswordCredential, NetworkError>) -> Unit
    ) {
        val credentialManager = CredentialManager.create(context)

        try {
            val result = credentialManager.getCredential(
                context = context,
                request = GetCredentialRequest(
                    credentialOptions = listOf(GetPasswordOption())
                )
            )

            val credential = result.credential as PasswordCredential
            onGetCredentialReceived(AppResult.Success(credential))

        } catch (e: Exception) {
            when (e) {
                is GetCredentialCancellationException -> onGetCredentialReceived(AppResult.Error(NetworkError.CREDENTIAL_CANCELLATION))
                is NoCredentialException -> onGetCredentialReceived(AppResult.Error(NetworkError.CREDENTIAL_CANCELLATION))
                is GetCredentialException -> onGetCredentialReceived(AppResult.Error(NetworkError.GET_CREDENTIAL_EXCEPTION))
            }
        }
    }
}

