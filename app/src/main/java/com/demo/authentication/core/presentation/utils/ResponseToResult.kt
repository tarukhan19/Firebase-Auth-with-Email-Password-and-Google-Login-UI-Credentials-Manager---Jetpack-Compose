package com.demo.authentication.core.presentation.utils

import com.demo.authentication.core.domain.utils.NetworkError
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException


fun NetworkError.toUserFriendlyMessage(): String = when (this) {
    NetworkError.INVALID_EMAIL_PASSWORD -> "Invalid email address/password."
    NetworkError.USER_NOT_FOUND -> "User not found."
    NetworkError.EMAIL_ALREADY_IN_USE -> "Email already in use."
    NetworkError.NO_INTERNET -> "Check your internet connection."
    else -> "An unknown error occurred."
}
