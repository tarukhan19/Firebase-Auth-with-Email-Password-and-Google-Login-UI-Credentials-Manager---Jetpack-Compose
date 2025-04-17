package com.demo.authentication.userauth.domain.repository

import android.content.Context
import androidx.credentials.PasswordCredential
import com.demo.authentication.core.domain.utils.AppResult
import com.demo.authentication.core.domain.utils.NetworkError

interface CredentialManagementRepository {
    suspend fun launchCreateCredential(
        context: Context,
        email: String,
        password: String,
        onCreateCredentialReceived: (AppResult<Boolean, NetworkError>) -> Unit
    )

    suspend fun launchGetCredential(
        context: Context,
        onGetCredentialReceived: (AppResult<PasswordCredential, NetworkError>) -> Unit
    )
}