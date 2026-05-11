package com.taskly.taskmanagement.infrastructure.adapter.outbound.persistence

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

@Entity
@Table(name = "tasks")
class TaskJpaEntity(
    @Id
    val id: UUID,

    @Column(name = "user_id", nullable = false)
    val userId: UUID,

    @Column(name = "title", nullable = false, length = 200)
    val title: String,

    @Column(name = "description", columnDefinition = "TEXT")
    val description: String?,

    @Column(name = "subject", nullable = false, length = 100)
    val subject: String,

    @Column(name = "priority", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    val priority: com.taskly.taskmanagement.domain.model.Priority,

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    val status: com.taskly.taskmanagement.domain.model.TaskStatus,

    @Column(name = "type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    val type: com.taskly.taskmanagement.domain.model.TaskType,

    @Column(name = "due_date", nullable = false)
    val dueDate: LocalDate,

    @Column(name = "estimated_duration_minutes", nullable = false)
    val estimatedDurationMinutes: Int,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant,

    @Column(name = "updated_at", nullable = false)
    val updatedAt: Instant
)
