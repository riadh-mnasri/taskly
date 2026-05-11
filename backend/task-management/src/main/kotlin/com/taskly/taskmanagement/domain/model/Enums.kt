package com.taskly.taskmanagement.domain.model

enum class Priority {
    HIGH, MEDIUM, LOW;

    fun isHigherThan(other: Priority): Boolean = this.ordinal < other.ordinal
}

enum class TaskType {
    HOMEWORK, EXAM, PROJECT, PERSONAL, HEALTH
}

enum class TaskStatus {
    TODO, IN_PROGRESS, DONE
}
