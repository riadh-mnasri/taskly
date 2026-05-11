package com.taskly.identity.infrastructure.adapter.outbound.security

import com.taskly.identity.domain.exception.InvalidTokenException
import com.taskly.identity.domain.port.outbound.TokenClaims
import com.taskly.identity.domain.port.outbound.TokenGenerator
import com.taskly.sharedkernel.domain.model.UserId
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.Date

class JwtTokenGenerator(
    private val secret: String,
    private val accessTokenExpirySeconds: Long,
    private val refreshTokenExpirySeconds: Long
) : TokenGenerator {

    private val signingKey by lazy {
        Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))
    }

    override fun generateAccessToken(userId: UserId, email: String): String {
        val now = Instant.now()
        return Jwts.builder()
            .subject(userId.value.toString())
            .claim("email", email)
            .claim("type", "access")
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusSeconds(accessTokenExpirySeconds)))
            .signWith(signingKey)
            .compact()
    }

    override fun generateRefreshToken(userId: UserId): String {
        val now = Instant.now()
        return Jwts.builder()
            .subject(userId.value.toString())
            .claim("type", "refresh")
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusSeconds(refreshTokenExpirySeconds)))
            .signWith(signingKey)
            .compact()
    }

    override fun validateAccessToken(token: String): TokenClaims {
        val claims = parseClaims(token)
        val type = claims["type"] as? String
        if (type != "access") throw InvalidTokenException("Not an access token")
        return TokenClaims(
            userId = UserId.of(claims.subject),
            email = claims["email"] as String
        )
    }

    override fun validateRefreshToken(token: String): UserId {
        val claims = parseClaims(token)
        val type = claims["type"] as? String
        if (type != "refresh") throw InvalidTokenException("Not a refresh token")
        return UserId.of(claims.subject)
    }

    override fun accessTokenExpiresIn(): Long = accessTokenExpirySeconds

    private fun parseClaims(token: String) = try {
        Jwts.parser()
            .verifyWith(signingKey)
            .build()
            .parseSignedClaims(token)
            .payload
    } catch (e: JwtException) {
        throw InvalidTokenException("Token validation failed: ${e.message}")
    }
}
