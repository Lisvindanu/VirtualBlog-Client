package com.virtualsblog.project.di

import com.virtualsblog.project.domain.repository.AuthRepository
import com.virtualsblog.project.domain.usecase.auth.GetCurrentUserUseCase
import com.virtualsblog.project.domain.usecase.auth.LoginUseCase
import com.virtualsblog.project.domain.usecase.auth.LogoutUseCase
import com.virtualsblog.project.domain.usecase.auth.RegisterUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideLoginUseCase(
        repository: AuthRepository
    ): LoginUseCase {
        return LoginUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideRegisterUseCase(
        repository: AuthRepository
    ): RegisterUseCase {
        return RegisterUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetCurrentUserUseCase(
        repository: AuthRepository
    ): GetCurrentUserUseCase {
        return GetCurrentUserUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideLogoutUseCase(
        repository: AuthRepository
    ): LogoutUseCase {
        return LogoutUseCase(repository)
    }
}