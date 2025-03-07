package com.demo.userauth.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(private val dataStore: DataStore<Preferences>)  {
    companion object {
        private val KEY_IS_LOGGED_IN = booleanPreferencesKey("isLoggedIn")
    }

    // save login status
    suspend fun saveLoginStatus(isLoggedIn :  Boolean) {
        dataStore.edit { preference ->
            preference[KEY_IS_LOGGED_IN] = isLoggedIn
        }
    }

    // read login status
    val isLoggedInFlow : Flow<Boolean?> = dataStore.data.map { preference ->
        preference[KEY_IS_LOGGED_IN] // Returns Boolean? (true, false, or null)
    }
}