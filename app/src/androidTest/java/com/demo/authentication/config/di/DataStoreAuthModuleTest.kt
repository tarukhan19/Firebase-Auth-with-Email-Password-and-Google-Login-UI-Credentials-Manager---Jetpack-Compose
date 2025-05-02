package com.demo.authentication.config.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.demo.authentication.core.data.datastore.DataStoreAuthPreferencesImpl
import com.demo.authentication.core.domain.repository.DataStoreAuthPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

/**
 * Test module that overrides the production DataStoreAuthModule for testing.
 *
 * This module provides a real DataStore implementation for testing.
 */
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DataStoreAuthModule::class]
)
object TestDataStoreAuthModule {

    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = context.dataStore

    @Provides
    @Singleton
    fun provideDataStoreRepository(dataStore: DataStore<Preferences>): DataStoreAuthPreferences =
        DataStoreAuthPreferencesImpl(dataStore)
}