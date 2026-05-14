package com.taskly.gamification.domain.model

enum class BadgeCode(val label: String, val description: String, val emoji: String) {
    FIRST_TASK("Première tâche", "Complète ta première tâche", "🌱"),
    TEN_TASKS("Déterminé", "Complète 10 tâches", "🎯"),
    TWENTY_FIVE_TASKS("Appliqué", "Complète 25 tâches", "⚡"),
    FIFTY_TASKS("Champion", "Complète 50 tâches", "🏆"),
    FIVE_HIGH_PRIORITY("Courageux", "Complète 5 tâches de haute priorité", "🔥"),
    NO_LATE_WEEK("Ponctuel", "Aucun retard cette semaine", "⏰")
}
