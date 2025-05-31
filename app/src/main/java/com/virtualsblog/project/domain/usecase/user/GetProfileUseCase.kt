package com.virtualsblog.project.domain.usecase.user

import com.virtualsblog.project.domain.model.User
import com.virtualsblog.project.domain.repository.UserRepository
import com.virtualsblog.project.util.Resource
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(): Resource<User> {
        return repository.getProfile()
    }
}