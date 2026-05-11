package com.taskly.identity.domain.port.outbound

import com.taskly.identity.domain.model.HashedPassword
import com.taskly.identity.domain.model.RawPassword

interface PasswordHasher {
    fun hash(rawPassword: RawPassword): HashedPassword
    fun matches(rawPassword: RawPassword, hashedPassword: HashedPassword): Boolean
}
