package com.taskly.gamification.application

import com.taskly.gamification.domain.model.BadgeCode
import com.taskly.gamification.domain.model.DayStat
import com.taskly.gamification.domain.model.UserProgress
import com.taskly.gamification.domain.port.inbound.AwardXpUseCase
import com.taskly.gamification.domain.port.inbound.GetUserProgressUseCase
import com.taskly.gamification.domain.port.outbound.UserProgressRepository
import com.taskly.sharedkernel.domain.event.TaskCompletedEvent
import org.springframework.context.event.EventListener
import java.time.LocalDate

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
        repo.recordDailyCompletion(userId, xp)

        val total = repo.findTotalTasksDone(userId)
        val highPriority = repo.findHighPriorityDone(userId)

        checkAndAwardBadge(userId, BadgeCode.FIRST_TASK,         total >= 1)
        checkAndAwardBadge(userId, BadgeCode.TEN_TASKS,          total >= 10)
        checkAndAwardBadge(userId, BadgeCode.TWENTY_FIVE_TASKS,  total >= 25)
        checkAndAwardBadge(userId, BadgeCode.FIFTY_TASKS,        total >= 50)
        checkAndAwardBadge(userId, BadgeCode.FIVE_HIGH_PRIORITY, highPriority >= 5)
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

    fun getStats(userId: String): UserStats {
        repo.initIfAbsent(userId)
        val today = LocalDate.now()
        val last7Days = repo.findDailySince(userId, today.minusDays(6))
        val last14Days = repo.findDailySince(userId, today.minusDays(13))

        val thisWeekXp = last7Days.sumOf { it.xpGained }
        val lastWeekXp = last14Days
            .filter { it.date < today.minusDays(6) }
            .sumOf { it.xpGained }

        val thisWeekCount = last7Days.sumOf { it.count }

        val filledDays = fillMissingDays(last7Days, today)
        val streak = computeStreak(last14Days, today)

        return UserStats(filledDays, streak, thisWeekXp, lastWeekXp, thisWeekCount)
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
        var current = if (activeDates.contains(today)) today else today.minusDays(1)
        while (activeDates.contains(current)) {
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
