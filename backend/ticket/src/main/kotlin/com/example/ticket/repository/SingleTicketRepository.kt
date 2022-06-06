package com.example.ticket.repository

import com.example.ticket.model.SingleTicket
import org.springframework.data.jpa.repository.JpaRepository

interface SingleTicketRepository: JpaRepository<SingleTicket, Long> {

    fun getAllByPassengerUsername(username: String): List<SingleTicket>

}