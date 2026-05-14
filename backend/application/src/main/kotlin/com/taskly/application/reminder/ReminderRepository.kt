package com.taskly.application.reminder

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDate
import java.util.UUID

interface SentReminderJpaRepository : JpaRepository<SentReminderJpaEntity, SentReminderId> {
    fun existsByTaskIdAndReminderType(taskId: UUID, reminderType: String): Boolean
}

data class TaskReminderRow(
    val taskId: UUID,
    val title: String,
    val subject: String,
    val dueDate: LocalDate,
    val priority: String,
    val userEmail: String
)

interface TaskReminderQueryRepository {
    fun findTasksDueTomorrow(): List<TaskReminderRow>
}
