package com.demo.authentication.config.di

import com.demo.authentication.core.domain.utils.ResourceProvider
import com.demo.authentication.userauth.domain.repository.AuthRepository
import com.demo.authentication.userauth.domain.repository.CredentialManagementRepository
import com.demo.authentication.userauth.domain.repository.GoogleAuthUiClientRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import io.mockk.mockk
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppModule::class]
)
object AppModuleTest {

    @Provides
    @Singleton
    fun provideAuthRepository(): AuthRepository = mockk(relaxed = true)

    @Provides
    @Singleton
    fun provideCredentialManagementRepository(): CredentialManagementRepository = mockk(relaxed = true)

    @Provides
    @Singleton
    fun provideGoogleAuthUiClientRepository(): GoogleAuthUiClientRepository = mockk(relaxed = true)

    @Provides
    @Singleton
    fun provideResourceProvider(): ResourceProvider = mockk(relaxed = true)
}