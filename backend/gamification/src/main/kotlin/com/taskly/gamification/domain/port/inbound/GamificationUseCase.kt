package com.taskly.gamification.domain.port.inbound

import com.taskly.gamification.domain.model.UserProgress

interface GetUserProgressUseCase {
    fun getProgress(userId: String): UserProgress
}
