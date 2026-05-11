package com.taskly.taskmanagement.application.service

import com.taskly.sharedkernel.domain.model.UserId
import com.taskly.taskmanagement.domain.exception.TaskAccessDeniedException
import com.taskly.taskmanagement.domain.exception.TaskNotFoundException
import com.taskly.taskmanagement.domain.model.Deadline
import com.taskly.taskmanagement.domain.model.EstimatedDuration
import com.taskly.taskmanagement.domain.model.Subject
import com.taskly.taskmanagement.domain.model.Task
import com.taskly.taskmanagement.domain.model.TaskId
import com.taskly.taskmanagement.domain.model.TaskStatus
import com.taskly.taskmanagement.domain.port.inbound.ChangeTaskStatusUseCase
import com.taskly.taskmanagement.domain.port.inbound.CreateTaskCommand
import com.taskly.taskmanagement.domain.port.inbound.CreateTaskUseCase
import com.taskly.taskmanagement.domain.port.inbound.DeleteTaskUseCase
import com.taskly.taskmanagement.domain.port.inbound.GetTaskByIdUseCase
import com.taskly.taskmanagement.domain.port.inbound.ListTasksForUserUseCase
import com.taskly.taskmanagement.domain.port.inbound.ListTasksQuery
import com.taskly.taskmanagement.domain.port.inbound.MarkTaskAsDoneUseCase
import com.taskly.taskmanagement.domain.port.inbound.UpdateTaskCommand
import com.taskly.taskmanagement.domain.port.inbound.UpdateTaskUseCase
import com.taskly.taskmanagement.domain.port.outbound.SortDirection
import com.taskly.taskmanagement.domain.port.outbound.SortField
import com.taskly.taskmanagement.domain.port.outbound.TaskFilter
import com.taskly.taskmanagement.domain.port.outbound.TaskRepository

class TaskService(
    private val taskRepository: TaskRepository
) : CreateTaskUseCase,
    UpdateTaskUseCase,
    DeleteTaskUseCase,
    GetTaskByIdUseCase,
    ListTasksForUserUseCase,
    MarkTaskAsDoneUseCase,
    ChangeTaskStatusUseCase {

    override fun create(command: CreateTaskCommand): Task {
        val task = Task.create(
            id = TaskId.generate(),
            userId = command.userId,
            title = command.title,
            description = command.description,
            subject = Subject.of(command.subject),
            priority = command.priority,
            type = command.type,
            deadline = Deadline.of(command.dueDate),
            estimatedDuration = EstimatedDuration.of(command.estimatedDurationMinutes)
        )
        return taskRepository.save(task)
    }

    override fun update(command: UpdateTaskCommand): Task {
        val task = requireOwnedTask(command.taskId, command.userId)
        val updated = task.update(
            title = command.title,
            description = command.description,
            subject = Subject.of(command.subject),
            priority = command.priority,
            type = command.type,
            deadline = Deadline.ofExisting(command.dueDate),
            estimatedDuration = EstimatedDuration.of(command.estimatedDurationMinutes)
        )
        return taskRepository.save(updated)
    }

    override fun delete(taskId: String, userId: UserId) {
        requireOwnedTask(taskId, userId)
        taskRepository.delete(TaskId.of(taskId))
    }

    override fun getById(taskId: String, userId: UserId): Task =
        requireOwnedTask(taskId, userId)

    override fun list(query: ListTasksQuery): List<Task> {
        val filter = TaskFilter(
            priority = query.priority,
            status = query.status,
            subject = query.subject,
            sortBy = if (query.sortBy == "priority") SortField.PRIORITY else SortField.DUE_DATE,
            sortDirection = if (query.sortDirection == "desc") SortDirection.DESC else SortDirection.ASC
        )
        return taskRepository.findByUserId(query.userId, filter)
    }

    override fun markAsDone(taskId: String, userId: UserId): Task {
        val task = requireOwnedTask(taskId, userId)
        return taskRepository.save(task.markAsDone())
    }

    override fun changeStatus(taskId: String, userId: UserId, newStatus: TaskStatus): Task {
        val task = requireOwnedTask(taskId, userId)
        return taskRepository.save(task.changeStatus(newStatus))
    }

    private fun requireOwnedTask(taskId: String, userId: UserId): Task {
        val task = taskRepository.findById(TaskId.of(taskId))
            ?: throw TaskNotFoundException(taskId)
        if (!task.belongsTo(userId)) throw TaskAccessDeniedException(taskId)
        return task
    }
}
