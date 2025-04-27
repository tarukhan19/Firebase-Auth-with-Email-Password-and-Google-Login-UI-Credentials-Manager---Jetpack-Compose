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
import com.demo.authentication.userauth.domain.model.User
import com.demo.authentication.userauth.domain.repository.GoogleAuthUiClientRepository
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GoogleAuthUiClientRepositoryImpl
@Inject
constructor() : GoogleAuthUiClientRepository {
    private val tag = "GoogleAuthUiClient: "

    private val firebaseAuth = FirebaseAuth.getInstance()

    override suspend fun googleSignIn(context: Context): Flow<AppResult<User, NetworkError>> = flow {
        try {
            // First get the credential response
            val credentialResponse = buildCredentialRequest(context)

            // Process the credential response
            val credential = credentialResponse.credential
            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                try {
                    val tokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    println(tag + "name ${tokenCredential.displayName}")
                    println(tag + "email ${tokenCredential.id}")
                    println(tag + "image ${tokenCredential.profilePictureUri}")

                    val authCredential = GoogleAuthProvider.getCredential(tokenCredential.idToken, null)
                    val authResult = firebaseAuth.signInWithCredential(authCredential).await()

                    if (authResult.user != null) {
                        val user = User(
                            id = authResult.user?.uid ?: "",
                            email = authResult.user?.email ?: "",
                            name = authResult.user?.displayName ?: ""
                        )
                        emit(AppResult.Success(user))
                    } else {
                        emit(AppResult.Error(NetworkError.USER_NOT_FOUND))
                    }
                } catch (e: GoogleIdTokenParsingException) {
                    emit(AppResult.Error(NetworkError.SERVER_ERROR(e.message)))
                }
            } else {
                emit(AppResult.Error(NetworkError.NOT_GOOGLE_ID_TOKEN_CREDENTIAL))
            }
        } catch (e: Exception) {
            emit(handleException(e))
        }
    }

    private fun handleException(e: Exception): AppResult.Error<NetworkError> {
        return when {
            e.message?.contains("password is invalid") == true ->
                AppResult.Error(NetworkError.INVALID_CREDENTIALS)

            e.message?.contains("no user record") == true ->
                AppResult.Error(NetworkError.USER_NOT_FOUND)

            else -> AppResult.Error(NetworkError.SERVER_ERROR(e.message))
        }
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

    override suspend fun googleSignOut(context: Context): Boolean {
        val credentialManager by lazy { CredentialManager.create(context) }
        credentialManager.clearCredentialState(ClearCredentialStateRequest())
        firebaseAuth.signOut()
        return true
    }
}