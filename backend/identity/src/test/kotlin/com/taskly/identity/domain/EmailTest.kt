package com.taskly.identity.domain

import com.taskly.identity.domain.model.Email
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class EmailTest {

    @Test
    fun `valid email is accepted and normalized to lowercase`() {
        val email = Email.of("Alice@Example.COM")
        assertThat(email.value).isEqualTo("alice@example.com")
    }

    @Test
    fun `blank email is rejected`() {
        assertThatThrownBy { Email.of("   ") }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("blank")
    }

    @Test
    fun `email without at sign is rejected`() {
        assertThatThrownBy { Email.of("invalidemail.com") }
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `email without domain is rejected`() {
        assertThatThrownBy { Email.of("user@") }
            .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `email exceeding 254 characters is rejected`() {
        val longEmail = "a".repeat(250) + "@b.com"
        assertThatThrownBy { Email.of(longEmail) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("254")
    }

    @Test
    fun `two emails with same value are equal`() {
        val email1 = Email.of("user@example.com")
        val email2 = Email.of("USER@EXAMPLE.COM")
        assertThat(email1).isEqualTo(email2)
    }
}
