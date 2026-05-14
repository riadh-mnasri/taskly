package com.taskly.gamification.domain.port.outbound

import com.taskly.gamification.domain.model.BadgeCode
import com.taskly.gamification.domain.model.DayStat
import com.taskly.gamification.domain.model.EarnedBadge
import java.time.LocalDate

interface UserProgressRepository {
    fun findXp(userId: String): Int
    fun findTotalTasksDone(userId: String): Int
    fun findHighPriorityDone(userId: String): Int
    fun incrementTasksDone(userId: String, xpGained: Int, isHighPriority: Boolean)
    fun hasBadge(userId: String, badge: BadgeCode): Boolean
    fun awardBadge(userId: String, badge: BadgeCode)
    fun findBadges(userId: String): List<EarnedBadge>
    fun initIfAbsent(userId: String)
    fun recordDailyCompletion(userId: String, xp: Int)
    fun findDailySince(userId: String, from: LocalDate): List<DayStat>
}
