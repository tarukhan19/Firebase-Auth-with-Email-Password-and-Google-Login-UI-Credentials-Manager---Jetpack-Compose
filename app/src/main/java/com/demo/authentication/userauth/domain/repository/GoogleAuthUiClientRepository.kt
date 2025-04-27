package com.demo.authentication.userauth.domain.repository

import android.content.Context
import com.demo.authentication.core.domain.utils.AppResult
import com.demo.authentication.core.domain.utils.NetworkError
import com.demo.authentication.userauth.domain.model.User
import kotlinx.coroutines.flow.Flow

interface GoogleAuthUiClientRepository {

    suspend fun googleSignOut(context: Context): Boolean

    suspend fun googleSignIn(
        context : Context
    ): Flow<AppResult<User, NetworkError>>
}
