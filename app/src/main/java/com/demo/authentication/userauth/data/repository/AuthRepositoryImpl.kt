package com.demo.authentication.userauth.data.repository

import com.demo.authentication.core.domain.utils.AppResult
import com.demo.authentication.core.domain.utils.NetworkError
import com.demo.authentication.core.presentation.utils.toUserFriendlyMessage
import com.demo.authentication.userauth.data.networking.safeCall
import com.demo.authentication.userauth.domain.model.User
import com.demo.authentication.userauth.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(private val firebaseAuth: FirebaseAuth) :
    AuthRepository {

    override suspend fun signUp(
        email: String,
        password: String,
        name: String,
        mobileNo: String
    ): Flow<AppResult<User, NetworkError>> = safeCall {
        val authResult =
            firebaseAuth.createUserWithEmailAndPassword(email.trim(), password.trim()).await()
        val firebaseUser = authResult.user
            ?: throw Exception(NetworkError.USER_NOT_FOUND.toUserFriendlyMessage())

        User(
            id = firebaseUser.uid,
            name = firebaseUser.displayName ?: "",
            email = firebaseUser.email ?: ""
        )
    }

    override suspend fun signIn(
        email: String,
        password: String
    ): Flow<AppResult<User, NetworkError>> =
        safeCall {
            val authResult =
                firebaseAuth.signInWithEmailAndPassword(email.trim(), password.trim()).await()
            val firebaseUser = authResult.user
                ?: throw Exception(NetworkError.USER_NOT_FOUND.toUserFriendlyMessage())

            User(
                id = firebaseUser.uid,
                name = firebaseUser.displayName ?: "",
                email = firebaseUser.email ?: ""
            )
        }
}






