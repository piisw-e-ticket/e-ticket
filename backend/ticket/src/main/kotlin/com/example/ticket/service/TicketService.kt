package com.example.ticket.service

import com.example.ticket.model.Ticket

interface TicketService {

    fun getTicketsByUsername(username: String): List<Ticket>
    fun getTicketById(id: Long): Ticket

}