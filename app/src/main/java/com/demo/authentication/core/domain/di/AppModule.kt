package com.demo.authentication.core.domain.di

import android.content.Context
import com.demo.authentication.userauth.data.repository.AuthRepositoryImpl
import com.demo.authentication.userauth.data.repository.CredentialManagementImpl
import com.demo.authentication.userauth.domain.repository.AuthRepository
import com.demo.authentication.userauth.domain.repository.CredentialManagement
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
//    @Provides
//    fun provideAuthRepository(): AuthRepository = AuthRepositoryImpl()

    @Provides
    fun provideCredentialManagerHelper(
    ): CredentialManagement {
        return CredentialManagementImpl()
    }

    @Provides
    fun provideAuthRepository(
        credentialManagerHelper: CredentialManagementImpl
    ): AuthRepository {
        return AuthRepositoryImpl(credentialManagerHelper)
    }
}