package com.taskly.identity.domain.port.inbound

interface AuthenticateUserUseCase {
    fun authenticate(command: AuthenticateUserCommand): TokenPair
}

data class AuthenticateUserCommand(
    val email: String,
    val rawPassword: String
)

data class TokenPair(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long
)
