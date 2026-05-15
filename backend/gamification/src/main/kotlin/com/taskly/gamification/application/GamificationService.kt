package com.taskly.gamification.application

import com.taskly.gamification.domain.model.BadgeCode
import com.taskly.gamification.domain.model.DayStat
import com.taskly.gamification.domain.model.UserProgress
import com.taskly.gamification.domain.port.inbound.GetUserProgressUseCase
import com.taskly.gamification.domain.port.outbound.UserProgressRepository
import com.taskly.sharedkernel.domain.event.TaskCompletedEvent
import org.springframework.context.event.EventListener
import java.time.LocalDate

class GamificationService(
    private val repo: UserProgressRepository
) : GetUserProgressUseCase {

    @EventListener
    fun onTaskCompleted(event: TaskCompletedEvent) {
        // Guard: a task can only be rewarded once, regardless of status changes
        if (repo.hasBeenRewarded(event.taskId)) return

        repo.initIfAbsent(event.userId)

        val xp = when (event.priority) {
            "HIGH"   -> 30
            "MEDIUM" -> 20
            else     -> 10
        }
        val isHighPriority = event.priority == "HIGH"

        repo.markRewarded(event.taskId)
        repo.incrementTasksDone(event.userId, xp, isHighPriority)
        repo.recordDailyCompletion(event.userId, xp)

        val total       = repo.findTotalTasksDone(event.userId)
        val highPriority = repo.findHighPriorityDone(event.userId)

        checkAndAwardBadge(event.userId, BadgeCode.FIRST_TASK,         total >= 1)
        checkAndAwardBadge(event.userId, BadgeCode.TEN_TASKS,          total >= 10)
        checkAndAwardBadge(event.userId, BadgeCode.TWENTY_FIVE_TASKS,  total >= 25)
        checkAndAwardBadge(event.userId, BadgeCode.FIFTY_TASKS,        total >= 50)
        checkAndAwardBadge(event.userId, BadgeCode.FIVE_HIGH_PRIORITY, highPriority >= 5)
    }

    override fun getProgress(userId: String): UserProgress {
        repo.initIfAbsent(userId)
        return UserProgress(
            userId           = userId,
            xp               = repo.findXp(userId),
            totalTasksDone   = repo.findTotalTasksDone(userId),
            highPriorityDone = repo.findHighPriorityDone(userId),
            badges           = repo.findBadges(userId)
        )
    }

    fun getStats(userId: String): UserStats {
        repo.initIfAbsent(userId)
        val today    = LocalDate.now()
        val last7Days  = repo.findDailySince(userId, today.minusDays(6))
        val last14Days = repo.findDailySince(userId, today.minusDays(13))

        return UserStats(
            last7Days            = fillMissingDays(last7Days, today),
            streak               = computeStreak(last14Days, today),
            xpThisWeek           = last7Days.sumOf { it.xpGained },
            xpLastWeek           = last14Days.filter { it.date < today.minusDays(6) }.sumOf { it.xpGained },
            completionsThisWeek  = last7Days.sumOf { it.count }
        )
    }

    private fun fillMissingDays(data: List<DayStat>, today: LocalDate): List<DayStat> {
        val byDate = data.associateBy { it.date }
        return (6 downTo 0).map { offset ->
            val date = today.minusDays(offset.toLong())
            byDate[date] ?: DayStat(date, 0, 0)
        }
    }

    private fun computeStreak(data: List<DayStat>, today: LocalDate): Int {
        val activeDates = data.filter { it.count > 0 }.map { it.date }.toSet()
        var streak = 0
        var current = if (today in activeDates) today else today.minusDays(1)
        while (current in activeDates) {
            streak++
            current = current.minusDays(1)
        }
        return streak
    }

    private fun checkAndAwardBadge(userId: String, badge: BadgeCode, condition: Boolean) {
        if (condition && !repo.hasBadge(userId, badge)) {
            repo.awardBadge(userId, badge)
        }
    }
}

data class UserStats(
    val last7Days: List<DayStat>,
    val streak: Int,
    val xpThisWeek: Int,
    val xpLastWeek: Int,
    val completionsThisWeek: Int
)
