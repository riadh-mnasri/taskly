package com.taskly.gamification.infrastructure.config

import com.taskly.gamification.application.GamificationService
import com.taskly.gamification.infrastructure.adapter.outbound.persistence.DailyCompletionJpaRepository
import com.taskly.gamification.infrastructure.adapter.outbound.persistence.GamificationRepositoryAdapter
import com.taskly.gamification.infrastructure.adapter.outbound.persistence.RewardedTaskJpaRepository
import com.taskly.gamification.infrastructure.adapter.outbound.persistence.UserBadgeJpaRepository
import com.taskly.gamification.infrastructure.adapter.outbound.persistence.UserProgressJpaRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GamificationConfig {

    @Bean
    fun gamificationRepositoryAdapter(
        progressRepo: UserProgressJpaRepository,
        badgeRepo: UserBadgeJpaRepository,
        dailyRepo: DailyCompletionJpaRepository,
        rewardedRepo: RewardedTaskJpaRepository
    ): GamificationRepositoryAdapter =
        GamificationRepositoryAdapter(progressRepo, badgeRepo, dailyRepo, rewardedRepo)

    @Bean
    fun gamificationService(repo: GamificationRepositoryAdapter): GamificationService =
        GamificationService(repo)
}
