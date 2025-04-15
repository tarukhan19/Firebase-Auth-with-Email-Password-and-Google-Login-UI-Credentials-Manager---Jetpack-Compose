package com.demo.authentication.core.presentation.utils

import com.demo.authentication.core.domain.utils.NetworkError
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

fun Throwable.toNetworkError(): NetworkError = when (this) {
    is FirebaseAuthWeakPasswordException -> NetworkError.SERVER_ERROR
    is FirebaseAuthInvalidCredentialsException -> NetworkError.INVALID_CREDENTIAL
    is FirebaseAuthUserCollisionException -> NetworkError.TOO_MANY_REQUESTS
    else -> NetworkError.UNKNOWN
}
