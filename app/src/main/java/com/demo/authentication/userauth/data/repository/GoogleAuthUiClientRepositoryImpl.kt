package com.demo.authentication.userauth.data.repository

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.demo.authentication.R
import com.demo.authentication.core.domain.utils.AppResult
import com.demo.authentication.core.domain.utils.NetworkError
import com.demo.authentication.core.domain.utils.onError
import com.demo.authentication.core.domain.utils.onSuccess
import com.demo.authentication.userauth.domain.repository.GoogleAuthUiClientRepository
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GoogleAuthUiClientRepositoryImpl
    @Inject
    constructor() : GoogleAuthUiClientRepository {
        private val tag = "GoogleAuthUiClient: "

        private val firebaseAuth = FirebaseAuth.getInstance()

        private fun isGoogleSignedIn(): Boolean {
            if (firebaseAuth.currentUser != null) {
                println("$tag already Signed In")
                return true
            } else {
                return false
            }
        }

        override suspend fun launchGoogleSignIn(context: Context): AppResult<FirebaseUser?, NetworkError> {
            if (isGoogleSignedIn()) {
                return AppResult.Success(firebaseAuth.currentUser)
            }
            return try {
                val result = buildCredentialRequest(context)
                handleGoogleSignIn(result)
                    .onSuccess { user ->
                        AppResult.Success(user)
                    }.onError {
                        AppResult.Error(NetworkError.SERVER_ERROR(it.message))
                    }
            } catch (e: Exception) {
                AppResult.Error(NetworkError.SERVER_ERROR(e.message))
            }
        }

        override suspend fun googleSignOut(context: Context): Boolean {
            val credentialManager by lazy { CredentialManager.create(context) }
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
            firebaseAuth.signOut()
            return true
        }

        private suspend fun buildCredentialRequest(context: Context): GetCredentialResponse {
            val credentialManager by lazy { CredentialManager.create(context) }

            val request =
                GetCredentialRequest
                    .Builder()
                    .addCredentialOption(
                        GetGoogleIdOption
                            .Builder()
                            .setFilterByAuthorizedAccounts(false)
                            .setServerClientId(context.getString(R.string.default_web_client_id))
                            .setAutoSelectEnabled(false)
                            .build(),
                    ).build()
            return credentialManager.getCredential(context = context, request = request)
        }

        private suspend fun handleGoogleSignIn(result: GetCredentialResponse): AppResult<FirebaseUser?, NetworkError> {
            val credential = result.credential
            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                try {
                    val tokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    println(tag + "name ${tokenCredential.displayName}")
                    println(tag + "email ${tokenCredential.id}")
                    println(tag + "image ${tokenCredential.profilePictureUri}")

                    val authCredential = GoogleAuthProvider.getCredential(tokenCredential.idToken, null)
                    val authResult = firebaseAuth.signInWithCredential(authCredential).await()

                    return if (authResult.user != null) {
                        AppResult.Success(authResult.user)
                    } else {
                        AppResult.Error(NetworkError.USER_NOT_FOUND)
                    }
                } catch (e: GoogleIdTokenParsingException) {
                    return AppResult.Error(NetworkError.SERVER_ERROR(e.message))
                }
            } else {
                return AppResult.Error(NetworkError.NOT_GOOGLE_ID_TOKEN_CREDENTIAL)
            }
        }
    }
