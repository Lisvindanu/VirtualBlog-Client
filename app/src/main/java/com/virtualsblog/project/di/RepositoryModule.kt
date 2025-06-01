package com.virtualsblog.project.di

import com.virtualsblog.project.data.repository.AuthRepositoryImpl
import com.virtualsblog.project.data.repository.BlogRepositoryImpl
import com.virtualsblog.project.data.repository.UserRepositoryImpl
import com.virtualsblog.project.domain.repository.AuthRepository
import com.virtualsblog.project.domain.repository.BlogRepository
import com.virtualsblog.project.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository( // Binding untuk UserRepository
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    abstract fun bindBlogRepository(
        blogRepositoryImpl: BlogRepositoryImpl
    ): BlogRepository
}