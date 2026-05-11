package com.taskly.taskmanagement.infrastructure.config

import com.taskly.taskmanagement.application.service.TaskService
import com.taskly.taskmanagement.domain.port.outbound.TaskRepository
import com.taskly.taskmanagement.infrastructure.adapter.outbound.persistence.TaskJpaRepository
import com.taskly.taskmanagement.infrastructure.adapter.outbound.persistence.TaskRepositoryAdapter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TaskManagementConfig {

    @Bean
    fun taskRepository(jpaRepository: TaskJpaRepository): TaskRepository =
        TaskRepositoryAdapter(jpaRepository)

    @Bean
    fun taskService(taskRepository: TaskRepository): TaskService =
        TaskService(taskRepository)
}
