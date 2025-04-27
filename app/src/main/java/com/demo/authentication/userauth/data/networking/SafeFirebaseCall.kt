package com.demo.authentication.userauth.data.networking

import com.demo.authentication.core.domain.utils.AppResult
import com.demo.authentication.core.domain.utils.NetworkError
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.coroutines.coroutineContext

 inline fun <T> safeCall(crossinline call: suspend () -> T): Flow<AppResult<T, NetworkError>> =
    flow {
        try {
            val result = call()
            emit(AppResult.Success(result))
        } catch (e: Exception) {
            coroutineContext.ensureActive()
           emit(AppResult.Error(NetworkError.SERVER_ERROR(e.message)))

        }
    }

