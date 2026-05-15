package com.taskly.gamification.infrastructure.adapter.outbound.persistence

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "rewarded_tasks")
class RewardedTaskJpaEntity(
    @Id
    @Column(name = "task_id", nullable = false)
    val taskId: UUID = UUID.randomUUID(),

    @Column(name = "rewarded_at", nullable = false)
    val rewardedAt: Instant = Instant.now()
)
