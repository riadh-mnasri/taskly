package com.taskly.gamification.application

import com.taskly.gamification.domain.model.BadgeCode
import com.taskly.gamification.domain.model.UserProgress
import com.taskly.gamification.domain.port.inbound.AwardXpUseCase
import com.taskly.gamification.domain.port.inbound.GetUserProgressUseCase
import com.taskly.gamification.domain.port.outbound.UserProgressRepository
import com.taskly.sharedkernel.domain.event.TaskCompletedEvent
import org.springframework.context.event.EventListener

class GamificationService(
    private val repo: UserProgressRepository
) : GetUserProgressUseCase, AwardXpUseCase {

    @EventListener
    fun onTaskCompleted(event: TaskCompletedEvent) {
        awardXpForTaskCompleted(event.userId, event.priority)
    }

    override fun awardXpForTaskCompleted(userId: String, priority: String) {
        repo.initIfAbsent(userId)

        val xp = when (priority) {
            "HIGH"   -> 30
            "MEDIUM" -> 20
            else     -> 10
        }
        val isHighPriority = priority == "HIGH"
        repo.incrementTasksDone(userId, xp, isHighPriority)

        val total = repo.findTotalTasksDone(userId)
        val highPriority = repo.findHighPriorityDone(userId)

        checkAndAwardBadge(userId, BadgeCode.FIRST_TASK,          total >= 1)
        checkAndAwardBadge(userId, BadgeCode.TEN_TASKS,           total >= 10)
        checkAndAwardBadge(userId, BadgeCode.TWENTY_FIVE_TASKS,   total >= 25)
        checkAndAwardBadge(userId, BadgeCode.FIFTY_TASKS,         total >= 50)
        checkAndAwardBadge(userId, BadgeCode.FIVE_HIGH_PRIORITY,  highPriority >= 5)
    }

    override fun getProgress(userId: String): UserProgress {
        repo.initIfAbsent(userId)
        return UserProgress(
            userId = userId,
            xp = repo.findXp(userId),
            totalTasksDone = repo.findTotalTasksDone(userId),
            highPriorityDone = repo.findHighPriorityDone(userId),
            badges = repo.findBadges(userId)
        )
    }

    private fun checkAndAwardBadge(userId: String, badge: BadgeCode, condition: Boolean) {
        if (condition && !repo.hasBadge(userId, badge)) {
            repo.awardBadge(userId, badge)
        }
    }
}
