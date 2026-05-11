package com.taskly.identity.domain.port.outbound

import com.taskly.identity.domain.model.Email
import com.taskly.identity.domain.model.User
import com.taskly.sharedkernel.domain.model.UserId

interface UserRepository {
    fun save(user: User): User
    fun findByEmail(email: Email): User?
    fun findById(id: UserId): User?
    fun existsByEmail(email: Email): Boolean
}
