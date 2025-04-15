package com.demo.authentication.features.data.repository

import android.app.Activity
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CreatePasswordRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.GetPasswordOption
import androidx.credentials.PasswordCredential
import androidx.credentials.exceptions.CreateCredentialCancellationException
import androidx.credentials.exceptions.CreateCredentialException
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CancellationException
import com.demo.authentication.R
import com.demo.authentication.core.domain.utils.Resource
import com.demo.authentication.features.domain.repository.GoogleAuthUiClient
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class GoogleAuthUiClientImpl @Inject constructor(
    private val activity: Activity,
): GoogleAuthUiClient {
    private val tag = "GoogleAuthUiClient: "

    private val credentialManager = CredentialManager.create(activity)
    private val firebaseAuth = FirebaseAuth.getInstance()

    private fun isGoogleSignedIn(): Boolean {
        if (firebaseAuth.currentUser != null) {
            println("$tag already Signed In")
            return true
        } else {
            return false
        }
    }

    override suspend fun googleSignIn(): Resource<String> {
        if (isGoogleSignedIn()) {
            return Resource.Success("Already Signed In")
        }

        return try {
            val result = buildCredentialRequest()
            handleGoogleSignIn(result)
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

    private suspend fun handleGoogleSignIn(result: GetCredentialResponse): Resource<String> {
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
                    Resource.Success("Sign-in successful")
                } else {
                    Resource.Error("Authentication failed: No user found")
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

    override suspend fun googleSignOut() {
        credentialManager.clearCredentialState(
            ClearCredentialStateRequest()
        )
        firebaseAuth.signOut()
    }

    //// credential manager for normal login signup ////////

    override suspend fun userCredentialManagerRegister(emailId: String, password : String): Resource<String> {
        return try {
            credentialManager.createCredential(
                context = activity,
                request = CreatePasswordRequest(
                    id = emailId,
                    password = password
                )
            )
            Resource.Success("Registration successful") // Return the collected result
        } catch (e: CreateCredentialCancellationException) {
            e.printStackTrace()
            Resource.Error("CreateCredentialCancellationException: ${e.message}")
        } catch (e: CreateCredentialException) {
            e.printStackTrace()
            Resource.Error("CreateCredentialException: ${e.message}")
        }
    }

    override suspend fun userCredentialManagerLogin(): Resource<String> {
        try {
            val credentialResponse = credentialManager.getCredential(
                context = activity,
                request = GetCredentialRequest(
                    credentialOptions = listOf(GetPasswordOption())
                )
            )

            val credential = credentialResponse.credential as? PasswordCredential
                ?: return Resource.Error("Something went wrong")

            var loginResult: Resource<String> = Resource.Error("Unexpected error") // Default error

//            userAuthRepo.userDatabaseLogin(credential.id, credential.password)
//                .collect { result ->
//                    loginResult = result  // Store the result inside the variable
//                }
           return loginResult // Return the collected result

        } catch (e: GetCredentialCancellationException) {
            e.printStackTrace()
            return Resource.Error("GetCredentialCancellationException: ${e.message}")
        } catch (e: NoCredentialException) {
            e.printStackTrace()
            return Resource.Error("NoCredentialException: ${e.message}")
        } catch (e: GetCredentialException) {
            e.printStackTrace()
            return Resource.Error("GetCredentialException: ${e.message}")
        }
    }
}
