package com.taskly.identity.application

import com.taskly.identity.application.service.RegisterUserService
import com.taskly.identity.domain.exception.EmailAlreadyUsedException
import com.taskly.identity.domain.model.Email
import com.taskly.identity.domain.model.HashedPassword
import com.taskly.identity.domain.model.User
import com.taskly.identity.domain.port.inbound.RegisterUserCommand
import com.taskly.identity.domain.port.outbound.PasswordHasher
import com.taskly.identity.domain.port.outbound.UserRepository
import com.taskly.sharedkernel.domain.model.UserId
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class RegisterUserServiceTest {

    private val userRepository: UserRepository = mock()
    private val passwordHasher: PasswordHasher = mock()
    private lateinit var service: RegisterUserService

    @BeforeEach
    fun setUp() {
        service = RegisterUserService(userRepository, passwordHasher)
    }

    @Test
    fun `registers a user successfully when email is not taken`() {
        val command = RegisterUserCommand("alice@example.com", "SecurePass1!")
        val hashedPwd = HashedPassword.of("hashed")

        whenever(userRepository.existsByEmail(any())).thenReturn(false)
        whenever(passwordHasher.hash(any())).thenReturn(hashedPwd)
        whenever(userRepository.save(any())).thenAnswer { it.arguments[0] as User }

        val userId = service.register(command)

        assertThat(userId).isNotNull
        verify(userRepository).save(any())
    }

    @Test
    fun `throws EmailAlreadyUsedException when email is already registered`() {
        whenever(userRepository.existsByEmail(any())).thenReturn(true)

        assertThatThrownBy {
            service.register(RegisterUserCommand("existing@example.com", "SecurePass1!"))
        }.isInstanceOf(EmailAlreadyUsedException::class.java)

        verify(userRepository, never()).save(any())
    }

    @Test
    fun `throws IllegalArgumentException for invalid email format`() {
        assertThatThrownBy {
            service.register(RegisterUserCommand("not-an-email", "SecurePass1!"))
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `throws IllegalArgumentException for weak password`() {
        whenever(userRepository.existsByEmail(any())).thenReturn(false)

        assertThatThrownBy {
            service.register(RegisterUserCommand("alice@example.com", "weak"))
        }.isInstanceOf(IllegalArgumentException::class.java)
    }
}
