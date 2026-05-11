package com.taskly.identity.application.service

import com.taskly.identity.domain.exception.InvalidCredentialsException
import com.taskly.identity.domain.model.Email
import com.taskly.identity.domain.model.RawPassword
import com.taskly.identity.domain.port.inbound.AuthenticateUserCommand
import com.taskly.identity.domain.port.inbound.AuthenticateUserUseCase
import com.taskly.identity.domain.port.inbound.TokenPair
import com.taskly.identity.domain.port.outbound.PasswordHasher
import com.taskly.identity.domain.port.outbound.TokenGenerator
import com.taskly.identity.domain.port.outbound.UserRepository

class AuthenticateUserService(
    private val userRepository: UserRepository,
    private val passwordHasher: PasswordHasher,
    private val tokenGenerator: TokenGenerator
) : AuthenticateUserUseCase {

    override fun authenticate(command: AuthenticateUserCommand): TokenPair {
        val email = try {
            Email.of(command.email)
        } catch (e: IllegalArgumentException) {
            throw InvalidCredentialsException()
        }

        val user = userRepository.findByEmail(email) ?: throw InvalidCredentialsException()

        val rawPassword = try {
            RawPassword.of(command.rawPassword)
        } catch (e: IllegalArgumentException) {
            throw InvalidCredentialsException()
        }

        if (!passwordHasher.matches(rawPassword, user.hashedPassword)) {
            throw InvalidCredentialsException()
        }

        return TokenPair(
            accessToken = tokenGenerator.generateAccessToken(user.id, user.email.value),
            refreshToken = tokenGenerator.generateRefreshToken(user.id),
            expiresIn = tokenGenerator.accessTokenExpiresIn()
        )
    }
}
