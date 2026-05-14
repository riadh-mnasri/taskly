package com.taskly.gamification.domain.model

data class UserProgress(
    val userId: String,
    val xp: Int,
    val totalTasksDone: Int,
    val highPriorityDone: Int,
    val badges: List<EarnedBadge>
) {
    val level: Int get() = when {
        xp >= 1000 -> 5
        xp >= 600  -> 4
        xp >= 300  -> 3
        xp >= 100  -> 2
        else       -> 1
    }

    val levelName: String get() = when (level) {
        5    -> "Expert"
        4    -> "Avancé"
        3    -> "Élève"
        2    -> "Apprenti"
        else -> "Débutant"
    }

    val xpToNextLevel: Int get() = when (level) {
        1    -> 100 - xp
        2    -> 300 - xp
        3    -> 600 - xp
        4    -> 1000 - xp
        else -> 0
    }

    val xpForCurrentLevel: Int get() = when (level) {
        1    -> 0
        2    -> 100
        3    -> 300
        4    -> 600
        5    -> 1000
        else -> 0
    }

    val xpForNextLevel: Int get() = when (level) {
        1    -> 100
        2    -> 300
        3    -> 600
        4    -> 1000
        else -> 1000
    }
}

data class EarnedBadge(
    val code: BadgeCode,
    val earnedAt: java.time.LocalDateTime
)
