package com.example.ticket.service

import com.example.ticket.dto.PeriodicTicketCreateDto
import com.example.ticket.model.PeriodicTicket

interface PeriodicTicketService: TicketService {

    override fun getTicketsByUsername(username: String): List<PeriodicTicket>
    fun createTicket(passengerUsername: String, discounted: Boolean, createDto: PeriodicTicketCreateDto): PeriodicTicket

}