package com.example.ticket.service

import com.example.ticket.client.AuthClient
import com.example.ticket.dto.PassengerInfoDto
import com.example.ticket.dto.PunchTicketDto
import com.example.ticket.model.SingleTicket
import com.example.ticket.repository.SingleTicketRepository
import com.example.ticket.service.impl.SingleTicketServiceImpl
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.mockk
import io.mockk.spyk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import javax.persistence.EntityNotFoundException

class SingleTicketServiceImplTests {

    @MockK
    private lateinit var singleTicketRepository: SingleTicketRepository

    @MockK
    private lateinit var authClient: AuthClient

    @SpyK
    @InjectMockKs
    private lateinit var singleTicketService: SingleTicketServiceImpl

    @BeforeEach
    fun composeMocks() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `getTicketById returns single ticket on valid parameters`() {
        val expectedTicket = mockk<SingleTicket>()
        every { singleTicketRepository.findById(1L) } returns Optional.of(expectedTicket)

        val actualTicket = singleTicketService.getTicketById(1L)

        assertThat(actualTicket).isEqualTo(expectedTicket)
    }

    @Test
    fun `getTicketById throws exception on non existing id`() {
        val expectedException = EntityNotFoundException("Could not find single ticket with id '1'.")
        every { singleTicketRepository.findById(1L) } returns Optional.empty()

        val actualException = assertThrows(EntityNotFoundException::class.java) {
            singleTicketService.getTicketById(1L)
        }

        assertThat(actualException.message).isEqualTo(expectedException.message)
    }

    @Test
    fun `getTicketsByUsername returns list of single tickets of given user`() {
        val username = "validUsername"
        val ticketOne = mockk<SingleTicket>()
        val ticketTwo = mockk<SingleTicket>()
        val expectedTickets = listOf(ticketOne, ticketTwo)
        every { singleTicketRepository.getAllByPassengerUsername(username) } returns expectedTickets

        val actualTickets = singleTicketService.getTicketsByUsername(username)

        assertThat(actualTickets).isEqualTo(expectedTickets)
    }

    @Test
    fun `createTicket creates and returns discounted single ticket on valid parameters`() {
        val username = "validUsername"
        val discounted = true
        val mockPassenger = mockk<PassengerInfoDto> {
            every { eligibleForDiscount } returns true
        }
        every { authClient.getPassengerInfo(username) } returns mockPassenger
        every { singleTicketRepository.save(any()) } returnsArgument 0

        val actualTicket = singleTicketService.createTicket(username, discounted)

        assertAll(
            { assertThat(actualTicket.passengerUsername).isEqualTo(username) },
            { assertThat(actualTicket.isDiscounted).isTrue() },
            { assertThat(actualTicket.isPunched).isFalse() },
            { assertThat(actualTicket.courseId).isNull() },
        )
    }

    @Test
    fun `createTicket creates and returns non-discounted single ticket on valid parameters`() {
        val username = "validUsername"
        val discounted = false
        val mockPassenger = mockk<PassengerInfoDto>()
        every { authClient.getPassengerInfo(username) } returns mockPassenger
        every { singleTicketRepository.save(any()) } returnsArgument 0

        val actualTicket = singleTicketService.createTicket(username, discounted)

        assertAll(
            { assertThat(actualTicket.passengerUsername).isEqualTo(username) },
            { assertThat(actualTicket.isDiscounted).isFalse() },
            { assertThat(actualTicket.isPunched).isFalse() },
            { assertThat(actualTicket.courseId).isNull() },
        )
    }

    @Test
    fun `createTicket throws error when passenger not eligible for discount tries to buy discounted ticket`() {
        val username = "validUsername"
        val discounted = true
        val mockPassenger = mockk<PassengerInfoDto>() {
            every { eligibleForDiscount } returns false
        }
        val expectedException = IllegalArgumentException("Passenger '$username' is not eligible for a discounted ticket.")
        every { authClient.getPassengerInfo(username) } returns mockPassenger

        val actualException = assertThrows(IllegalArgumentException::class.java){
            singleTicketService.createTicket(username, discounted)
        }

        assertThat(actualException.message).isEqualTo(expectedException.message)
    }

    @Test
    fun `punchTicket returns punched ticket on valid parameters`() {
        val ticketId = 1L
        val punchTicketDto = PunchTicketDto(courseId = 1L)
        val mockTicket = spyk<SingleTicket>()
        every { singleTicketService.getTicketById(ticketId) } returns mockTicket
        every { singleTicketRepository.save(any()) } returnsArgument 0

        val actualTicket = singleTicketService.punchTicket(ticketId, punchTicketDto)

        assertAll(
            { assertThat(actualTicket.isPunched).isTrue() },
            { assertThat(actualTicket.courseId).isEqualTo(punchTicketDto.courseId) }
        )
    }

    @Test
    fun `punchTicket throws exception on already punched ticket`() {
        val ticketId = 1L
        val punchTicketDto = PunchTicketDto(courseId = 1L)
        val mockTicket = mockk<SingleTicket> {
            every { id } returns ticketId
            every { isPunched } returns true
        }
        val expectedException = IllegalArgumentException("Ticket with id '$ticketId' is already punched.")
        every { singleTicketService.getTicketById(ticketId) } returns mockTicket

        val actualException = assertThrows(IllegalArgumentException::class.java){
            singleTicketService.punchTicket(ticketId, punchTicketDto)
        }

        assertThat(actualException.message).isEqualTo(expectedException.message)
    }

}