package com.demo.authentication.core.domain.utils

interface ResourceProvider {
    fun getString(resId: Int): String
}