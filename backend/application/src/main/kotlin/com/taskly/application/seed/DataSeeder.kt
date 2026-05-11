package com.taskly.application.seed

import com.taskly.identity.domain.port.inbound.RegisterUserCommand
import com.taskly.identity.domain.port.inbound.RegisterUserUseCase
import com.taskly.identity.domain.port.outbound.UserRepository
import com.taskly.identity.domain.model.Email
import com.taskly.taskmanagement.domain.port.inbound.CreateTaskCommand
import com.taskly.taskmanagement.application.service.TaskService
import com.taskly.taskmanagement.domain.model.Priority
import com.taskly.taskmanagement.domain.model.TaskType
import com.taskly.sharedkernel.domain.model.UserId
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
@Profile("local")
class DataSeeder(
    private val registerUser: RegisterUserUseCase,
    private val userRepository: UserRepository,
    private val createTask: TaskService
) : CommandLineRunner {

    private val log = LoggerFactory.getLogger(DataSeeder::class.java)

    override fun run(vararg args: String?) {
        val demoEmail = "demo@taskly.app"
        val demoPassword = "Demo1234!"

        val existingUser = userRepository.findByEmail(Email.of(demoEmail))
        if (existingUser != null) {
            log.info("Demo user already exists — skipping seed")
            return
        }

        log.info("Seeding demo data...")

        val userId = registerUser.register(
            RegisterUserCommand(email = demoEmail, rawPassword = demoPassword)
        )

        val today = LocalDate.now()
        val tasks = listOf(
            CreateTaskCommand(
                userId = userId,
                title = "Chapter 5 exercises — Algebra",
                description = "Complete exercises 5.1 to 5.8 from the textbook",
                subject = "Mathematics",
                priority = Priority.HIGH,
                type = TaskType.HOMEWORK,
                dueDate = today,
                estimatedDurationMinutes = 60
            ),
            CreateTaskCommand(
                userId = userId,
                title = "Biology exam revision",
                description = "Review chapter 3: Cell division and mitosis",
                subject = "Biology",
                priority = Priority.HIGH,
                type = TaskType.EXAM,
                dueDate = today.plusDays(1),
                estimatedDurationMinutes = 90
            ),
            CreateTaskCommand(
                userId = userId,
                title = "History essay — World War II",
                description = "Write 500-word essay on causes of WWII",
                subject = "History",
                priority = Priority.MEDIUM,
                type = TaskType.PROJECT,
                dueDate = today.plusDays(3),
                estimatedDurationMinutes = 120
            ),
            CreateTaskCommand(
                userId = userId,
                title = "French vocabulary list",
                description = "Learn 30 new words for Friday's test",
                subject = "French",
                priority = Priority.MEDIUM,
                type = TaskType.HOMEWORK,
                dueDate = today.plusDays(3),
                estimatedDurationMinutes = 30
            ),
            CreateTaskCommand(
                userId = userId,
                title = "Physics lab report",
                description = "Write up the pendulum experiment results",
                subject = "Physics",
                priority = Priority.HIGH,
                type = TaskType.HOMEWORK,
                dueDate = today.plusDays(2),
                estimatedDurationMinutes = 75
            ),
            CreateTaskCommand(
                userId = userId,
                title = "English book report — To Kill a Mockingbird",
                description = "Summarize chapters 1-10 and identify main themes",
                subject = "English",
                priority = Priority.LOW,
                type = TaskType.HOMEWORK,
                dueDate = today.plusDays(7),
                estimatedDurationMinutes = 90
            ),
            CreateTaskCommand(
                userId = userId,
                title = "Morning run",
                description = "30 minute jog around the park",
                subject = "Physical Education",
                priority = Priority.LOW,
                type = TaskType.HEALTH,
                dueDate = today.plusDays(1),
                estimatedDurationMinutes = 30
            ),
            CreateTaskCommand(
                userId = userId,
                title = "Science project — Solar System model",
                description = "Build a scale model of the solar system for the school fair",
                subject = "Science",
                priority = Priority.MEDIUM,
                type = TaskType.PROJECT,
                dueDate = today.plusDays(14),
                estimatedDurationMinutes = 180
            )
        )

        tasks.forEach { command ->
            try {
                createTask.create(command)
                log.info("Seeded task: ${command.title}")
            } catch (e: Exception) {
                log.warn("Failed to seed task '${command.title}': ${e.message}")
            }
        }

        log.info("Demo data seeding complete. Login: $demoEmail / $demoPassword")
    }
}
