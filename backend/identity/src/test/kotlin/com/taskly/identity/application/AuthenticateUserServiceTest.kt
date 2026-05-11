package com.taskly.identity.application

import com.taskly.identity.application.service.AuthenticateUserService
import com.taskly.identity.domain.exception.InvalidCredentialsException
import com.taskly.identity.domain.model.Email
import com.taskly.identity.domain.model.HashedPassword
import com.taskly.identity.domain.model.User
import com.taskly.identity.domain.port.inbound.AuthenticateUserCommand
import com.taskly.identity.domain.port.outbound.PasswordHasher
import com.taskly.identity.domain.port.outbound.TokenGenerator
import com.taskly.identity.domain.port.outbound.UserRepository
import com.taskly.sharedkernel.domain.model.UserId
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.Instant

class AuthenticateUserServiceTest {

    private val userRepository: UserRepository = mock()
    private val passwordHasher: PasswordHasher = mock()
    private val tokenGenerator: TokenGenerator = mock()
    private lateinit var service: AuthenticateUserService

    private val email = Email.of("alice@example.com")
    private val hashedPassword = HashedPassword.of("hashed")
    private val user = User.reconstitute(UserId.generate(), email, hashedPassword, Instant.now())

    @BeforeEach
    fun setUp() {
        service = AuthenticateUserService(userRepository, passwordHasher, tokenGenerator)
    }

    @Test
    fun `returns token pair when credentials are valid`() {
        whenever(userRepository.findByEmail(any())).thenReturn(user)
        whenever(passwordHasher.matches(any(), any())).thenReturn(true)
        whenever(tokenGenerator.generateAccessToken(any(), any())).thenReturn("access-token")
        whenever(tokenGenerator.generateRefreshToken(any())).thenReturn("refresh-token")
        whenever(tokenGenerator.accessTokenExpiresIn()).thenReturn(900L)

        val result = service.authenticate(AuthenticateUserCommand("alice@example.com", "SecurePass1!"))

        assertThat(result.accessToken).isEqualTo("access-token")
        assertThat(result.refreshToken).isEqualTo("refresh-token")
        assertThat(result.expiresIn).isEqualTo(900L)
    }

    @Test
    fun `throws InvalidCredentialsException when user not found`() {
        whenever(userRepository.findByEmail(any())).thenReturn(null)

        assertThatThrownBy {
            service.authenticate(AuthenticateUserCommand("alice@example.com", "SecurePass1!"))
        }.isInstanceOf(InvalidCredentialsException::class.java)
    }

    @Test
    fun `throws InvalidCredentialsException when password does not match`() {
        whenever(userRepository.findByEmail(any())).thenReturn(user)
        whenever(passwordHasher.matches(any(), any())).thenReturn(false)

        assertThatThrownBy {
            service.authenticate(AuthenticateUserCommand("alice@example.com", "WrongPass1!"))
        }.isInstanceOf(InvalidCredentialsException::class.java)
    }
}
