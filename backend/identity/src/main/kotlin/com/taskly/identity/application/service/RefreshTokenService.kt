package com.taskly.identity.application.service

import com.taskly.identity.domain.exception.InvalidTokenException
import com.taskly.identity.domain.exception.UserNotFoundException
import com.taskly.identity.domain.port.inbound.RefreshTokenCommand
import com.taskly.identity.domain.port.inbound.RefreshTokenUseCase
import com.taskly.identity.domain.port.inbound.TokenPair
import com.taskly.identity.domain.port.outbound.TokenGenerator
import com.taskly.identity.domain.port.outbound.UserRepository

class RefreshTokenService(
    private val userRepository: UserRepository,
    private val tokenGenerator: TokenGenerator
) : RefreshTokenUseCase {

    override fun refresh(command: RefreshTokenCommand): TokenPair {
        val userId = try {
            tokenGenerator.validateRefreshToken(command.refreshToken)
        } catch (e: Exception) {
            throw InvalidTokenException()
        }

        val user = userRepository.findById(userId)
            ?: throw UserNotFoundException(userId.toString())

        return TokenPair(
            accessToken = tokenGenerator.generateAccessToken(user.id, user.email.value),
            refreshToken = tokenGenerator.generateRefreshToken(user.id),
            expiresIn = tokenGenerator.accessTokenExpiresIn()
        )
    }
}
