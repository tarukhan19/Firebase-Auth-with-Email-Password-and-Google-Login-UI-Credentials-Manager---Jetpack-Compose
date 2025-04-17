package com.demo.authentication.core.presentation.utils

import com.demo.authentication.core.domain.utils.NetworkError

fun NetworkError.toUserFriendlyMessage(): String =
    when (this) {
        NetworkError.USER_NOT_FOUND -> "User not found."
        NetworkError.NO_INTERNET -> "Check your internet connection."
        NetworkError.NOT_GOOGLE_ID_TOKEN_CREDENTIAL -> "credential is not GoogleIdTokenCredential"
        is NetworkError.SERVER_ERROR -> this.errorMessage ?: "A server error occurred."
        else -> "An unknown error occurred."
    }
