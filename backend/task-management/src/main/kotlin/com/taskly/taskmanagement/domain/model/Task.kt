package com.taskly.taskmanagement.domain.model

import com.taskly.sharedkernel.domain.model.UserId
import java.time.Instant

class Task private constructor(
    val id: TaskId,
    val userId: UserId,
    val title: String,
    val description: String?,
    val subject: Subject,
    val priority: Priority,
    val status: TaskStatus,
    val type: TaskType,
    val deadline: Deadline,
    val estimatedDuration: EstimatedDuration,
    val createdAt: Instant,
    val updatedAt: Instant
) {

    companion object {
        fun create(
            id: TaskId,
            userId: UserId,
            title: String,
            description: String?,
            subject: Subject,
            priority: Priority,
            type: TaskType,
            deadline: Deadline,
            estimatedDuration: EstimatedDuration,
            now: Instant = Instant.now()
        ): Task {
            require(title.isNotBlank()) { "Title must not be blank" }
            require(title.length <= 200) { "Title must not exceed 200 characters" }
            return Task(
                id = id,
                userId = userId,
                title = title.trim(),
                description = description?.trim()?.takeIf { it.isNotBlank() },
                subject = subject,
                priority = priority,
                status = TaskStatus.TODO,
                type = type,
                deadline = deadline,
                estimatedDuration = estimatedDuration,
                createdAt = now,
                updatedAt = now
            )
        }

        fun reconstitute(
            id: TaskId,
            userId: UserId,
            title: String,
            description: String?,
            subject: Subject,
            priority: Priority,
            status: TaskStatus,
            type: TaskType,
            deadline: Deadline,
            estimatedDuration: EstimatedDuration,
            createdAt: Instant,
            updatedAt: Instant
        ): Task = Task(id, userId, title, description, subject, priority, status, type, deadline, estimatedDuration, createdAt, updatedAt)
    }

    fun update(
        title: String,
        description: String?,
        subject: Subject,
        priority: Priority,
        type: TaskType,
        deadline: Deadline,
        estimatedDuration: EstimatedDuration,
        now: Instant = Instant.now()
    ): Task {
        require(title.isNotBlank()) { "Title must not be blank" }
        require(title.length <= 200) { "Title must not exceed 200 characters" }
        return Task(
            id = this.id,
            userId = this.userId,
            title = title.trim(),
            description = description?.trim()?.takeIf { it.isNotBlank() },
            subject = subject,
            priority = priority,
            status = this.status,
            type = type,
            deadline = deadline,
            estimatedDuration = estimatedDuration,
            createdAt = this.createdAt,
            updatedAt = now
        )
    }

    fun changeStatus(newStatus: TaskStatus, now: Instant = Instant.now()): Task =
        Task(id, userId, title, description, subject, priority, newStatus, type, deadline, estimatedDuration, createdAt, now)

    fun markAsDone(now: Instant = Instant.now()): Task = changeStatus(TaskStatus.DONE, now)

    fun belongsTo(userId: UserId): Boolean = this.userId == userId
}
