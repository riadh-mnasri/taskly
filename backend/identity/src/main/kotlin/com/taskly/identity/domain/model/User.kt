package com.taskly.identity.domain.model

import com.taskly.identity.domain.event.UserRegistered
import com.taskly.sharedkernel.domain.model.UserId
import java.time.Instant

class User private constructor(
    val id: UserId,
    val email: Email,
    val hashedPassword: HashedPassword,
    val createdAt: Instant,
    val domainEvents: MutableList<Any> = mutableListOf()
) {

    companion object {
        fun register(
            id: UserId,
            email: Email,
            hashedPassword: HashedPassword,
            now: Instant = Instant.now()
        ): User {
            val user = User(id, email, hashedPassword, now)
            user.domainEvents.add(UserRegistered(userId = id, email = email, occurredAt = now))
            return user
        }

        fun reconstitute(
            id: UserId,
            email: Email,
            hashedPassword: HashedPassword,
            createdAt: Instant
        ): User = User(id, email, hashedPassword, createdAt)
    }

    fun clearEvents(): List<Any> {
        val events = domainEvents.toList()
        domainEvents.clear()
        return events
    }
}
