package com.virtualsblog.project.di

import com.virtualsblog.project.domain.repository.AuthRepository
import com.virtualsblog.project.domain.repository.UserRepository // Import UserRepository
import com.virtualsblog.project.domain.usecase.auth.*
// Import User UseCases
import com.virtualsblog.project.domain.usecase.user.GetProfileUseCase
import com.virtualsblog.project.domain.usecase.user.UpdateProfileUseCase // Pastikan path import benar
import com.virtualsblog.project.domain.usecase.user.UploadProfilePictureUseCase
import com.virtualsblog.project.domain.repository.BlogRepository
import com.virtualsblog.project.domain.usecase.blog.GetPostsUseCase

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    // Auth UseCases
    @Provides
    @Singleton
    fun provideLoginUseCase(repository: AuthRepository): LoginUseCase {
        return LoginUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideRegisterUseCase(repository: AuthRepository): RegisterUseCase {
        return RegisterUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetCurrentUserUseCase(repository: AuthRepository): GetCurrentUserUseCase {
        return GetCurrentUserUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideLogoutUseCase(repository: AuthRepository): LogoutUseCase {
        return LogoutUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideForgotPasswordUseCase(repository: AuthRepository): ForgotPasswordUseCase {
        return ForgotPasswordUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideVerifyOtpUseCase(repository: AuthRepository): VerifyOtpUseCase {
        return VerifyOtpUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideResetPasswordUseCase(repository: AuthRepository): ResetPasswordUseCase {
        return ResetPasswordUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideChangePasswordUseCase(repository: AuthRepository): ChangePasswordUseCase {
        return ChangePasswordUseCase(repository)
    }

    // User UseCases
    @Provides
    @Singleton
    fun provideGetProfileUseCase(repository: UserRepository): GetProfileUseCase {
        return GetProfileUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideUpdateProfileUseCase(repository: UserRepository): UpdateProfileUseCase {
        return UpdateProfileUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideUploadProfilePictureUseCase(repository: UserRepository): UploadProfilePictureUseCase {
        return UploadProfilePictureUseCase(repository)
    }

    // Blog UseCases
    @Provides
    @Singleton
    fun provideGetPostsUseCase(repository: BlogRepository): GetPostsUseCase {
        return GetPostsUseCase(repository)
    }
}