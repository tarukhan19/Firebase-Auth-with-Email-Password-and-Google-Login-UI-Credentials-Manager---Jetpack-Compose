package com.demo.authentication.features.data.repository

import android.util.Log
import com.demo.authentication.core.domain.utils.AppResult
import com.demo.authentication.core.domain.utils.NetworkError
import com.demo.authentication.core.presentation.utils.toNetworkError
import com.demo.authentication.features.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AuthRepositoryImpl @Inject constructor() : AuthRepository {

    val mAuth = FirebaseAuth.getInstance()

    override suspend fun signUp(
        email: String,
        password: String,
        name: String,
        mobileNo: String
    ): AppResult<FirebaseUser, NetworkError> =
        suspendCoroutine { cont ->
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { response ->
                    val user = response.user
                    if (user != null) {
                        cont.resume(AppResult.Success(user))
                    } else {
                        cont.resume(AppResult.Error(NetworkError.UNKNOWN))
                    }
                }
                .addOnFailureListener { exception ->
                    cont.resume(AppResult.Error(exception.toNetworkError()))
                }
        }

    override suspend fun signIn(
        email: String,
        password: String
    ): AppResult<FirebaseUser, NetworkError> =

        suspendCoroutine { cont ->
            mAuth.signInWithEmailAndPassword(email.trim(), password.trim())
                .addOnSuccessListener { result ->
                    val user = result.user
                    if (user != null) {
                        cont.resume(AppResult.Success(user))
                    } else {
                        cont.resume(
                            AppResult.Error(NetworkError.UNKNOWN),
                        )
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("signIn >>>>>>>>>", exception.toString())
                    cont.resume(AppResult.Error(exception.toNetworkError()))
                }
        }

}