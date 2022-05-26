package com.example.ticket.repository

import com.example.ticket.model.Ticket
import org.springframework.data.jpa.repository.JpaRepository

interface TicketRepository : JpaRepository<Ticket, Long>