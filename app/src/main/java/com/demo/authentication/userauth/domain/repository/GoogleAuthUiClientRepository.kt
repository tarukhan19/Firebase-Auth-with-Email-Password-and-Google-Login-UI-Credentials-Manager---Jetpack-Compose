package com.demo.authentication.userauth.domain.repository

import android.content.Context
import com.demo.authentication.core.domain.utils.AppResult
import com.demo.authentication.core.domain.utils.NetworkError
import com.google.firebase.auth.FirebaseUser

interface GoogleAuthUiClientRepository {
    suspend fun launchGoogleSignIn(context: Context): AppResult<FirebaseUser?, NetworkError>
    suspend fun googleSignOut(context: Context) : Boolean
}
