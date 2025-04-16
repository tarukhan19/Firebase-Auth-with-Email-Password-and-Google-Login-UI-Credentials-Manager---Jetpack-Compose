package com.demo.authentication.userauth.domain.repository

import com.demo.authentication.core.domain.utils.AppResult
import com.demo.authentication.core.domain.utils.NetworkError
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    suspend fun signUp(email: String, password: String, name : String, mobileNo : String): AppResult<FirebaseUser, NetworkError>
    suspend fun signIn(email: String, password: String): AppResult<FirebaseUser, NetworkError>
}