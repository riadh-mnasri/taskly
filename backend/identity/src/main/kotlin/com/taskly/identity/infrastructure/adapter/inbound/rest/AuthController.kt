package com.taskly.identity.infrastructure.adapter.inbound.rest

import com.taskly.identity.domain.port.inbound.AuthenticateUserCommand
import com.taskly.identity.domain.port.inbound.AuthenticateUserUseCase
import com.taskly.identity.domain.port.inbound.GetCurrentUserUseCase
import com.taskly.identity.domain.port.inbound.RefreshTokenCommand
import com.taskly.identity.domain.port.inbound.RefreshTokenUseCase
import com.taskly.identity.domain.port.inbound.RegisterUserCommand
import com.taskly.identity.domain.port.inbound.RegisterUserUseCase
import com.taskly.sharedkernel.domain.model.UserId
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
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
    private val refreshToken: RefreshTokenUseCase,
    private val getCurrentUser: GetCurrentUserUseCase,
    @Value("\${taskly.jwt.refresh-token-expiry-seconds:604800}") private val refreshTokenExpirySeconds: Long
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
    @Operation(summary = "Authenticate and set session cookies")
    fun signIn(@Valid @RequestBody request: SignInRequest, response: HttpServletResponse): ExpiryResponse {
        val tokens = authenticateUser.authenticate(
            AuthenticateUserCommand(email = request.email, rawPassword = request.password)
        )
        setSessionCookies(response, tokens.accessToken, tokens.refreshToken, tokens.expiresIn)
        return ExpiryResponse(expiresIn = tokens.expiresIn)
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh the session using the refresh token cookie")
    fun refresh(
        @CookieValue("refresh_token") refreshTokenCookie: String,
        response: HttpServletResponse
    ): ExpiryResponse {
        val tokens = refreshToken.refresh(RefreshTokenCommand(refreshToken = refreshTokenCookie))
        setSessionCookies(response, tokens.accessToken, tokens.refreshToken, tokens.expiresIn)
        return ExpiryResponse(expiresIn = tokens.expiresIn)
    }

    @PostMapping("/sign-out")
    @Operation(summary = "Clear session cookies")
    fun signOut(response: HttpServletResponse) {
        clearCookie(response, "access_token")
        clearCookie(response, "refresh_token")
    }

    @GetMapping("/me")
    @Operation(summary = "Get the currently authenticated user")
    fun me(authentication: Authentication): CurrentUserResponse {
        val view = getCurrentUser.getCurrentUser(UserId.of(authentication.principal as String))
        return CurrentUserResponse(email = view.email)
    }

    private fun setSessionCookies(response: HttpServletResponse, accessToken: String, refreshToken: String, accessTokenExpiresIn: Long) {
        response.addHeader(HttpHeaders.SET_COOKIE, sessionCookie("access_token", accessToken, accessTokenExpiresIn).toString())
        response.addHeader(HttpHeaders.SET_COOKIE, sessionCookie("refresh_token", refreshToken, refreshTokenExpirySeconds).toString())
    }

    private fun sessionCookie(name: String, value: String, maxAgeSeconds: Long): ResponseCookie =
        ResponseCookie.from(name, value)
            .httpOnly(true)
            .secure(true)
            .sameSite("Lax")
            .path("/")
            .maxAge(maxAgeSeconds)
            .build()

    private fun clearCookie(response: HttpServletResponse, name: String) {
        response.addHeader(HttpHeaders.SET_COOKIE, sessionCookie(name, "", 0).toString())
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

data class SignUpResponse(val userId: String, val email: String)

data class ExpiryResponse(val expiresIn: Long)

data class CurrentUserResponse(val email: String)
