package com.example.ticket.controller.ticket

import com.example.ticket.client.AuthClient
import com.example.ticket.controller.TicketController
import com.example.ticket.controller.addRoleToRequestHeaders
import com.example.ticket.dto.PassengerInfoDto
import com.example.ticket.dto.ValidateTicketDto
import com.example.ticket.model.Role
import com.example.ticket.model.SingleTicket
import com.example.ticket.repository.TicketRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import java.util.*

@SpringBootTest
class ValidateTicketEndpointTests {

    @MockBean
    private lateinit var authClient: AuthClient

    @MockBean
    private lateinit var ticketRepository: TicketRepository

    @Autowired
    private lateinit var sut: TicketController

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
        val username = "passenger"
        addRoleToRequestHeaders(Role.TICKET_COLLECTOR)
        Mockito.`when`(ticketRepository.findById(1))
            .thenReturn(Optional.of(SingleTicket(username, true)))
        Mockito.`when`(authClient.getPassengerInfo(username))
            .thenReturn(PassengerInfoDto("example@example.com", false))

        // when
        val response = sut.validateTicket(ValidateTicketDto(1, 1))

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body!!.isValid).isFalse
        assertThat(response.body!!.errors.size).isEqualTo(2)
        assertThat(response.body!!.details.username).isEqualTo(username)
    }

    @Test
    fun `validateTicket returns result without errors when ticket is valid`() {
        // given
        val username = "passenger"
        val courseId = 1L
        addRoleToRequestHeaders(Role.TICKET_COLLECTOR)
        Mockito.`when`(ticketRepository.findById(1))
            .thenReturn(Optional.of(SingleTicket(username, true).apply {
                isPunched = true
                this.courseId = courseId
            }))
        Mockito.`when`(authClient.getPassengerInfo(username))
            .thenReturn(PassengerInfoDto("example@example.com", true))

        // when
        val response = sut.validateTicket(ValidateTicketDto(1, courseId))

        // then
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body!!.isValid).isTrue
        assertThat(response.body!!.errors.size).isEqualTo(0)
        assertThat(response.body!!.details.username).isEqualTo(username)
    }
}