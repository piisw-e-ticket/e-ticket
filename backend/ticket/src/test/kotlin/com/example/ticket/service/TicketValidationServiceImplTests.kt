package com.example.ticket.service

import com.example.ticket.client.AuthClient
import com.example.ticket.dto.PassengerInfoDto
import com.example.ticket.model.Ticket
import com.example.ticket.model.ValidationChain
import com.example.ticket.model.ensure
import com.example.ticket.repository.TicketRepository
import com.example.ticket.service.impl.TicketValidationServiceImpl
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TicketValidationServiceImplTests {

    @MockK
    private lateinit var authClient: AuthClient

    @MockK
    private lateinit var ticketRepository: TicketRepository

    @BeforeEach
    fun composeMocks() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `validateTicket returns result without error when valid ticket is not discounted`() {
        // given
        val username = "passenger"
        every { authClient.getPassengerInfo(any()) }.returns(
            PassengerInfoDto("mail@example.com", false))
        every { ticketRepository.getById(any()) }.returns(mockk {
            every { isDiscounted }.returns(false)
            every { passengerUsername } returns(username)
            every { validate(-1) }.returns(ValidationChain.begin(this, breakOnError = true))
        })

        // when
        val result = TicketValidationServiceImpl(authClient, ticketRepository).validateTicket(-1, -1)

        // then
        assertThat(result.isSuccess).isTrue
    }

    @Test
    fun `validateTicket ensures owner is eligible for discounted ticket regardless of ticket validity`() {
        // given
        val username = "passenger"
        every { authClient.getPassengerInfo(any()) }.returns(
            PassengerInfoDto("mail@example.com", false))
        every { ticketRepository.getById(any()) }.returns(mockk {
            every { isDiscounted }.returns(true)
            every { passengerUsername } returns(username)
            every { validate(-1) }.returns(ValidationChain
                .begin(this, breakOnError = true)
                .link(ensure({false}, "Error")))
        })

        // when
        val result = TicketValidationServiceImpl(authClient, ticketRepository).validateTicket(-1, -1)

        // then
        assertThat(result.isSuccess).isFalse
        assertThat(result.errors.size).isEqualTo(2)
        assertThat(result.errors.last()).contains(username, "not", "eligible", "discounted")
    }
}