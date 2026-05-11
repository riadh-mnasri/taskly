package com.taskly.identity.domain.event

import com.taskly.identity.domain.model.Email
import com.taskly.sharedkernel.domain.model.UserId
import java.time.Instant

data class UserRegistered(
    val userId: UserId,
    val email: Email,
    val occurredAt: Instant
)
