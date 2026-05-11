package com.taskly.identity.infrastructure.adapter.outbound.security

import com.taskly.identity.domain.model.HashedPassword
import com.taskly.identity.domain.model.RawPassword
import com.taskly.identity.domain.port.outbound.PasswordHasher
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class BCryptPasswordHasher : PasswordHasher {

    private val encoder = BCryptPasswordEncoder()

    override fun hash(rawPassword: RawPassword): HashedPassword =
        HashedPassword.of(encoder.encode(rawPassword.value))

    override fun matches(rawPassword: RawPassword, hashedPassword: HashedPassword): Boolean =
        encoder.matches(rawPassword.value, hashedPassword.value)
}
