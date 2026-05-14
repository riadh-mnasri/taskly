package com.taskly.gamification.infrastructure.adapter.outbound.persistence

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.Table
import java.io.Serializable
import java.time.LocalDate

@Entity
@Table(name = "daily_completions")
@IdClass(DailyCompletionId::class)
class DailyCompletionJpaEntity(
    @Id
    @Column(name = "user_id", nullable = false)
    val userId: String = "",

    @Id
    @Column(name = "completion_date", nullable = false)
    val completionDate: LocalDate = LocalDate.now(),

    @Column(nullable = false)
    var count: Int = 0,

    @Column(name = "xp_gained", nullable = false)
    var xpGained: Int = 0
)

data class DailyCompletionId(
    val userId: String = "",
    val completionDate: LocalDate = LocalDate.now()
) : Serializable
