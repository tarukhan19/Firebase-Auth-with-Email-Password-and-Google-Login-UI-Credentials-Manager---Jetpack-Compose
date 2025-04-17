package com.demo.authentication.core.domain.utils

sealed class NetworkError(
    val message: String? = null,
) : Error {
    object USER_NOT_FOUND : NetworkError()

    object NO_INTERNET : NetworkError()

    object NOT_GOOGLE_ID_TOKEN_CREDENTIAL : NetworkError()

    data class SERVER_ERROR(
        val errorMessage: String?,
    ) : NetworkError(errorMessage)
}
