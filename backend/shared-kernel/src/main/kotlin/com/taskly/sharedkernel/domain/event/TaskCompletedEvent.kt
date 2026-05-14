package com.taskly.sharedkernel.domain.event

data class TaskCompletedEvent(
    val userId: String,
    val taskId: String,
    val priority: String
)
