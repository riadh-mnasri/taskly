package com.taskly.taskmanagement.domain

import com.taskly.taskmanagement.domain.model.Deadline
import com.taskly.taskmanagement.domain.model.EstimatedDuration
import com.taskly.taskmanagement.domain.model.Subject
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.time.LocalDate

class TaskValueObjectsTest {

    @Test
    fun `Subject trims whitespace and accepts valid input`() {
        val subject = Subject.of("  Mathematics  ")
        assertThat(subject.value).isEqualTo("Mathematics")
    }

    @Test
    fun `Subject rejects blank value`() {
        assertThatThrownBy { Subject.of("   ") }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("blank")
    }

    @Test
    fun `Subject rejects value exceeding 100 characters`() {
        assertThatThrownBy { Subject.of("a".repeat(101)) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("100")
    }

    @Test
    fun `Deadline accepts today`() {
        val today = LocalDate.now()
        val deadline = Deadline.of(today, today)
        assertThat(deadline.value).isEqualTo(today)
    }

    @Test
    fun `Deadline accepts future date`() {
        val future = LocalDate.now().plusDays(5)
        val deadline = Deadline.of(future)
        assertThat(deadline.value).isEqualTo(future)
    }

    @Test
    fun `Deadline rejects past date`() {
        val yesterday = LocalDate.now().minusDays(1)
        assertThatThrownBy { Deadline.of(yesterday) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("future")
    }

    @Test
    fun `EstimatedDuration accepts valid minutes`() {
        val duration = EstimatedDuration.of(45)
        assertThat(duration.minutes).isEqualTo(45)
    }

    @Test
    fun `EstimatedDuration rejects zero minutes`() {
        assertThatThrownBy { EstimatedDuration.of(0) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("1")
    }

    @Test
    fun `EstimatedDuration rejects more than 480 minutes`() {
        assertThatThrownBy { EstimatedDuration.of(481) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("480")
    }
}
