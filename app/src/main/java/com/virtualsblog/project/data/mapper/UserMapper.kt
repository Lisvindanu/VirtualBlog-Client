package com.virtualsblog.project.data.mapper

import com.virtualsblog.project.data.local.entities.UserEntity
import com.virtualsblog.project.domain.model.User

object UserMapper {

    fun mapEntityToDomain(entity: UserEntity): User {
        return User(
            id = entity.id,
            username = entity.username,
            fullname = entity.fullname,
            email = entity.email,
            image = entity.image,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    fun mapDomainToEntity(user: User, isCurrent: Boolean = false): UserEntity {
        return UserEntity(
            id = user.id,
            username = user.username,
            fullname = user.fullname,
            email = user.email,
            image = user.image,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt,
            isCurrent = isCurrent
        )
    }

    fun mapEntitiesToDomain(entities: List<UserEntity>): List<User> {
        return entities.map { mapEntityToDomain(it) }
    }

    fun mapDomainToEntities(users: List<User>): List<UserEntity> {
        return users.map { mapDomainToEntity(it) }
    }
}