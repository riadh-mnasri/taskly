package com.taskly.identity.infrastructure.config

import com.taskly.identity.application.service.AuthenticateUserService
import com.taskly.identity.application.service.RefreshTokenService
import com.taskly.identity.application.service.RegisterUserService
import com.taskly.identity.domain.port.inbound.AuthenticateUserUseCase
import com.taskly.identity.domain.port.inbound.RefreshTokenUseCase
import com.taskly.identity.domain.port.inbound.RegisterUserUseCase
import com.taskly.identity.domain.port.outbound.PasswordHasher
import com.taskly.identity.domain.port.outbound.TokenGenerator
import com.taskly.identity.domain.port.outbound.UserRepository
import com.taskly.identity.infrastructure.adapter.outbound.persistence.UserJpaRepository
import com.taskly.identity.infrastructure.adapter.outbound.persistence.UserRepositoryAdapter
import com.taskly.identity.infrastructure.adapter.outbound.security.BCryptPasswordHasher
import com.taskly.identity.infrastructure.adapter.outbound.security.JwtTokenGenerator
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class IdentityConfig {

    @Bean
    fun passwordHasher(): PasswordHasher = BCryptPasswordHasher()

    @Bean
    fun tokenGenerator(
        @Value("\${taskly.jwt.secret}") secret: String,
        @Value("\${taskly.jwt.access-token-expiry-seconds:900}") accessExpiry: Long,
        @Value("\${taskly.jwt.refresh-token-expiry-seconds:604800}") refreshExpiry: Long
    ): TokenGenerator = JwtTokenGenerator(secret, accessExpiry, refreshExpiry)

    @Bean
    fun userRepository(jpaRepository: UserJpaRepository): UserRepository =
        UserRepositoryAdapter(jpaRepository)

    @Bean
    fun registerUserUseCase(
        userRepository: UserRepository,
        passwordHasher: PasswordHasher
    ): RegisterUserUseCase = RegisterUserService(userRepository, passwordHasher)

    @Bean
    fun authenticateUserUseCase(
        userRepository: UserRepository,
        passwordHasher: PasswordHasher,
        tokenGenerator: TokenGenerator
    ): AuthenticateUserUseCase = AuthenticateUserService(userRepository, passwordHasher, tokenGenerator)

    @Bean
    fun refreshTokenUseCase(
        userRepository: UserRepository,
        tokenGenerator: TokenGenerator
    ): RefreshTokenUseCase = RefreshTokenService(userRepository, tokenGenerator)

    @Bean
    fun jwtAuthenticationFilter(tokenGenerator: TokenGenerator): JwtAuthenticationFilter =
        JwtAuthenticationFilter(tokenGenerator)
}
