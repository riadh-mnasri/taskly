package com.taskly.taskmanagement.domain.model

import java.util.UUID

data class TaskId(val value: UUID) {
    companion object {
        fun generate(): TaskId = TaskId(UUID.randomUUID())
        fun of(value: String): TaskId = TaskId(UUID.fromString(value))
        fun of(value: UUID): TaskId = TaskId(value)
    }

    override fun toString(): String = value.toString()
}
