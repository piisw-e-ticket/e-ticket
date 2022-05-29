package com.example.ticket.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class PeriodicTicketTests {
    @Test
    fun `validate returns ValidationChain containing error when startDate is after now`() {
        // given
        val now = LocalDateTime.now()
        val ticket = PeriodicTicket(
            now.plusSeconds(10),
            now.plusSeconds(20),
            "passenger",
            false)

        // when
        val chain = ticket.validate(-1)

        // then
        assertThat(chain.errors.size).isEqualTo(1)
        assertThat(chain.errors.first()).contains("validity", "period", "not", "started")
    }

    @Test
    fun `validate returns ValidationChain containing error when endDate is after now`() {
        // given
        val now = LocalDateTime.now()
        val ticket = PeriodicTicket(
            now.minusSeconds(20),
            now.minusSeconds(10),
            "passenger",
            false)

        // when
        val chain = ticket.validate(-1)

        // then
        assertThat(chain.errors.size).isEqualTo(1)
        assertThat(chain.errors.first()).contains("validity", "period", "exceeded")
    }

    @Test
    fun `validate returns ValidationChain with no errors when now is after startDate and before endDate`() {
        // given
        val now = LocalDateTime.now()
        val ticket = PeriodicTicket(
            now.minusSeconds(20),
            now.plusSeconds(20),
            "passenger",
            false)

        // when
        val chain = ticket.validate(-1)

        // then
        assertThat(chain.errors.size).isEqualTo(0)
    }
}