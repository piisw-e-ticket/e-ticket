package com.example.ticket.controller.ticket

import com.example.ticket.controller.TicketController
import com.example.ticket.controller.addRoleToRequestHeaders
import com.example.ticket.dto.ValidateTicketDto
import com.example.ticket.model.*
import com.example.ticket.service.PeriodicTicketService
import com.example.ticket.service.SingleTicketService
import com.example.ticket.service.TicketValidationService
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus
import java.time.LocalDateTime

@SpringBootTest
class TicketControllerTests {

    @TestConfiguration
    class TicketControllerTestsConfig {
        @Bean
        fun singleTicketService(): SingleTicketService = mockk()

        @Bean
        fun periodicTicketService(): PeriodicTicketService = mockk()

        @Bean
        fun ticketValidationService(): TicketValidationService = mockk()
    }

    @Autowired
    private lateinit var sut: TicketController

    @Autowired
    private lateinit var singleTicketService: SingleTicketService

    @Autowired
    private lateinit var periodicTicketService: PeriodicTicketService

    @Autowired
    private lateinit var ticketValidationService: TicketValidationService

    @Test
    fun `getAllUserTickets cannot be accessed with ticket collector role`() {
        // given
        addRoleToRequestHeaders(Role.TICKET_COLLECTOR)

        // when
        val response = sut.getAllUserTickets("username")

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.FORBIDDEN)
    }

    @Test
    fun `getAllUserTickets cannot be accessed with admin role`() {
        // given
        addRoleToRequestHeaders(Role.ADMIN)

        // when
        val response = sut.getAllUserTickets("username")

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.FORBIDDEN)
    }

    @Test
    fun `getAllUserTickets returns list of tickets of given user`() {
        // given
        addRoleToRequestHeaders(Role.PASSENGER)
        val username = "passenger"
        val mockSingleTicket = mockk<SingleTicket> {
            every { id } returns 1
            every { passengerUsername } returns username
            every { isDiscounted } returns true
            every { isPunched } returns true
            every { courseId } returns 2
        }
        val ticketStartDate = LocalDateTime.of(2022, 6, 1, 0, 0, 0)
        val ticketEndDate = LocalDateTime.of(2022, 12, 1, 0, 0, 0)
        val mockPeriodicTicket = mockk<PeriodicTicket> {
            every { id } returns 1
            every { passengerUsername } returns username
            every { isDiscounted } returns false
            every { startDate } returns ticketStartDate
            every { endDate } returns ticketEndDate
        }
        every { singleTicketService.getTicketsByUsername(username) } returns listOf(mockSingleTicket)
        every { periodicTicketService.getTicketsByUsername(username) } returns listOf(mockPeriodicTicket)

        // when
        val response = sut.getAllUserTickets(username)

        // then
        val responseBody = response.body!!
        assertAll(
            { assertThat(response.statusCode).isEqualTo(HttpStatus.OK) },
            { assertThat(responseBody.singleTickets).hasSize(1) },
            { assertThat(responseBody.periodicTickets).hasSize(1) },
            { assertThat(responseBody.singleTickets[0].id).isEqualTo(1) },
            { assertThat(responseBody.singleTickets[0].passengerUsername).isEqualTo(username) },
            { assertThat(responseBody.singleTickets[0].isDiscounted).isTrue() },
            { assertThat(responseBody.singleTickets[0].isPunched).isTrue() },
            { assertThat(responseBody.singleTickets[0].courseId).isEqualTo(2) },
            { assertThat(responseBody.periodicTickets[0].id).isEqualTo(1) },
            { assertThat(responseBody.periodicTickets[0].passengerUsername).isEqualTo(username) },
            { assertThat(responseBody.periodicTickets[0].isDiscounted).isFalse() },
            { assertThat(responseBody.periodicTickets[0].startDate).isEqualTo(ticketStartDate) },
            { assertThat(responseBody.periodicTickets[0].endDate).isEqualTo(ticketEndDate) },
        )
    }

    @Test
    fun `buySingleTicket cannot be accessed with ticket collector role`() {
        // given
        addRoleToRequestHeaders(Role.TICKET_COLLECTOR)

        // when
        val response = sut.buySingleTicket("username", true)

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.FORBIDDEN)
    }

    @Test
    fun `buySingleTicket cannot be accessed with admin role`() {
        // given
        addRoleToRequestHeaders(Role.ADMIN)

        // when
        val response = sut.buySingleTicket("username", true)

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.FORBIDDEN)
    }

    @Test
    fun `buySingleTicket returns single ticket`() {
        // given
        addRoleToRequestHeaders(Role.PASSENGER)
        val username = "passenger"
        val mockSingleTicket = mockk<SingleTicket> {
            every { id } returns 1
            every { passengerUsername } returns username
            every { isDiscounted } returns true
            every { isPunched } returns false
            every { courseId } returns null
        }
        every { singleTicketService.createTicket(username, true) } returns mockSingleTicket

        // when
        val response = sut.buySingleTicket(username, true)

        // then
        val responseBody = response.body!!
        assertAll(
            { assertThat(response.statusCode).isEqualTo(HttpStatus.OK) },
            { assertThat(responseBody.id).isEqualTo(1) },
            { assertThat(responseBody.passengerUsername).isEqualTo(username) },
            { assertThat(responseBody.isDiscounted).isTrue() },
            { assertThat(responseBody.isPunched).isFalse() },
            { assertThat(responseBody.courseId).isNull() },
        )
    }

    @Test
    fun `buyPeriodicTicket cannot be accessed with ticket collector role`() {
        // given
        addRoleToRequestHeaders(Role.TICKET_COLLECTOR)

        // when
        val response = sut.buyPeriodicTicket("username", true, mockk())

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.FORBIDDEN)
    }

    @Test
    fun `buyPeriodicTicket cannot be accessed with admin role`() {
        // given
        addRoleToRequestHeaders(Role.ADMIN)

        // when
        val response = sut.buyPeriodicTicket("username", true, mockk())

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.FORBIDDEN)
    }

    @Test
    fun `buyPeriodicTicket returns periodic ticket`() {
        // given
        addRoleToRequestHeaders(Role.PASSENGER)
        val username = "passenger"
        val ticketStartDate = LocalDateTime.of(2022, 6, 1, 0, 0, 0)
        val ticketEndDate = LocalDateTime.of(2022, 12, 1, 0, 0, 0)
        val mockPeriodicTicket = mockk<PeriodicTicket> {
            every { id } returns 1
            every { passengerUsername } returns username
            every { isDiscounted } returns false
            every { startDate } returns ticketStartDate
            every { endDate } returns ticketEndDate
        }
        every { periodicTicketService.createTicket(username, false, any()) } returns mockPeriodicTicket

        // when
        val response = sut.buyPeriodicTicket(username, false, mockk())

        // then
        val responseBody = response.body!!
        assertAll(
            { assertThat(response.statusCode).isEqualTo(HttpStatus.OK) },
            { assertThat(responseBody.id).isEqualTo(1) },
            { assertThat(responseBody.passengerUsername).isEqualTo(username) },
            { assertThat(responseBody.isDiscounted).isFalse() },
            { assertThat(responseBody.startDate).isEqualTo(ticketStartDate) },
            { assertThat(responseBody.endDate).isEqualTo(ticketEndDate) },
        )
    }

    @Test
    fun `validateTicket cannot be invoked with forbidden role`() {
        // given
        addRoleToRequestHeaders(Role.PASSENGER)

        // when
        val response = sut.validateTicket(ValidateTicketDto(1, 1))

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.FORBIDDEN)
    }

    @Test
    fun `validateTicket returns result with errors when ticket is not valid`() {
        // given
        val passengerUsername = "passenger"
        addRoleToRequestHeaders(Role.TICKET_COLLECTOR)
        val mockTicket = mockk<SingleTicket> {
            every { isDiscounted } returns true
            every { courseId } returns 2
        }
        val mockOwner = mockk<TicketOwner> {
            every { username } returns passengerUsername
            every { email } returns "mail@mail.com"
            every { isEligibleForDiscount } returns false
        }
        val errorValidationResult = TicketValidationResult(
            ticket = mockTicket,
            owner = mockOwner,
            isSuccess = false,
            errors = listOf("errorOne", "errorTwo")
        )
        every { ticketValidationService.validateTicket(1, 1) } returns errorValidationResult

        // when
        val response = sut.validateTicket(ValidateTicketDto(1, 1))

        // then
        assertAll(
            { assertThat(response.statusCode).isEqualTo(HttpStatus.OK) },
            { assertThat(response.body!!.isValid).isFalse },
            { assertThat(response.body!!.errors.size).isEqualTo(2) },
            { assertThat(response.body!!.details.username).isEqualTo(passengerUsername) }
        )
    }

    @Test
    fun `validateTicket returns result without errors when ticket is valid`() {
        // given
        addRoleToRequestHeaders(Role.TICKET_COLLECTOR)
        val mockTicket = mockk<SingleTicket> {
            every { isDiscounted } returns true
            every { courseId } returns 1
        }
        val mockOwner = mockk<TicketOwner> {
            every { username } returns "passenger"
            every { email } returns "mail@mail.com"
            every { isEligibleForDiscount } returns true
        }
        val successValidationResult = TicketValidationResult(
            ticket = mockTicket,
            owner = mockOwner,
            isSuccess = true,
            errors = listOf()
        )
        every { ticketValidationService.validateTicket(1, 1) } returns successValidationResult

        // when
        val response = sut.validateTicket(ValidateTicketDto(1, 1))

        // then
        assertAll(
            { assertThat(response.statusCode).isEqualTo(HttpStatus.OK) },
            { assertThat(response.body!!.isValid).isTrue },
            { assertThat(response.body!!.errors.size).isEqualTo(0) },
            { assertThat(response.body!!.details.username).isEqualTo("passenger") }
        )
    }
}