package com.demo.authentication.userauth.domain.repository

import android.content.Context
import com.demo.authentication.core.domain.utils.AppResult
import com.demo.authentication.core.domain.utils.NetworkError
import com.demo.authentication.userauth.domain.model.User
import kotlinx.coroutines.flow.Flow

interface CredentialManagementRepository {
    suspend fun launchCreateCredential(
        context: Context,
        email: String,
        password: String,
    ): Flow<AppResult<Unit, NetworkError>>

    suspend fun launchGetCredential(
        context: Context,
    ): Flow<AppResult<User, NetworkError>>
}
