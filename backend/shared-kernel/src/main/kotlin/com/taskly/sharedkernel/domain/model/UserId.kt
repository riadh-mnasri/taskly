package com.taskly.sharedkernel.domain.model

import java.util.UUID

data class UserId(val value: UUID) {
    companion object {
        fun generate(): UserId = UserId(UUID.randomUUID())
        fun of(value: String): UserId = UserId(UUID.fromString(value))
        fun of(value: UUID): UserId = UserId(value)
    }

    override fun toString(): String = value.toString()
}
