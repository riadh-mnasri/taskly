package com.taskly.application.reminder

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Component
class ReminderScheduler(
    private val queryRepo: TaskReminderQueryRepository,
    private val sentRepo: SentReminderJpaRepository,
    private val emailService: EmailReminderService
) {
    private val log = LoggerFactory.getLogger(javaClass)

    // Every day at 08:00 server time
    @Scheduled(cron = "0 0 8 * * *")
    @Transactional
    fun sendDailyReminders() {
        log.info("Running daily reminder scheduler...")
        val tasks = queryRepo.findTasksDueTomorrow()
        log.info("Found ${tasks.size} task(s) to remind")

        tasks.forEach { task ->
            emailService.sendReminder(task)
            sentRepo.save(SentReminderJpaEntity(
                taskId       = task.taskId,
                reminderType = "24H",
                sentAt       = Instant.now()
            ))
        }
    }
}
