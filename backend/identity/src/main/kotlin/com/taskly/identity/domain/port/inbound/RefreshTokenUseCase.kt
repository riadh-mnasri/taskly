package com.taskly.identity.domain.port.inbound

interface RefreshTokenUseCase {
    fun refresh(command: RefreshTokenCommand): TokenPair
}

data class RefreshTokenCommand(val refreshToken: String)
