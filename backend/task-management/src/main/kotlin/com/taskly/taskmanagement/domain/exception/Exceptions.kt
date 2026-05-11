package com.taskly.taskmanagement.domain.exception

class TaskNotFoundException(id: String) :
    RuntimeException("Task not found: $id")

class TaskAccessDeniedException(taskId: String) :
    RuntimeException("Access denied to task: $taskId")
