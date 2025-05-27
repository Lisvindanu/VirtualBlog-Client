package com.virtualsblog.project.domain.usecase.auth

import com.virtualsblog.project.domain.model.User
import com.virtualsblog.project.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(): Flow<User?> {
        return repository.getCurrentUser()
    }
}