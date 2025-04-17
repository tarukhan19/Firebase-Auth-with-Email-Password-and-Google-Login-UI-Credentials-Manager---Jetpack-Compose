package com.demo.authentication.core.domain.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.demo.authentication.core.data.datastore.DataStoreAuthPreferencesImpl
import com.demo.authentication.core.domain.repository.DataStoreAuthPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/*
 * This extension property (Context.dataStore) creates a singleton instance of DataStore<Preferences>. It uses Jetpack's
   Preferences DataStore to store key-value pairs in a file named "user_prefs".
   It Prevents multiple instances of DataStore from being created.

 * Declares a Hilt module using @Module annotation.
   @InstallIn(SingletonComponent::class) ensures that this module provides dependencies at the application level (singleton)

 * @Provides: Marks this function as a dependency provider in Hilt.

 * @Singleton: Ensures only one instance of DataStore is created and shared across the app.

 * @ApplicationContext context: Context: Injects the application context (required for DataStore).

 * return context.dataStore: Returns the previously defined singleton DataStore instance.
*/
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

@Module
@InstallIn(SingletonComponent::class)
object DataStoreAuthModule {
    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = context.dataStore

    @Provides
    @Singleton
    fun provideDataStoreRepository(dataStore: DataStore<Preferences>): DataStoreAuthPreferences = DataStoreAuthPreferencesImpl(dataStore)
}
