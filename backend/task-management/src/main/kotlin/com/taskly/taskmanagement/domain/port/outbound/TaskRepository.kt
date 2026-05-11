package com.taskly.taskmanagement.domain.port.outbound

import com.taskly.sharedkernel.domain.model.UserId
import com.taskly.taskmanagement.domain.model.Priority
import com.taskly.taskmanagement.domain.model.Task
import com.taskly.taskmanagement.domain.model.TaskId
import com.taskly.taskmanagement.domain.model.TaskStatus

interface TaskRepository {
    fun save(task: Task): Task
    fun findById(id: TaskId): Task?
    fun findByUserId(userId: UserId, filter: TaskFilter = TaskFilter()): List<Task>
    fun delete(id: TaskId)
    fun existsById(id: TaskId): Boolean
}

data class TaskFilter(
    val priority: Priority? = null,
    val status: TaskStatus? = null,
    val subject: String? = null,
    val sortBy: SortField = SortField.DUE_DATE,
    val sortDirection: SortDirection = SortDirection.ASC
)

enum class SortField { DUE_DATE, PRIORITY }
enum class SortDirection { ASC, DESC }
