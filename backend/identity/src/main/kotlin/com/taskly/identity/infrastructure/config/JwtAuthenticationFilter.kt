package com.taskly.identity.infrastructure.config

import com.taskly.identity.domain.exception.InvalidTokenException
import com.taskly.identity.domain.port.outbound.TokenGenerator
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.filter.OncePerRequestFilter

class JwtAuthenticationFilter(
    private val tokenGenerator: TokenGenerator
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = extractAccessToken(request)

        if (token != null && SecurityContextHolder.getContext().authentication == null) {
            try {
                val claims = tokenGenerator.validateAccessToken(token)
                val auth = UsernamePasswordAuthenticationToken(
                    claims.userId.toString(),
                    null,
                    listOf(SimpleGrantedAuthority("ROLE_CHILD"))
                )
                auth.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = auth
            } catch (e: InvalidTokenException) {
                // Invalid token — continue without authentication; security rules will reject if needed
            }
        }

        filterChain.doFilter(request, response)
    }

    private fun extractAccessToken(request: HttpServletRequest): String? {
        val cookieToken = request.cookies?.firstOrNull { it.name == "access_token" }?.value
        if (!cookieToken.isNullOrBlank()) return cookieToken

        val header = request.getHeader("Authorization") ?: return null
        if (!header.startsWith("Bearer ")) return null
        return header.substring(7).takeIf { it.isNotBlank() }
    }
}
