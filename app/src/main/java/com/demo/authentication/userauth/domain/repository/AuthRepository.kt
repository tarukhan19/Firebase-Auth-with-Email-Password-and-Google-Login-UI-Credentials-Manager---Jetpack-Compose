package com.demo.authentication.userauth.domain.repository

import com.demo.authentication.core.domain.utils.AppResult
import com.demo.authentication.core.domain.utils.NetworkError
import com.demo.authentication.userauth.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun signUp(
        email: String,
        password: String,
        name: String,
        mobileNo: String,
    ): Flow<AppResult<User, NetworkError>>

    suspend fun signIn(
        email: String,
        password: String,
    ): Flow<AppResult<User, NetworkError>>

}