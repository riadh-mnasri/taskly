package com.taskly.identity.infrastructure.adapter.inbound.rest

import com.taskly.identity.domain.port.inbound.AuthenticateUserCommand
import com.taskly.identity.domain.port.inbound.AuthenticateUserUseCase
import com.taskly.identity.domain.port.inbound.RefreshTokenCommand
import com.taskly.identity.domain.port.inbound.RefreshTokenUseCase
import com.taskly.identity.domain.port.inbound.RegisterUserCommand
import com.taskly.identity.domain.port.inbound.RegisterUserUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "User registration and authentication")
class AuthController(
    private val registerUser: RegisterUserUseCase,
    private val authenticateUser: AuthenticateUserUseCase,
    private val refreshToken: RefreshTokenUseCase
) {

    @PostMapping("/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new user")
    fun signUp(@Valid @RequestBody request: SignUpRequest): SignUpResponse {
        val userId = registerUser.register(
            RegisterUserCommand(email = request.email, rawPassword = request.password)
        )
        return SignUpResponse(userId = userId.toString(), email = request.email.lowercase().trim())
    }

    @PostMapping("/sign-in")
    @Operation(summary = "Authenticate and receive JWT tokens")
    fun signIn(@Valid @RequestBody request: SignInRequest): TokenResponse {
        val tokens = authenticateUser.authenticate(
            AuthenticateUserCommand(email = request.email, rawPassword = request.password)
        )
        return TokenResponse(
            accessToken = tokens.accessToken,
            refreshToken = tokens.refreshToken,
            expiresIn = tokens.expiresIn
        )
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token using a refresh token")
    fun refresh(@Valid @RequestBody request: RefreshRequest): TokenResponse {
        val tokens = refreshToken.refresh(RefreshTokenCommand(refreshToken = request.refreshToken))
        return TokenResponse(
            accessToken = tokens.accessToken,
            refreshToken = tokens.refreshToken,
            expiresIn = tokens.expiresIn
        )
    }
}

data class SignUpRequest(
    @field:Email(message = "Must be a valid email address")
    @field:NotBlank(message = "Email is required")
    val email: String,

    @field:NotBlank(message = "Password is required")
    val password: String
)

data class SignInRequest(
    @field:NotBlank(message = "Email is required")
    val email: String,

    @field:NotBlank(message = "Password is required")
    val password: String
)

data class RefreshRequest(
    @field:NotBlank(message = "Refresh token is required")
    val refreshToken: String
)

data class SignUpResponse(val userId: String, val email: String)

data class TokenResponse(val accessToken: String, val refreshToken: String, val expiresIn: Long)
