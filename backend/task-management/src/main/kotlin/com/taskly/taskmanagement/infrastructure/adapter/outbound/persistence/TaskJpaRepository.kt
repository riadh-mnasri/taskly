package com.taskly.taskmanagement.infrastructure.adapter.outbound.persistence

import com.taskly.taskmanagement.domain.model.Priority
import com.taskly.taskmanagement.domain.model.TaskStatus
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TaskJpaRepository : JpaRepository<TaskJpaEntity, UUID> {

    fun findByUserId(userId: UUID): List<TaskJpaEntity>

    fun findByUserIdAndPriority(userId: UUID, priority: Priority): List<TaskJpaEntity>

    fun findByUserIdAndStatus(userId: UUID, status: TaskStatus): List<TaskJpaEntity>

    fun findByUserIdAndPriorityAndStatus(
        userId: UUID,
        priority: Priority,
        status: TaskStatus
    ): List<TaskJpaEntity>
}
