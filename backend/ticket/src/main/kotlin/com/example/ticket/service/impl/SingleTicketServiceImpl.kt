package com.example.ticket.service.impl

import com.example.ticket.client.AuthClient
import com.example.ticket.dto.PunchTicketDto
import com.example.ticket.model.SingleTicket
import com.example.ticket.repository.SingleTicketRepository
import com.example.ticket.service.SingleTicketService
import org.springframework.stereotype.Service
import javax.persistence.EntityNotFoundException

@Service
class SingleTicketServiceImpl(
        private val singleTicketRepository: SingleTicketRepository,
        private val authClient: AuthClient
): SingleTicketService {

    override fun getTicketById(id: Long): SingleTicket = singleTicketRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Could not find single ticket with id '${id}'.") }

    override fun getTicketsByUsername(username: String): List<SingleTicket> =
            singleTicketRepository.getAllByPassengerUsername(username)

    override fun createTicket(passengerUsername: String, discounted: Boolean): SingleTicket {
        val passengerInfo = authClient.getPassengerInfo(passengerUsername)
        if (discounted && !passengerInfo.eligibleForDiscount)
            throw IllegalArgumentException("Passenger '${passengerUsername}' is not eligible for a discounted ticket.")

        val ticket = SingleTicket(passengerUsername, discounted)
        return singleTicketRepository.save(ticket)
    }

    override fun punchTicket(ticketId: Long, punchTicketDto: PunchTicketDto): SingleTicket {
        val ticket = getTicketById(ticketId)
        if(ticket.isPunched)
            throw IllegalArgumentException("Ticket with id '${ticket.id}' is already punched.")
        ticket.isPunched = true
        ticket.courseId = punchTicketDto.courseId
        return singleTicketRepository.save(ticket)
    }

}