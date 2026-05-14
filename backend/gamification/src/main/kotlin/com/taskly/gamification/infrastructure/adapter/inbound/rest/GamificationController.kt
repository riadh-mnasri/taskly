package com.taskly.gamification.infrastructure.adapter.inbound.rest

import com.taskly.gamification.application.GamificationService
import com.taskly.gamification.application.UserStats
import com.taskly.gamification.domain.model.EarnedBadge
import com.taskly.gamification.domain.model.UserProgress
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/v1/gamification")
@Tag(name = "Gamification", description = "XP and badges")
@SecurityRequirement(name = "bearerAuth")
class GamificationController(private val service: GamificationService) {

    @GetMapping("/me")
    @Operation(summary = "Get current user progress")
    fun getMyProgress(auth: Authentication): UserProgressResponse =
        service.getProgress(auth.principal as String).toResponse()

    @GetMapping("/me/stats")
    @Operation(summary = "Get stats for the current user (last 7 days + streak)")
    fun getMyStats(auth: Authentication): StatsResponse =
        service.getStats(auth.principal as String).toResponse()
}

data class UserProgressResponse(
    val xp: Int,
    val level: Int,
    val levelName: String,
    val xpForCurrentLevel: Int,
    val xpForNextLevel: Int,
    val totalTasksDone: Int,
    val badges: List<BadgeResponse>
)

data class BadgeResponse(
    val code: String,
    val label: String,
    val description: String,
    val emoji: String,
    val earnedAt: LocalDateTime
)

data class StatsResponse(
    val last7Days: List<DayStatResponse>,
    val streak: Int,
    val xpThisWeek: Int,
    val xpLastWeek: Int,
    val completionsThisWeek: Int
)

data class DayStatResponse(
    val date: LocalDate,
    val count: Int,
    val xpGained: Int
)

private fun UserProgress.toResponse() = UserProgressResponse(
    xp = xp,
    level = level,
    levelName = levelName,
    xpForCurrentLevel = xpForCurrentLevel,
    xpForNextLevel = xpForNextLevel,
    totalTasksDone = totalTasksDone,
    badges = badges.map { it.toResponse() }
)

private fun EarnedBadge.toResponse() = BadgeResponse(
    code = code.name,
    label = code.label,
    description = code.description,
    emoji = code.emoji,
    earnedAt = earnedAt
)

private fun UserStats.toResponse() = StatsResponse(
    last7Days = last7Days.map { DayStatResponse(it.date, it.count, it.xpGained) },
    streak = streak,
    xpThisWeek = xpThisWeek,
    xpLastWeek = xpLastWeek,
    completionsThisWeek = completionsThisWeek
)
