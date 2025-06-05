// app/src/main/java/com/virtualsblog/project/di/UseCaseModule.kt
package com.virtualsblog.project.di

import com.virtualsblog.project.domain.repository.AuthRepository
import com.virtualsblog.project.domain.repository.UserRepository
import com.virtualsblog.project.domain.usecase.auth.*
import com.virtualsblog.project.domain.usecase.user.GetProfileUseCase
import com.virtualsblog.project.domain.usecase.user.UpdateProfileUseCase
import com.virtualsblog.project.domain.usecase.user.UploadProfilePictureUseCase
import com.virtualsblog.project.domain.repository.BlogRepository
import com.virtualsblog.project.domain.usecase.blog.GetPostsUseCase
import com.virtualsblog.project.domain.usecase.blog.GetPostsForHomeUseCase
import com.virtualsblog.project.domain.usecase.blog.GetTotalPostsCountUseCase
import com.virtualsblog.project.domain.usecase.blog.GetPostByIdUseCase
import com.virtualsblog.project.domain.usecase.blog.ToggleLikeUseCase
import com.virtualsblog.project.domain.usecase.blog.CreatePostUseCase
import com.virtualsblog.project.domain.usecase.blog.UpdatePostUseCase
import com.virtualsblog.project.domain.usecase.blog.DeletePostUseCase
import com.virtualsblog.project.domain.usecase.blog.GetCategoriesUseCase
import com.virtualsblog.project.domain.usecase.blog.GetPostsByCategoryIdUseCase
import com.virtualsblog.project.domain.usecase.blog.SearchUseCase
// *** NEW IMPORT FOR GetPostsByAuthorIdUseCase ***
import com.virtualsblog.project.domain.usecase.blog.GetPostsByAuthorIdUseCase
import com.virtualsblog.project.domain.usecase.comment.CreateCommentUseCase
import com.virtualsblog.project.domain.usecase.comment.DeleteCommentUseCase

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    // Auth UseCases (existing)
    @Provides
    @Singleton
    fun provideLoginUseCase(repository: AuthRepository): LoginUseCase = LoginUseCase(repository)

    @Provides
    @Singleton
    fun provideRegisterUseCase(repository: AuthRepository): RegisterUseCase = RegisterUseCase(repository)

    @Provides
    @Singleton
    fun provideGetCurrentUserUseCase(repository: AuthRepository): GetCurrentUserUseCase = GetCurrentUserUseCase(repository)

    @Provides
    @Singleton
    fun provideLogoutUseCase(repository: AuthRepository): LogoutUseCase = LogoutUseCase(repository)

    @Provides
    @Singleton
    fun provideForgotPasswordUseCase(repository: AuthRepository): ForgotPasswordUseCase = ForgotPasswordUseCase(repository)

    @Provides
    @Singleton
    fun provideVerifyOtpUseCase(repository: AuthRepository): VerifyOtpUseCase = VerifyOtpUseCase(repository)

    @Provides
    @Singleton
    fun provideResetPasswordUseCase(repository: AuthRepository): ResetPasswordUseCase = ResetPasswordUseCase(repository)

    @Provides
    @Singleton
    fun provideChangePasswordUseCase(repository: AuthRepository): ChangePasswordUseCase = ChangePasswordUseCase(repository)


    // User UseCases (existing)
    @Provides
    @Singleton
    fun provideGetProfileUseCase(repository: UserRepository): GetProfileUseCase = GetProfileUseCase(repository)

    @Provides
    @Singleton
    fun provideUpdateProfileUseCase(repository: UserRepository): UpdateProfileUseCase = UpdateProfileUseCase(repository)

    @Provides
    @Singleton
    fun provideUploadProfilePictureUseCase(repository: UserRepository): UploadProfilePictureUseCase = UploadProfilePictureUseCase(repository)


    // Blog UseCases
    @Provides
    @Singleton
    fun provideGetPostsUseCase(repository: BlogRepository): GetPostsUseCase = GetPostsUseCase(repository)

    @Provides
    @Singleton
    fun provideGetPostsForHomeUseCase(repository: BlogRepository): GetPostsForHomeUseCase = GetPostsForHomeUseCase(repository)

    @Provides
    @Singleton
    fun provideGetTotalPostsCountUseCase(repository: BlogRepository): GetTotalPostsCountUseCase = GetTotalPostsCountUseCase(repository)

    @Provides
    @Singleton
    fun provideGetPostByIdUseCase(repository: BlogRepository): GetPostByIdUseCase = GetPostByIdUseCase(repository)

    @Provides
    @Singleton
    fun provideCreatePostUseCase(repository: BlogRepository): CreatePostUseCase = CreatePostUseCase(repository)

    @Provides
    @Singleton
    fun provideGetCategoriesUseCase(repository: BlogRepository): GetCategoriesUseCase = GetCategoriesUseCase(repository)

    @Provides
    @Singleton
    fun provideGetPostsByCategoryIdUseCase(repository: BlogRepository): GetPostsByCategoryIdUseCase = GetPostsByCategoryIdUseCase(repository)

    // *** NEW PROVIDER FOR GetPostsByAuthorIdUseCase ***
    @Provides
    @Singleton
    fun provideGetPostsByAuthorIdUseCase(repository: BlogRepository): GetPostsByAuthorIdUseCase = GetPostsByAuthorIdUseCase(repository)

    @Provides
    @Singleton
    fun provideSearchUseCase(repository: BlogRepository): SearchUseCase = SearchUseCase(repository)

    @Provides
    @Singleton
    fun provideUpdatePostUseCase(repository: BlogRepository): UpdatePostUseCase = UpdatePostUseCase(repository)

    @Provides
    @Singleton
    fun provideDeletePostUseCase(repository: BlogRepository): DeletePostUseCase = DeletePostUseCase(repository)


    // Comment UseCases (existing)
    @Provides
    @Singleton
    fun provideCreateCommentUseCase(repository: BlogRepository): CreateCommentUseCase = CreateCommentUseCase(repository)

    @Provides
    @Singleton
    fun provideDeleteCommentUseCase(repository: BlogRepository): DeleteCommentUseCase = DeleteCommentUseCase(repository)

    // Like UseCases (existing)
    @Provides
    @Singleton
    fun provideToggleLikeUseCase(repository: BlogRepository): ToggleLikeUseCase = ToggleLikeUseCase(repository)
}