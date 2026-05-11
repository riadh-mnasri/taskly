package com.taskly.taskmanagement.infrastructure.adapter.outbound.persistence

import com.taskly.sharedkernel.domain.model.UserId
import com.taskly.taskmanagement.domain.model.Deadline
import com.taskly.taskmanagement.domain.model.EstimatedDuration
import com.taskly.taskmanagement.domain.model.Priority
import com.taskly.taskmanagement.domain.model.Subject
import com.taskly.taskmanagement.domain.model.Task
import com.taskly.taskmanagement.domain.model.TaskId
import com.taskly.taskmanagement.domain.port.outbound.SortDirection
import com.taskly.taskmanagement.domain.port.outbound.SortField
import com.taskly.taskmanagement.domain.port.outbound.TaskFilter
import com.taskly.taskmanagement.domain.port.outbound.TaskRepository

class TaskRepositoryAdapter(
    private val jpaRepository: TaskJpaRepository
) : TaskRepository {

    override fun save(task: Task): Task {
        jpaRepository.save(task.toEntity())
        return task
    }

    override fun findById(id: TaskId): Task? =
        jpaRepository.findById(id.value).orElse(null)?.toDomain()

    override fun findByUserId(userId: UserId, filter: TaskFilter): List<Task> {
        val entities = when {
            filter.priority != null && filter.status != null ->
                jpaRepository.findByUserIdAndPriorityAndStatus(userId.value, filter.priority, filter.status)
            filter.priority != null ->
                jpaRepository.findByUserIdAndPriority(userId.value, filter.priority)
            filter.status != null ->
                jpaRepository.findByUserIdAndStatus(userId.value, filter.status)
            else ->
                jpaRepository.findByUserId(userId.value)
        }

        var results = entities.map { it.toDomain() }

        // Apply subject filter in-memory (case-insensitive contains)
        if (filter.subject != null) {
            val subjectLower = filter.subject.lowercase()
            results = results.filter { it.subject.value.lowercase().contains(subjectLower) }
        }

        // Sort
        results = when (filter.sortBy) {
            SortField.PRIORITY -> results.sortedBy { it.priority.ordinal }
            SortField.DUE_DATE -> results.sortedBy { it.deadline.value }
        }

        return if (filter.sortDirection == SortDirection.DESC) results.reversed() else results
    }

    override fun delete(id: TaskId) {
        jpaRepository.deleteById(id.value)
    }

    override fun existsById(id: TaskId): Boolean =
        jpaRepository.existsById(id.value)

    private fun Task.toEntity() = TaskJpaEntity(
        id = this.id.value,
        userId = this.userId.value,
        title = this.title,
        description = this.description,
        subject = this.subject.value,
        priority = this.priority,
        status = this.status,
        type = this.type,
        dueDate = this.deadline.value,
        estimatedDurationMinutes = this.estimatedDuration.minutes,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )

    private fun TaskJpaEntity.toDomain() = Task.reconstitute(
        id = TaskId.of(this.id),
        userId = UserId.of(this.userId),
        title = this.title,
        description = this.description,
        subject = Subject.of(this.subject),
        priority = this.priority,
        status = this.status,
        type = this.type,
        deadline = Deadline.ofExisting(this.dueDate),
        estimatedDuration = EstimatedDuration.of(this.estimatedDurationMinutes),
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}
