package com.demo.authentication.core.domain.utils

enum class NetworkError : Error {
    REQUEST_TIMEOUTS,
    TOO_MANY_REQUESTS,
    NO_INTERNET,
    SERVER_ERROR,
    INVALID_CREDENTIAL,
    UNKNOWN
}