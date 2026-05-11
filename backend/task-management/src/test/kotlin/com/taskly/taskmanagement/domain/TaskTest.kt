package com.taskly.taskmanagement.domain

import com.taskly.sharedkernel.domain.model.UserId
import com.taskly.taskmanagement.domain.model.Deadline
import com.taskly.taskmanagement.domain.model.EstimatedDuration
import com.taskly.taskmanagement.domain.model.Priority
import com.taskly.taskmanagement.domain.model.Subject
import com.taskly.taskmanagement.domain.model.Task
import com.taskly.taskmanagement.domain.model.TaskId
import com.taskly.taskmanagement.domain.model.TaskStatus
import com.taskly.taskmanagement.domain.model.TaskType
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.time.LocalDate

class TaskTest {

    private val userId = UserId.generate()
    private val tomorrow = LocalDate.now().plusDays(1)

    private fun buildTask(
        title: String = "Math homework",
        userId: UserId = this.userId,
        dueDate: LocalDate = tomorrow
    ) = Task.create(
        id = TaskId.generate(),
        userId = userId,
        title = title,
        description = "Do exercises 1-10",
        subject = Subject.of("Mathematics"),
        priority = Priority.HIGH,
        type = TaskType.HOMEWORK,
        deadline = Deadline.of(dueDate),
        estimatedDuration = EstimatedDuration.of(45)
    )

    @Test
    fun `newly created task has TODO status`() {
        val task = buildTask()
        assertThat(task.status).isEqualTo(TaskStatus.TODO)
    }

    @Test
    fun `marking a task as done changes status to DONE`() {
        val task = buildTask().markAsDone()
        assertThat(task.status).isEqualTo(TaskStatus.DONE)
    }

    @Test
    fun `changing status to IN_PROGRESS works`() {
        val task = buildTask().changeStatus(TaskStatus.IN_PROGRESS)
        assertThat(task.status).isEqualTo(TaskStatus.IN_PROGRESS)
    }

    @Test
    fun `task creation trims whitespace from title`() {
        val task = buildTask(title = "  Homework  ")
        assertThat(task.title).isEqualTo("Homework")
    }

    @Test
    fun `blank title is rejected`() {
        assertThatThrownBy {
            Task.create(
                id = TaskId.generate(),
                userId = userId,
                title = "   ",
                description = null,
                subject = Subject.of("Math"),
                priority = Priority.LOW,
                type = TaskType.HOMEWORK,
                deadline = Deadline.of(tomorrow),
                estimatedDuration = EstimatedDuration.of(30)
            )
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("blank")
    }

    @Test
    fun `task belongs to its owner`() {
        val task = buildTask()
        assertThat(task.belongsTo(userId)).isTrue
    }

    @Test
    fun `task does not belong to a different user`() {
        val task = buildTask()
        assertThat(task.belongsTo(UserId.generate())).isFalse
    }

    @Test
    fun `HIGH priority is higher than MEDIUM`() {
        assertThat(Priority.HIGH.isHigherThan(Priority.MEDIUM)).isTrue
    }

    @Test
    fun `LOW priority is not higher than HIGH`() {
        assertThat(Priority.LOW.isHigherThan(Priority.HIGH)).isFalse
    }
}
