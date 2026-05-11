package com.taskly.identity.domain.model

@ConsistentCopyVisibility
data class Email private constructor(val value: String) {

    companion object {
        private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}$")

        fun of(raw: String): Email {
            val trimmed = raw.trim().lowercase()
            require(trimmed.isNotBlank()) { "Email must not be blank" }
            require(trimmed.length <= 254) { "Email must not exceed 254 characters" }
            require(EMAIL_REGEX.matches(trimmed)) { "Email '$raw' is not a valid email address" }
            return Email(trimmed)
        }
    }

    override fun toString(): String = value
}
