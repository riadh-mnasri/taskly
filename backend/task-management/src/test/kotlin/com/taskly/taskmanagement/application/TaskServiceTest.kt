package com.taskly.taskmanagement.application

import com.taskly.sharedkernel.domain.model.UserId
import com.taskly.taskmanagement.application.service.TaskService
import com.taskly.taskmanagement.domain.exception.TaskAccessDeniedException
import com.taskly.taskmanagement.domain.exception.TaskNotFoundException
import com.taskly.taskmanagement.domain.model.Deadline
import com.taskly.taskmanagement.domain.model.EstimatedDuration
import com.taskly.taskmanagement.domain.model.Priority
import com.taskly.taskmanagement.domain.model.Subject
import com.taskly.taskmanagement.domain.model.Task
import com.taskly.taskmanagement.domain.model.TaskId
import com.taskly.taskmanagement.domain.model.TaskStatus
import com.taskly.taskmanagement.domain.model.TaskType
import com.taskly.taskmanagement.domain.port.inbound.CreateTaskCommand
import com.taskly.taskmanagement.domain.port.outbound.TaskRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.context.ApplicationEventPublisher
import java.time.Instant
import java.time.LocalDate

class TaskServiceTest {

    private val taskRepository: TaskRepository = mock()
    private val eventPublisher: ApplicationEventPublisher = mock()
    private lateinit var service: TaskService

    private val userId = UserId.generate()
    private val tomorrow = LocalDate.now().plusDays(1)

    @BeforeEach
    fun setUp() {
        service = TaskService(taskRepository, eventPublisher)
    }

    private fun buildExistingTask(ownerId: UserId = userId) = Task.reconstitute(
        id = TaskId.generate(),
        userId = ownerId,
        title = "Math homework",
        description = null,
        subject = Subject.of("Math"),
        priority = Priority.HIGH,
        status = TaskStatus.TODO,
        type = TaskType.HOMEWORK,
        deadline = Deadline.ofExisting(tomorrow),
        estimatedDuration = EstimatedDuration.of(45),
        createdAt = Instant.now(),
        updatedAt = Instant.now()
    )

    @Test
    fun `creates a task and saves it`() {
        val command = CreateTaskCommand(
            userId = userId,
            title = "Study for exam",
            description = null,
            subject = "Physics",
            priority = Priority.HIGH,
            type = TaskType.EXAM,
            dueDate = tomorrow,
            estimatedDurationMinutes = 120
        )
        whenever(taskRepository.save(any())).thenAnswer { it.arguments[0] as Task }

        val task = service.create(command)

        assertThat(task.title).isEqualTo("Study for exam")
        assertThat(task.status).isEqualTo(TaskStatus.TODO)
        verify(taskRepository).save(any())
    }

    @Test
    fun `delete throws TaskNotFoundException when task does not exist`() {
        whenever(taskRepository.findById(any())).thenReturn(null)

        assertThatThrownBy { service.delete(TaskId.generate().toString(), userId) }
            .isInstanceOf(TaskNotFoundException::class.java)

        verify(taskRepository, never()).delete(any())
    }

    @Test
    fun `delete throws TaskAccessDeniedException when task belongs to another user`() {
        val otherTask = buildExistingTask(ownerId = UserId.generate())
        whenever(taskRepository.findById(any())).thenReturn(otherTask)

        assertThatThrownBy { service.delete(otherTask.id.toString(), userId) }
            .isInstanceOf(TaskAccessDeniedException::class.java)
    }

    @Test
    fun `markAsDone changes task status to DONE`() {
        val task = buildExistingTask()
        whenever(taskRepository.findById(any())).thenReturn(task)
        whenever(taskRepository.save(any())).thenAnswer { it.arguments[0] as Task }

        val result = service.markAsDone(task.id.toString(), userId)

        assertThat(result.status).isEqualTo(TaskStatus.DONE)
    }

    @Test
    fun `changeStatus changes task status correctly`() {
        val task = buildExistingTask()
        whenever(taskRepository.findById(any())).thenReturn(task)
        whenever(taskRepository.save(any())).thenAnswer { it.arguments[0] as Task }

        val result = service.changeStatus(task.id.toString(), userId, TaskStatus.IN_PROGRESS)

        assertThat(result.status).isEqualTo(TaskStatus.IN_PROGRESS)
    }
}
