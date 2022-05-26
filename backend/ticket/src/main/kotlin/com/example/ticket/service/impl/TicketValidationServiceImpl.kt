package com.example.ticket.service.impl

import com.example.ticket.client.AuthClient
import com.example.ticket.model.TicketOwner
import com.example.ticket.model.TicketValidationResult
import com.example.ticket.repository.TicketRepository
import com.example.ticket.service.TicketValidationService
import org.springframework.stereotype.Service

@Service
class TicketValidationServiceImpl(
    private val authClient: AuthClient,
    private val ticketRepository: TicketRepository
) : TicketValidationService{
    override fun validateTicket(
        ticketId: Long,
        courseId: Long
    ): TicketValidationResult {
        val ticket = ticketRepository.getById(ticketId)
        val passengerInfo = authClient.getPassengerInfo(ticket.passengerUsername)

        return ticket.validate(courseId)
            .setOwner(TicketOwner(
                ticket.passengerUsername,
                passengerInfo.email,
                passengerInfo.eligibleForDiscount))
            .ensureIf(ticket.isDiscounted, passengerInfo.eligibleForDiscount, "")
    }
}
