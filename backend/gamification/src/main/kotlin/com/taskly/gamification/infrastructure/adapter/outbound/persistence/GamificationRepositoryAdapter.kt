package com.taskly.gamification.infrastructure.adapter.outbound.persistence

import com.taskly.gamification.domain.model.BadgeCode
import com.taskly.gamification.domain.model.EarnedBadge
import com.taskly.gamification.domain.port.outbound.UserProgressRepository
import org.springframework.transaction.annotation.Transactional

@Transactional
class GamificationRepositoryAdapter(
    private val progressRepo: UserProgressJpaRepository,
    private val badgeRepo: UserBadgeJpaRepository
) : UserProgressRepository {

    override fun initIfAbsent(userId: String) {
        progressRepo.insertIfAbsent(userId)
    }

    override fun findXp(userId: String): Int =
        progressRepo.findById(userId).map { it.xp }.orElse(0)

    override fun findTotalTasksDone(userId: String): Int =
        progressRepo.findById(userId).map { it.totalTasksDone }.orElse(0)

    override fun findHighPriorityDone(userId: String): Int =
        progressRepo.findById(userId).map { it.highPriorityDone }.orElse(0)

    override fun incrementTasksDone(userId: String, xpGained: Int, isHighPriority: Boolean) {
        progressRepo.incrementStats(userId, xpGained, if (isHighPriority) 1 else 0)
    }

    override fun hasBadge(userId: String, badge: BadgeCode): Boolean =
        badgeRepo.existsByUserIdAndBadgeCode(userId, badge)

    override fun awardBadge(userId: String, badge: BadgeCode) {
        badgeRepo.save(UserBadgeJpaEntity(userId = userId, badgeCode = badge))
    }

    override fun findBadges(userId: String): List<EarnedBadge> =
        badgeRepo.findAllByUserId(userId).map { EarnedBadge(it.badgeCode, it.earnedAt) }
}
