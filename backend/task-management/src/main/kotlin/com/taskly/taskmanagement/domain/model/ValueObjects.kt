package com.taskly.taskmanagement.domain.model

import java.time.LocalDate

@ConsistentCopyVisibility
data class Subject private constructor(val value: String) {
    companion object {
        private const val MAX_LENGTH = 100

        fun of(raw: String): Subject {
            val trimmed = raw.trim()
            require(trimmed.isNotBlank()) { "Subject must not be blank" }
            require(trimmed.length <= MAX_LENGTH) { "Subject must not exceed $MAX_LENGTH characters" }
            return Subject(trimmed)
        }
    }

    override fun toString(): String = value
}

@ConsistentCopyVisibility
data class Deadline private constructor(val value: LocalDate) {
    companion object {
        fun of(date: LocalDate, today: LocalDate = LocalDate.now()): Deadline {
            require(!date.isBefore(today)) { "Due date must be today or in the future (got $date)" }
            return Deadline(date)
        }

        fun ofExisting(date: LocalDate): Deadline = Deadline(date)
    }
}

@ConsistentCopyVisibility
data class EstimatedDuration private constructor(val minutes: Int) {
    companion object {
        private const val MIN_MINUTES = 1
        private const val MAX_MINUTES = 480 // 8 hours

        fun of(minutes: Int): EstimatedDuration {
            require(minutes >= MIN_MINUTES) { "Estimated duration must be at least $MIN_MINUTES minute" }
            require(minutes <= MAX_MINUTES) { "Estimated duration must not exceed $MAX_MINUTES minutes" }
            return EstimatedDuration(minutes)
        }
    }
}
