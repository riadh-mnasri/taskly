package com.taskly.gamification.domain.model

import java.time.LocalDate

data class DayStat(
    val date: LocalDate,
    val count: Int,
    val xpGained: Int
)
