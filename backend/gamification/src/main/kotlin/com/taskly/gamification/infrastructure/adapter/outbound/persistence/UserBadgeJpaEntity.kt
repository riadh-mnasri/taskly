package com.taskly.gamification.infrastructure.adapter.outbound.persistence

import com.taskly.gamification.domain.model.BadgeCode
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "user_badges")
class UserBadgeJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "user_id", nullable = false)
    val userId: String = "",

    @Enumerated(EnumType.STRING)
    @Column(name = "badge_code", nullable = false)
    val badgeCode: BadgeCode = BadgeCode.FIRST_TASK,

    @Column(name = "earned_at", nullable = false)
    val earnedAt: LocalDateTime = LocalDateTime.now()
)
