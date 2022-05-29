package com.example.ticket.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class SingleTicketTests {
    @Test
    fun `validate returns ValidationChain containing error when ticket is not punched`() {
        // given
        val courseId: Long = 10
        val ticket = SingleTicket(
            "passenger",
            false)

        // when
        val chain = ticket.validate(courseId)

        // then
        assertThat(chain.errors.size).isEqualTo(1)
        assertThat(chain.errors.first()).contains("not", "punched")
    }

    @Test
    fun `validate returns ValidationChain containing error when ticket is assigned to wrong course`() {
        // given
        val courseId: Long = 10
        val ticket = SingleTicket(
            "passenger",
            false)
        ticket.courseId = courseId
        ticket.isPunched = true

        // when
        val chain = ticket.validate(courseId + 1)

        // then
        assertThat(chain.errors.size).isEqualTo(1)
        assertThat(chain.errors.first()).contains("punched", "different", "course", courseId.toString(), (courseId + 1).toString())
    }

    @Test
    fun `validate returns ValidationChain with no errors when ticket is assigned to correct course`() {
        // given
        val courseId: Long = 10
        val ticket = SingleTicket(
            "passenger",
            false)
        ticket.courseId = courseId

        // when
        val chain = ticket.validate(courseId)

        // then
        assertThat(chain.errors.size).isEqualTo(1)
    }
}