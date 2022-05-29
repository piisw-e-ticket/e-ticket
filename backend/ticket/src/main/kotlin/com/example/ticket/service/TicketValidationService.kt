package com.example.ticket.service

import com.example.ticket.model.TicketValidationResult
import javax.persistence.EntityNotFoundException

interface TicketValidationService {
    @Throws(EntityNotFoundException::class, )
    fun validateTicket(
        ticketId: Long,
        courseId: Long
    ): TicketValidationResult
}
