package com.example.ticket.service

import com.example.ticket.client.AuthClient
import com.example.ticket.dto.PassengerInfoDto
import com.example.ticket.dto.PeriodicTicketCreateDto
import com.example.ticket.model.PeriodicTicket
import com.example.ticket.repository.PeriodicTicketRepository
import com.example.ticket.service.impl.PeriodicTicketServiceImpl
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*
import javax.persistence.EntityNotFoundException

class PeriodicTicketServiceImplTests {

    @MockK
    private lateinit var periodicTicketRepository: PeriodicTicketRepository

    @MockK
    private lateinit var authClient: AuthClient

    @SpyK
    @InjectMockKs
    private lateinit var periodicTicketService: PeriodicTicketServiceImpl

    @BeforeEach
    fun composeMocks() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `getTicketById returns periodic ticket on valid parameters`() {
        val expectedTicket = mockk<PeriodicTicket>()
        every { periodicTicketRepository.findById(1L) } returns Optional.of(expectedTicket)

        val actualTicket = periodicTicketService.getTicketById(1L)

        assertThat(actualTicket).isEqualTo(expectedTicket)
    }

    @Test
    fun `getTicketById throws exception on non existing id`() {
        val expectedException = EntityNotFoundException("Could not find periodic ticket with id '1'.")
        every { periodicTicketRepository.findById(1L) } returns Optional.empty()

        val actualException = assertThrows(EntityNotFoundException::class.java) {
            periodicTicketService.getTicketById(1L)
        }

        assertThat(actualException.message).isEqualTo(expectedException.message)
    }

    @Test
    fun `getTicketsByUsername returns list of single tickets of given user`() {
        val username = "validUsername"
        val ticketOne = mockk<PeriodicTicket>()
        val ticketTwo = mockk<PeriodicTicket>()
        val expectedTickets = listOf(ticketOne, ticketTwo)
        every { periodicTicketRepository.getAllByPassengerUsername(username) } returns expectedTickets

        val actualTickets = periodicTicketService.getTicketsByUsername(username)

        assertThat(actualTickets).isEqualTo(expectedTickets)
    }

    @Test
    fun `createTicket creates and returns discounted periodic ticket on valid parameters`() {
        val username = "validUsername"
        val discounted = true
        val startDate = LocalDateTime.of(2022, 6, 1, 0, 0, 0)
        val endDate = LocalDateTime.of(2022, 12, 1, 0, 0, 0)
        val createDto = PeriodicTicketCreateDto(
            startDate = startDate,
            endDate = endDate
        )
        val mockPassenger = mockk<PassengerInfoDto> {
            every { eligibleForDiscount } returns true
        }
        every { authClient.getPassengerInfo(username) } returns mockPassenger
        every { periodicTicketRepository.save(any()) } returnsArgument 0

        val actualTicket = periodicTicketService.createTicket(username, discounted, createDto)

        assertAll(
            { assertThat(actualTicket.passengerUsername).isEqualTo(username) },
            { assertThat(actualTicket.isDiscounted).isTrue() },
            { assertThat(actualTicket.startDate).isEqualTo(startDate) },
            { assertThat(actualTicket.endDate).isEqualTo(endDate) },
        )
    }

    @Test
    fun `createTicket creates and returns non-discounted periodic ticket on valid parameters`() {
        val username = "validUsername"
        val discounted = true
        val startDate = LocalDateTime.of(2022, 6, 1, 0, 0, 0)
        val endDate = LocalDateTime.of(2022, 12, 1, 0, 0, 0)
        val createDto = PeriodicTicketCreateDto(
            startDate = startDate,
            endDate = endDate
        )
        val mockPassenger = mockk<PassengerInfoDto> {
            every { eligibleForDiscount } returns true
        }
        every { authClient.getPassengerInfo(username) } returns mockPassenger
        every { periodicTicketRepository.save(any()) } returnsArgument 0

        val actualTicket = periodicTicketService.createTicket(username, discounted, createDto)

        assertAll(
            { assertThat(actualTicket.passengerUsername).isEqualTo(username) },
            { assertThat(actualTicket.isDiscounted).isTrue() },
            { assertThat(actualTicket.startDate).isEqualTo(startDate) },
            { assertThat(actualTicket.endDate).isEqualTo(endDate) },
        )
    }

    @Test
    fun `createTicket throws error when passenger not eligible for discount tries to buy discounted periodic ticket`() {
        val username = "validUsername"
        val discounted = true
        val mockPassenger = mockk<PassengerInfoDto>() {
            every { eligibleForDiscount } returns false
        }
        val expectedException = IllegalArgumentException("Passenger '$username' is not eligible for a discounted ticket.")
        every { authClient.getPassengerInfo(username) } returns mockPassenger

        val actualException = assertThrows(IllegalArgumentException::class.java){
            periodicTicketService.createTicket(username, discounted, mockk())
        }

        assertThat(actualException.message).isEqualTo(expectedException.message)
    }

    @Test
    fun `createTicket throws error when end date is before start date`() {
        val username = "validUsername"
        val discounted = true
        val endDate = LocalDateTime.of(2022, 6, 1, 0, 0, 0)
        val startDate = LocalDateTime.of(2022, 12, 1, 0, 0, 0)
        val createDto = PeriodicTicketCreateDto(
            startDate = startDate,
            endDate = endDate
        )
        val mockPassenger = mockk<PassengerInfoDto>() {
            every { eligibleForDiscount } returns true
        }
        val expectedException = IllegalArgumentException("Ticket start date '${createDto.startDate}' " +
                "cannot be before end date '${createDto.endDate}'")
        every { authClient.getPassengerInfo(username) } returns mockPassenger

        val actualException = assertThrows(IllegalArgumentException::class.java){
            periodicTicketService.createTicket(username, discounted, createDto)
        }

        assertThat(actualException.message).isEqualTo(expectedException.message)
    }

}