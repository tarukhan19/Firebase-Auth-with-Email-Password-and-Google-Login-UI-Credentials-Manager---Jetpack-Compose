package com.demo.authentication.features.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.demo.authentication.core.presentation.utils.Constant.IS_LOGGED_IN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(private val dataStore: DataStore<Preferences>)  {
    companion object {
        private val KEY_IS_LOGGED_IN = booleanPreferencesKey(IS_LOGGED_IN)
    }

    // save login status
    suspend fun saveLoginStatus(isLoggedIn :  Boolean) {
        dataStore.edit { preference ->
            preference[KEY_IS_LOGGED_IN] = isLoggedIn
        }
    }

    // read login status
    val getLoginState: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[KEY_IS_LOGGED_IN] ?: false
        }
}