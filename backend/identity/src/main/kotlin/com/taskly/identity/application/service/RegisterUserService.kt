package com.taskly.identity.application.service

import com.taskly.identity.domain.exception.EmailAlreadyUsedException
import com.taskly.identity.domain.model.Email
import com.taskly.identity.domain.model.RawPassword
import com.taskly.identity.domain.model.User
import com.taskly.identity.domain.port.inbound.RegisterUserCommand
import com.taskly.identity.domain.port.inbound.RegisterUserUseCase
import com.taskly.identity.domain.port.outbound.PasswordHasher
import com.taskly.identity.domain.port.outbound.UserRepository
import com.taskly.sharedkernel.domain.model.UserId

class RegisterUserService(
    private val userRepository: UserRepository,
    private val passwordHasher: PasswordHasher
) : RegisterUserUseCase {

    override fun register(command: RegisterUserCommand): UserId {
        val email = Email.of(command.email)
        val rawPassword = RawPassword.of(command.rawPassword)

        if (userRepository.existsByEmail(email)) {
            throw EmailAlreadyUsedException(email.value)
        }

        val hashedPassword = passwordHasher.hash(rawPassword)
        val user = User.register(
            id = UserId.generate(),
            email = email,
            hashedPassword = hashedPassword
        )

        val saved = userRepository.save(user)
        return saved.id
    }
}
