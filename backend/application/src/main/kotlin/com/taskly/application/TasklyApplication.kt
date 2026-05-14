package com.taskly.application

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication(scanBasePackages = ["com.taskly"])
@EntityScan(basePackages = ["com.taskly"])
@EnableJpaRepositories(basePackages = ["com.taskly"])
@EnableScheduling
class TasklyApplication

fun main(args: Array<String>) {
    runApplication<TasklyApplication>(*args)
}
