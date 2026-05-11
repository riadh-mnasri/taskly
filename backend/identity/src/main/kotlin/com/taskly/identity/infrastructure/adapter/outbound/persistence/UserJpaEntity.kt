package com.taskly.identity.infrastructure.adapter.outbound.persistence

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "users")
class UserJpaEntity(
    @Id
    val id: UUID,

    @Column(name = "email", nullable = false, unique = true, length = 254)
    val email: String,

    @Column(name = "hashed_password", nullable = false)
    val hashedPassword: String,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant
)
