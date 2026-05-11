package com.taskly.identity.domain

import com.taskly.identity.domain.model.RawPassword
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class PasswordTest {

    @Test
    fun `valid password is accepted`() {
        val password = RawPassword.of("SecurePass1!")
        assertThat(password.value).isEqualTo("SecurePass1!")
    }

    @Test
    fun `blank password is rejected`() {
        assertThatThrownBy { RawPassword.of("") }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("blank")
    }

    @Test
    fun `password shorter than 8 characters is rejected`() {
        assertThatThrownBy { RawPassword.of("Ab1!") }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("8")
    }

    @Test
    fun `password without uppercase is rejected`() {
        assertThatThrownBy { RawPassword.of("lowercase1!") }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("uppercase")
    }

    @Test
    fun `password without lowercase is rejected`() {
        assertThatThrownBy { RawPassword.of("UPPERCASE1!") }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("lowercase")
    }

    @Test
    fun `password without digit is rejected`() {
        assertThatThrownBy { RawPassword.of("NoDigitsHere!") }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("digit")
    }

    @Test
    fun `password at exactly 8 characters is accepted`() {
        val password = RawPassword.of("Passw0rd")
        assertThat(password.value).isEqualTo("Passw0rd")
    }
}
