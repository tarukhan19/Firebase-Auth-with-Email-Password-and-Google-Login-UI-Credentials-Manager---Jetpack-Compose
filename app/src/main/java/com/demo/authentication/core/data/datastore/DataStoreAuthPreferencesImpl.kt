package com.demo.authentication.core.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.demo.authentication.core.domain.repository.DataStoreAuthPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreAuthPreferencesImpl
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
    ) : DataStoreAuthPreferences {
        companion object {
            private const val IS_LOGGED_IN = "is_logged_in"
            private val KEY_IS_LOGGED_IN = booleanPreferencesKey(IS_LOGGED_IN)
        }

        // save login status
        override suspend fun saveLoginStatus(isLoggedIn: Boolean) {
            dataStore.edit { preference ->
                preference[KEY_IS_LOGGED_IN] = isLoggedIn
            }
        }

        // read login status

        override val getLoginState: Flow<Boolean> =
            dataStore.data
                .map { preferences ->
                    val loggedIn = preferences[KEY_IS_LOGGED_IN] ?: false
                    loggedIn
                }
    }
