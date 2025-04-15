package com.demo.authentication.core.presentation.utils

import android.content.Context
import com.demo.authentication.R
import com.demo.authentication.core.domain.utils.NetworkError


fun NetworkError.toStrings(context: Context) : String {
    val resId = when(this) {
        NetworkError.REQUEST_TIMEOUTS -> R.string.error_request_timeout
        NetworkError.TOO_MANY_REQUESTS -> R.string.error_too_many_requests
        NetworkError.NO_INTERNET -> R.string.error_no_internet
        NetworkError.SERVER_ERROR -> R.string.error_unknown
        NetworkError.INVALID_CREDENTIAL -> R.string.error_serialization
        NetworkError.UNKNOWN -> R.string.error_unknown
    }
    return context.getString(resId)
}