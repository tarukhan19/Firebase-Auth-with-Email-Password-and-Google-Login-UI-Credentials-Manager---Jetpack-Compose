package com.demo.authentication.userauth.data.networking

import com.demo.authentication.core.domain.utils.AppResult
import com.demo.authentication.core.domain.utils.NetworkError
import kotlinx.coroutines.ensureActive
import kotlin.coroutines.coroutineContext

suspend inline fun <T> safeFirebaseCall(crossinline call: suspend () -> T): AppResult<T, NetworkError> =
    try {
        val result = call()
        AppResult.Success(result)
    } catch (e: Exception) {
        coroutineContext.ensureActive()
        AppResult.Error(NetworkError.SERVER_ERROR(e.message))
    }
