package com.demo.authentication.userauth.domain.repository

import com.demo.authentication.core.domain.utils.AppResult
import com.demo.authentication.core.domain.utils.NetworkError
import com.demo.authentication.core.domain.utils.Resource
import com.google.firebase.auth.FirebaseUser

interface GoogleAuthUiClient {
    suspend fun googleSignIn() : Resource<String>
    suspend fun googleSignOut()
    suspend fun userCredentialManagerRegister(email : String, password : String): Resource<String>
    suspend fun userCredentialManagerLogin() : Resource<String>
}