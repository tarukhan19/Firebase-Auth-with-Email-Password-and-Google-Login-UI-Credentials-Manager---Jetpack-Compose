package com.demo.authentication.core.domain.di

import com.demo.authentication.userauth.data.repository.AuthRepositoryImpl
import com.demo.authentication.userauth.domain.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun provideAuthRepository(): AuthRepository = AuthRepositoryImpl()
}