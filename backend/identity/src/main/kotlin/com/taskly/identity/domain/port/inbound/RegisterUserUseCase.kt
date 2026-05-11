package com.taskly.identity.domain.port.inbound

import com.taskly.sharedkernel.domain.model.UserId

interface RegisterUserUseCase {
    fun register(command: RegisterUserCommand): UserId
}

data class RegisterUserCommand(
    val email: String,
    val rawPassword: String
)
