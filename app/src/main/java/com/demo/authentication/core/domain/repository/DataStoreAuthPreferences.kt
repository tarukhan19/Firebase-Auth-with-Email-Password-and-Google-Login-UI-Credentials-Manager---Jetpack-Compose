package com.demo.authentication.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface DataStoreAuthPreferences {

    suspend fun saveLoginStatus(isLoggedIn: Boolean)
    val getLoginState: Flow<Boolean>

}