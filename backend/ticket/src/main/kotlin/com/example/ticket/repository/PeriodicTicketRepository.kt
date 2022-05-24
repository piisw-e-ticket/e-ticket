package com.example.ticket.repository

import com.example.ticket.model.PeriodicTicket
import org.springframework.data.jpa.repository.JpaRepository

interface PeriodicTicketRepository: JpaRepository<PeriodicTicket, Long> {

    fun getAllByPassengerUsername(username: String): List<PeriodicTicket>

}