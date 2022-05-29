package com.example.ticket.service.impl

import com.example.ticket.client.AuthClient
import com.example.ticket.model.TicketOwner
import com.example.ticket.model.TicketValidationResult
import com.example.ticket.model.ValidationChain
import com.example.ticket.model.ensure
import com.example.ticket.repository.TicketRepository
import com.example.ticket.service.TicketValidationService
import org.springframework.stereotype.Service
import javax.persistence.EntityNotFoundException

@Service
class TicketValidationServiceImpl(
    private val authClient: AuthClient,
    private val ticketRepository: TicketRepository
) : TicketValidationService{
    override fun validateTicket(
        ticketId: Long,
        courseId: Long
    ): TicketValidationResult {
        val ticket = ticketRepository.findById(ticketId)
            .orElseThrow { EntityNotFoundException("Ticket with id: '$ticketId' does not exist") }
        val passengerInfo = authClient.getPassengerInfo(ticket.passengerUsername)

        return ValidationChain.begin(ticket, breakOnError = false)
            .include(ticket.validate(courseId))
            .link(ensure(
                { t -> if (t.isDiscounted) passengerInfo.eligibleForDiscount else true },
                "Ticket owner with username: '${ticket.passengerUsername}' is not eligible for discounted ticket."))
            .map {
                TicketValidationResult(
                    it.target,
                    TicketOwner(ticket.passengerUsername, passengerInfo.email, passengerInfo.eligibleForDiscount),
                    it.errors)
            }
    }
}
