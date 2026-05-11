package com.taskly.identity.domain

import com.taskly.identity.domain.event.UserRegistered
import com.taskly.identity.domain.model.Email
import com.taskly.identity.domain.model.HashedPassword
import com.taskly.identity.domain.model.User
import com.taskly.sharedkernel.domain.model.UserId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UserTest {

    @Test
    fun `registering a user creates a UserRegistered domain event`() {
        val id = UserId.generate()
        val email = Email.of("student@school.com")
        val hashedPassword = HashedPassword.of("hashed_password")

        val user = User.register(id, email, hashedPassword)

        assertThat(user.domainEvents).hasSize(1)
        assertThat(user.domainEvents[0]).isInstanceOf(UserRegistered::class.java)

        val event = user.domainEvents[0] as UserRegistered
        assertThat(event.userId).isEqualTo(id)
        assertThat(event.email).isEqualTo(email)
    }

    @Test
    fun `clearing events returns all events and empties the list`() {
        val user = User.register(UserId.generate(), Email.of("test@example.com"), HashedPassword.of("hash"))
        assertThat(user.domainEvents).hasSize(1)

        val events = user.clearEvents()
        assertThat(events).hasSize(1)
        assertThat(user.domainEvents).isEmpty()
    }

    @Test
    fun `reconstituted user has no domain events`() {
        val user = User.reconstitute(
            id = UserId.generate(),
            email = Email.of("test@example.com"),
            hashedPassword = HashedPassword.of("hash"),
            createdAt = java.time.Instant.now()
        )
        assertThat(user.domainEvents).isEmpty()
    }
}
