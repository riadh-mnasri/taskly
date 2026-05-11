package com.taskly.application.config

import com.taskly.identity.domain.exception.EmailAlreadyUsedException
import com.taskly.identity.domain.exception.InvalidCredentialsException
import com.taskly.identity.domain.exception.InvalidTokenException
import com.taskly.identity.domain.exception.UserNotFoundException
import com.taskly.taskmanagement.domain.exception.TaskAccessDeniedException
import com.taskly.taskmanagement.domain.exception.TaskNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.net.URI

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ProblemDetail {
        val details = ex.bindingResult.fieldErrors
            .joinToString("; ") { "${it.field}: ${it.defaultMessage}" }
        return problemDetail(
            status = HttpStatus.BAD_REQUEST,
            title = "Validation Error",
            detail = details,
            type = "validation-error"
        )
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException): ProblemDetail =
        problemDetail(HttpStatus.BAD_REQUEST, "Bad Request", ex.message ?: "Invalid input", "bad-request")

    @ExceptionHandler(EmailAlreadyUsedException::class)
    fun handleEmailAlreadyUsed(ex: EmailAlreadyUsedException): ProblemDetail =
        problemDetail(HttpStatus.CONFLICT, "Email Already Used", ex.message ?: "Email already registered", "email-already-used")

    @ExceptionHandler(InvalidCredentialsException::class)
    fun handleInvalidCredentials(ex: InvalidCredentialsException): ProblemDetail =
        problemDetail(HttpStatus.UNAUTHORIZED, "Invalid Credentials", "Invalid email or password", "invalid-credentials")

    @ExceptionHandler(InvalidTokenException::class)
    fun handleInvalidToken(ex: InvalidTokenException): ProblemDetail =
        problemDetail(HttpStatus.UNAUTHORIZED, "Invalid Token", ex.message ?: "Token is invalid or expired", "invalid-token")

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFound(ex: UserNotFoundException): ProblemDetail =
        problemDetail(HttpStatus.NOT_FOUND, "User Not Found", ex.message ?: "User not found", "user-not-found")

    @ExceptionHandler(TaskNotFoundException::class)
    fun handleTaskNotFound(ex: TaskNotFoundException): ProblemDetail =
        problemDetail(HttpStatus.NOT_FOUND, "Task Not Found", ex.message ?: "Task not found", "task-not-found")

    @ExceptionHandler(TaskAccessDeniedException::class)
    fun handleTaskAccessDenied(ex: TaskAccessDeniedException): ProblemDetail =
        problemDetail(HttpStatus.FORBIDDEN, "Access Denied", ex.message ?: "Access denied", "access-denied")

    private fun problemDetail(
        status: HttpStatus,
        title: String,
        detail: String,
        type: String
    ): ProblemDetail {
        val pd = ProblemDetail.forStatusAndDetail(status, detail)
        pd.title = title
        pd.type = URI.create("https://taskly.app/errors/$type")
        return pd
    }
}
