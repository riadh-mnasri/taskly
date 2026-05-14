package com.taskly.gamification.infrastructure.adapter.outbound.persistence

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "user_progress")
class UserProgressJpaEntity(
    @Id
    @Column(name = "user_id", nullable = false)
    val userId: String = "",

    @Column(nullable = false)
    var xp: Int = 0,

    @Column(name = "total_tasks_done", nullable = false)
    var totalTasksDone: Int = 0,

    @Column(name = "high_priority_done", nullable = false)
    var highPriorityDone: Int = 0
)
