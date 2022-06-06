package com.example.ticket.service

import com.example.ticket.dto.PunchTicketDto
import com.example.ticket.model.SingleTicket

interface SingleTicketService: TicketService {

    override fun getTicketsByUsername(username: String): List<SingleTicket>
    fun createTicket(passengerUsername: String, discounted: Boolean): SingleTicket
    fun punchTicket(ticketId: Long, punchTicketDto: PunchTicketDto): SingleTicket

}