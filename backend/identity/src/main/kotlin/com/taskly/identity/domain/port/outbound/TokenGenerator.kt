package com.taskly.identity.domain.port.outbound

import com.taskly.sharedkernel.domain.model.UserId

interface TokenGenerator {
    fun generateAccessToken(userId: UserId, email: String): String
    fun generateRefreshToken(userId: UserId): String
    fun validateAccessToken(token: String): TokenClaims
    fun validateRefreshToken(token: String): UserId
    fun accessTokenExpiresIn(): Long
}

data class TokenClaims(
    val userId: UserId,
    val email: String
)
