package com.taskly.identity.infrastructure.adapter.outbound.persistence

import com.taskly.identity.domain.model.Email
import com.taskly.identity.domain.model.HashedPassword
import com.taskly.identity.domain.model.User
import com.taskly.identity.domain.port.outbound.UserRepository
import com.taskly.sharedkernel.domain.model.UserId

class UserRepositoryAdapter(
    private val jpaRepository: UserJpaRepository
) : UserRepository {

    override fun save(user: User): User {
        val entity = user.toEntity()
        jpaRepository.save(entity)
        return user
    }

    override fun findByEmail(email: Email): User? =
        jpaRepository.findByEmail(email.value)?.toDomain()

    override fun findById(id: UserId): User? =
        jpaRepository.findById(id.value).orElse(null)?.toDomain()

    override fun existsByEmail(email: Email): Boolean =
        jpaRepository.existsByEmail(email.value)

    private fun User.toEntity() = UserJpaEntity(
        id = this.id.value,
        email = this.email.value,
        hashedPassword = this.hashedPassword.value,
        createdAt = this.createdAt
    )

    private fun UserJpaEntity.toDomain() = User.reconstitute(
        id = UserId.of(this.id),
        email = Email.of(this.email),
        hashedPassword = HashedPassword.of(this.hashedPassword),
        createdAt = this.createdAt
    )
}
