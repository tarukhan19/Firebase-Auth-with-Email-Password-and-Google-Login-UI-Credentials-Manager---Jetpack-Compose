package com.demo.authentication.core.domain.di

import com.demo.authentication.userauth.data.repository.AuthRepositoryImpl
import com.demo.authentication.userauth.data.repository.CredentialManagementRepositoryImpl
import com.demo.authentication.userauth.data.repository.GoogleAuthUiClientRepositoryImpl
import com.demo.authentication.userauth.domain.repository.AuthRepository
import com.demo.authentication.userauth.domain.repository.CredentialManagementRepository
import com.demo.authentication.userauth.domain.repository.GoogleAuthUiClientRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun provideCredentialManagerHelper(): CredentialManagementRepository = CredentialManagementRepositoryImpl()

    @Provides
    fun provideAuthRepository(): AuthRepository = AuthRepositoryImpl()

    @Provides
    fun provideGoogleAuthUiClient(): GoogleAuthUiClientRepository = GoogleAuthUiClientRepositoryImpl()
}
