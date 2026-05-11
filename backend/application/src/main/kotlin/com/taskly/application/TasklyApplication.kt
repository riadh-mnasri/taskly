package com.taskly.application

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(scanBasePackages = ["com.taskly"])
@EntityScan(basePackages = ["com.taskly"])
@EnableJpaRepositories(basePackages = ["com.taskly"])
class TasklyApplication

fun main(args: Array<String>) {
    runApplication<TasklyApplication>(*args)
}
