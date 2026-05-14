package com.taskly.gamification.infrastructure.adapter.outbound.persistence

import com.taskly.gamification.domain.model.BadgeCode
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface UserProgressJpaRepository : JpaRepository<UserProgressJpaEntity, String> {
    @Modifying
    @Query("""
        INSERT INTO user_progress (user_id, xp, total_tasks_done, high_priority_done)
        VALUES (:userId, 0, 0, 0)
        ON CONFLICT (user_id) DO NOTHING
    """, nativeQuery = true)
    fun insertIfAbsent(userId: String)

    @Modifying
    @Query("""
        UPDATE user_progress
        SET xp = xp + :xp,
            total_tasks_done = total_tasks_done + 1,
            high_priority_done = high_priority_done + :highPriorityIncrement
        WHERE user_id = :userId
    """, nativeQuery = true)
    fun incrementStats(userId: String, xp: Int, highPriorityIncrement: Int)
}

interface UserBadgeJpaRepository : JpaRepository<UserBadgeJpaEntity, Long> {
    fun existsByUserIdAndBadgeCode(userId: String, badgeCode: BadgeCode): Boolean
    fun findAllByUserId(userId: String): List<UserBadgeJpaEntity>
}
