package com.demo.userauth.repository

import android.app.Activity
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CancellationException
import com.demo.userauth.R
import com.demo.userauth.utils.Resource
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class GoogleAuthUiClient @Inject constructor(private val activity: Activity) {
    private val tag = "GoogleAuthUiClient: "

    private val credentialManager = CredentialManager.create(activity)
    private val firebaseAuth = FirebaseAuth.getInstance()

    fun isSignedIn(): Boolean {
        if (firebaseAuth.currentUser != null) {
            println("$tag already Signed In")
            return true
        } else {
            return false
        }
    }

    suspend fun signIn(): Resource<String> {
        if (isSignedIn()) {
            return Resource.Success("Already Signed In")
        }

        return try {
            val result = buildCredentialRequest()
            handleSignIn(result)
        } catch (e: Exception) {
            e.printStackTrace()

            if (e is CancellationException) throw e
            Resource.Error("Sign-in failed: ${e.localizedMessage ?: "Unknown error"}")
        }
    }

    private suspend fun buildCredentialRequest(): GetCredentialResponse {
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(
                GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(activity.getString(R.string.default_web_client_id))
                    .setAutoSelectEnabled(false)
                    .build()
            )
            .build()
        return credentialManager.getCredential(context = activity, request = request)
    }

    private suspend fun handleSignIn(result: GetCredentialResponse): Resource<String> {
        val credential = result.credential
        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            try {
                val tokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                println(tag + "name ${tokenCredential.displayName}")
                println(tag + "email ${tokenCredential.id}")
                println(tag + "image ${tokenCredential.profilePictureUri}")

                val authCredential = GoogleAuthProvider.getCredential(tokenCredential.idToken, null)
                val authResult = firebaseAuth.signInWithCredential(authCredential).await()

                if (authResult.user != null) {
                   return Resource.Success("Sign-in successful")
                } else {
                    return Resource.Error("Authentication failed: No user found")
                }
            } catch (e: GoogleIdTokenParsingException) {
                println(tag + "GoogleIdTokenParsingException: ${e.message} ")

                return Resource.Error("Invalid Google token: ${e.message}")

            }
        } else {
            println(tag + "credential is not GoogleIdTokenCredential")

            return Resource.Error("Credential is not GoogleIdTokenCredential")
        }
    }

    suspend fun signOut() {
        credentialManager.clearCredentialState(
            ClearCredentialStateRequest()
        )
        firebaseAuth.signOut()
    }
}
