package com.demo.authentication.features.domain.repository

import com.demo.authentication.core.domain.utils.Resource

interface GoogleAuthUiClient {
    suspend fun googleSignIn() : Resource<String>
    suspend fun googleSignOut()
    suspend fun userCredentialManagerRegister(email : String, password : String): Resource<String>
    suspend fun userCredentialManagerLogin() : Resource<String>
}