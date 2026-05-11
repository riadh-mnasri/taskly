package com.taskly.taskmanagement.domain.port.inbound

import com.taskly.sharedkernel.domain.model.UserId
import com.taskly.taskmanagement.domain.model.Priority
import com.taskly.taskmanagement.domain.model.Task
import com.taskly.taskmanagement.domain.model.TaskStatus
import com.taskly.taskmanagement.domain.model.TaskType
import java.time.LocalDate

interface CreateTaskUseCase {
    fun create(command: CreateTaskCommand): Task
}

interface UpdateTaskUseCase {
    fun update(command: UpdateTaskCommand): Task
}

interface DeleteTaskUseCase {
    fun delete(taskId: String, userId: UserId)
}

interface GetTaskByIdUseCase {
    fun getById(taskId: String, userId: UserId): Task
}

interface ListTasksForUserUseCase {
    fun list(query: ListTasksQuery): List<Task>
}

interface MarkTaskAsDoneUseCase {
    fun markAsDone(taskId: String, userId: UserId): Task
}

interface ChangeTaskStatusUseCase {
    fun changeStatus(taskId: String, userId: UserId, newStatus: TaskStatus): Task
}

data class CreateTaskCommand(
    val userId: UserId,
    val title: String,
    val description: String?,
    val subject: String,
    val priority: Priority,
    val type: TaskType,
    val dueDate: LocalDate,
    val estimatedDurationMinutes: Int
)

data class UpdateTaskCommand(
    val taskId: String,
    val userId: UserId,
    val title: String,
    val description: String?,
    val subject: String,
    val priority: Priority,
    val type: TaskType,
    val dueDate: LocalDate,
    val estimatedDurationMinutes: Int
)

data class ListTasksQuery(
    val userId: UserId,
    val priority: Priority? = null,
    val status: TaskStatus? = null,
    val subject: String? = null,
    val sortBy: String = "dueDate",
    val sortDirection: String = "asc"
)
