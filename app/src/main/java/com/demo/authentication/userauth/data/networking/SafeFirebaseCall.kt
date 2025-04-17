package com.demo.authentication.userauth.data.networking

import com.demo.authentication.core.domain.utils.AppResult
import com.demo.authentication.core.domain.utils.NetworkError
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.ensureActive
import java.io.IOException
import kotlin.coroutines.coroutineContext


suspend inline fun <T> safeFirebaseCall(
    crossinline call: suspend () -> T
): AppResult<T, NetworkError> {
    return try {
        val result = call()
        AppResult.Success(result)
    } catch (e: Exception) {
        coroutineContext.ensureActive()
        AppResult.Error(NetworkError.SERVER_ERROR(e.message))
    }
}
