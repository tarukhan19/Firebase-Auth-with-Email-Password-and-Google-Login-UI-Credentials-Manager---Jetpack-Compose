package com.demo.authentication.userauth.data.repository

import android.util.Log
import com.demo.authentication.core.domain.utils.AppResult
import com.demo.authentication.core.domain.utils.NetworkError
import com.demo.authentication.core.presentation.utils.toUserFriendlyMessage
import com.demo.authentication.userauth.data.networking.safeFirebaseCall
import com.demo.authentication.userauth.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor() : AuthRepository {

    val mAuth = FirebaseAuth.getInstance()

    override suspend fun signUp(
        email: String,
        password: String,
        name: String,
        mobileNo: String
    ): AppResult<FirebaseUser, NetworkError> {
        return safeFirebaseCall {
            val authResult = mAuth.createUserWithEmailAndPassword(email, password).await()
            authResult.user ?: throw Exception(NetworkError.USER_NOT_FOUND.toUserFriendlyMessage())
        }
    }

    override suspend fun signIn(
        email: String,
        password: String
    ): AppResult<FirebaseUser, NetworkError> {
        return safeFirebaseCall {
            val authResult = mAuth.signInWithEmailAndPassword(email.trim(), password.trim()).await()
            authResult.user ?: throw Exception(NetworkError.USER_NOT_FOUND.toUserFriendlyMessage())
        }
    }
}