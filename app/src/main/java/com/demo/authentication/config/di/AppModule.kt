package com.demo.authentication.config.di

import android.content.Context
import com.demo.authentication.core.data.util.ResourceProviderImpl
import com.demo.authentication.core.domain.utils.ResourceProvider
import com.demo.authentication.userauth.data.repository.AuthRepositoryImpl
import com.demo.authentication.userauth.data.repository.CredentialManagementRepositoryImpl
import com.demo.authentication.userauth.data.repository.GoogleAuthUiClientRepositoryImpl
import com.demo.authentication.userauth.domain.repository.AuthRepository
import com.demo.authentication.userauth.domain.repository.CredentialManagementRepository
import com.demo.authentication.userauth.domain.repository.GoogleAuthUiClientRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun provideCredentialManagerHelper(): CredentialManagementRepository = CredentialManagementRepositoryImpl()

    @Provides
    @Singleton
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideAuthRepository(firebaseAuth: FirebaseAuth): AuthRepository {
        return AuthRepositoryImpl(firebaseAuth)
    }

    @Provides
    @Singleton
    fun provideResourceProvider(@ApplicationContext context: Context): ResourceProvider {
        return ResourceProviderImpl(context)
    }

    @Provides
    fun provideGoogleAuthUiClient(): GoogleAuthUiClientRepository = GoogleAuthUiClientRepositoryImpl()
}
