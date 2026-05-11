package com.taskly.taskmanagement.infrastructure.adapter.inbound.rest

import com.taskly.sharedkernel.domain.model.UserId
import com.taskly.taskmanagement.application.service.TaskService
import com.taskly.taskmanagement.domain.model.Priority
import com.taskly.taskmanagement.domain.model.Task
import com.taskly.taskmanagement.domain.model.TaskStatus
import com.taskly.taskmanagement.domain.model.TaskType
import com.taskly.taskmanagement.domain.port.inbound.CreateTaskCommand
import com.taskly.taskmanagement.domain.port.inbound.ListTasksQuery
import com.taskly.taskmanagement.domain.port.inbound.UpdateTaskCommand
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.time.LocalDate

@RestController
@RequestMapping("/api/v1/tasks")
@Tag(name = "Tasks", description = "Task management operations")
@SecurityRequirement(name = "bearerAuth")
class TaskController(private val taskService: TaskService) {

    @GetMapping
    @Operation(summary = "List tasks for the current user")
    fun list(
        @RequestParam(required = false) priority: Priority?,
        @RequestParam(required = false) status: TaskStatus?,
        @RequestParam(required = false) subject: String?,
        @RequestParam(defaultValue = "dueDate") sort: String,
        @RequestParam(defaultValue = "asc") direction: String,
        auth: Authentication
    ): List<TaskResponse> =
        taskService.list(
            ListTasksQuery(
                userId = auth.toUserId(),
                priority = priority,
                status = status,
                subject = subject,
                sortBy = sort,
                sortDirection = direction
            )
        ).map { it.toResponse() }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new task")
    fun create(@Valid @RequestBody request: CreateTaskRequest, auth: Authentication): TaskResponse =
        taskService.create(
            CreateTaskCommand(
                userId = auth.toUserId(),
                title = request.title,
                description = request.description,
                subject = request.subject,
                priority = request.priority,
                type = request.type,
                dueDate = request.dueDate,
                estimatedDurationMinutes = request.estimatedDurationMinutes
            )
        ).toResponse()

    @GetMapping("/{id}")
    @Operation(summary = "Get a task by ID")
    fun getById(@PathVariable id: String, auth: Authentication): TaskResponse =
        taskService.getById(id, auth.toUserId()).toResponse()

    @PutMapping("/{id}")
    @Operation(summary = "Update a task")
    fun update(
        @PathVariable id: String,
        @Valid @RequestBody request: UpdateTaskRequest,
        auth: Authentication
    ): TaskResponse =
        taskService.update(
            UpdateTaskCommand(
                taskId = id,
                userId = auth.toUserId(),
                title = request.title,
                description = request.description,
                subject = request.subject,
                priority = request.priority,
                type = request.type,
                dueDate = request.dueDate,
                estimatedDurationMinutes = request.estimatedDurationMinutes
            )
        ).toResponse()

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a task")
    fun delete(@PathVariable id: String, auth: Authentication) =
        taskService.delete(id, auth.toUserId())

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update task status")
    fun updateStatus(
        @PathVariable id: String,
        @Valid @RequestBody request: UpdateStatusRequest,
        auth: Authentication
    ): TaskResponse =
        taskService.changeStatus(id, auth.toUserId(), request.status).toResponse()

    private fun Authentication.toUserId(): UserId = UserId.of(this.principal as String)
}

data class CreateTaskRequest(
    @field:NotBlank(message = "Title is required")
    @field:Size(max = 200, message = "Title must not exceed 200 characters")
    val title: String,
    val description: String?,
    @field:NotBlank(message = "Subject is required")
    @field:Size(max = 100, message = "Subject must not exceed 100 characters")
    val subject: String,
    @field:NotNull(message = "Priority is required") val priority: Priority,
    @field:NotNull(message = "Type is required") val type: TaskType,
    @field:NotNull(message = "Due date is required") val dueDate: LocalDate,
    @field:NotNull(message = "Estimated duration is required")
    @field:Min(value = 1, message = "Duration must be at least 1 minute")
    @field:Max(value = 480, message = "Duration must not exceed 480 minutes")
    val estimatedDurationMinutes: Int
)

data class UpdateTaskRequest(
    @field:NotBlank(message = "Title is required")
    @field:Size(max = 200) val title: String,
    val description: String?,
    @field:NotBlank(message = "Subject is required")
    @field:Size(max = 100) val subject: String,
    @field:NotNull val priority: Priority,
    @field:NotNull val type: TaskType,
    @field:NotNull val dueDate: LocalDate,
    @field:NotNull @field:Min(1) @field:Max(480) val estimatedDurationMinutes: Int
)

data class UpdateStatusRequest(@field:NotNull(message = "Status is required") val status: TaskStatus)

data class TaskResponse(
    val id: String, val title: String, val description: String?,
    val subject: String, val priority: Priority, val status: TaskStatus,
    val type: TaskType, val dueDate: LocalDate,
    val estimatedDurationMinutes: Int, val createdAt: Instant, val updatedAt: Instant
)

private fun Task.toResponse() = TaskResponse(
    id = this.id.toString(), title = this.title, description = this.description,
    subject = this.subject.value, priority = this.priority, status = this.status,
    type = this.type, dueDate = this.deadline.value,
    estimatedDurationMinutes = this.estimatedDuration.minutes,
    createdAt = this.createdAt, updatedAt = this.updatedAt
)
