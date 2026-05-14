package com.taskly.application.reminder

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.Table
import java.io.Serializable
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "sent_reminders")
@IdClass(SentReminderId::class)
class SentReminderJpaEntity(
    @Id
    @Column(name = "task_id", nullable = false)
    val taskId: UUID = UUID.randomUUID(),

    @Id
    @Column(name = "reminder_type", nullable = false, length = 10)
    val reminderType: String = "",

    @Column(name = "sent_at", nullable = false)
    val sentAt: Instant = Instant.now()
)

data class SentReminderId(
    val taskId: UUID = UUID.randomUUID(),
    val reminderType: String = ""
) : Serializable
