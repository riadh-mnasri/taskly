package com.taskly.identity.domain.model

@ConsistentCopyVisibility
data class RawPassword private constructor(val value: String) {

    companion object {
        private const val MIN_LENGTH = 8
        private const val MAX_LENGTH = 72 // bcrypt limit

        fun of(raw: String): RawPassword {
            require(raw.isNotBlank()) { "Password must not be blank" }
            require(raw.length >= MIN_LENGTH) { "Password must be at least $MIN_LENGTH characters" }
            require(raw.length <= MAX_LENGTH) { "Password must not exceed $MAX_LENGTH characters" }
            require(raw.any { it.isUpperCase() }) { "Password must contain at least one uppercase letter" }
            require(raw.any { it.isLowerCase() }) { "Password must contain at least one lowercase letter" }
            require(raw.any { it.isDigit() }) { "Password must contain at least one digit" }
            return RawPassword(raw)
        }
    }
}

data class HashedPassword(val value: String) {
    companion object {
        fun of(hashed: String): HashedPassword = HashedPassword(hashed)
    }
}
