package com.example.ticket.service

import com.example.ticket.model.TicketValidationResult

interface TicketValidationService {
    fun validateTicket(
        ticketId: Long,
        courseId: Long
    ): TicketValidationResult
}
