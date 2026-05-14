package com.taskly.application.reminder

import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.UUID

@Repository
class TaskReminderQueryRepositoryImpl(
    private val em: EntityManager
) : TaskReminderQueryRepository {

    override fun findTasksDueTomorrow(): List<TaskReminderRow> {
        val tomorrow = LocalDate.now().plusDays(1)

        @Suppress("UNCHECKED_CAST")
        val rows = em.createNativeQuery("""
            SELECT t.id, t.title, t.subject, t.due_date, t.priority, u.email
            FROM tasks t
            JOIN users u ON t.user_id = u.id
            WHERE t.due_date = :tomorrow
              AND t.status != 'DONE'
              AND NOT EXISTS (
                SELECT 1 FROM sent_reminders sr
                WHERE sr.task_id = t.id AND sr.reminder_type = '24H'
              )
        """.trimIndent())
            .setParameter("tomorrow", tomorrow)
            .resultList as List<Array<Any>>

        return rows.map { row ->
            TaskReminderRow(
                taskId  = UUID.fromString(row[0].toString()),
                title   = row[1] as String,
                subject = row[2] as String,
                dueDate = (row[3] as java.sql.Date).toLocalDate(),
                priority = row[4] as String,
                userEmail = row[5] as String
            )
        }
    }
}
