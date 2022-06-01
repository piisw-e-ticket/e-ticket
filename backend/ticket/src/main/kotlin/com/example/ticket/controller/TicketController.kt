package com.example.ticket.controller

import com.example.ticket.dto.*
import com.example.ticket.infrastructure.RequiredRole
import com.example.ticket.model.Role
import com.example.ticket.service.PeriodicTicketService
import com.example.ticket.service.SingleTicketService
import com.example.ticket.service.TicketValidationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.ws.rs.QueryParam

@RestController
@RequestMapping("/tickets")
class TicketController(
    private val singleTicketService: SingleTicketService,
    private val periodicTicketService: PeriodicTicketService,
    private val ticketValidationService: TicketValidationService
) {

    @GetMapping
    @RequiredRole(Role.PASSENGER)
    fun getAllUserTickets(@RequestHeader("username") username: String): ResponseEntity<TicketsReadDto> {
        val singleTickets = singleTicketService.getTicketsByUsername(username)
        val periodicTickets = periodicTicketService.getTicketsByUsername(username)
        return ResponseEntity.ok(TicketsReadDto.from(singleTickets, periodicTickets))
    }

    @PostMapping("/single")
    @RequiredRole(Role.PASSENGER)
    fun buySingleTicket(
        @RequestHeader("username") username: String,
        @QueryParam("discounted") discounted: Boolean = false
    ): ResponseEntity<SingleTicketReadDto> {
        val ticket = singleTicketService.createTicket(username, discounted)
        return ResponseEntity.ok(SingleTicketReadDto.from(ticket))
    }

    @PatchMapping("/single/{ticketId}")
    @RequiredRole(Role.PASSENGER)
    fun punchSingleTicket(
        @PathVariable ticketId: Long,
        @RequestBody punchTicketDto: PunchTicketDto
    ): ResponseEntity<SingleTicketReadDto> {
        val ticket = singleTicketService.punchTicket(ticketId, punchTicketDto)
        return ResponseEntity.ok(SingleTicketReadDto.from(ticket))
    }

    @PostMapping("/periodic")
    @RequiredRole(Role.PASSENGER)
    fun buyPeriodicTicket(
        @RequestHeader("username") username: String,
        @QueryParam("discounted") discounted: Boolean = false,
        @RequestBody periodicTicketCreateDto: PeriodicTicketCreateDto
    ): ResponseEntity<PeriodicTicketReadDto> {
        val ticket = periodicTicketService.createTicket(username, discounted, periodicTicketCreateDto)
        return ResponseEntity.ok(PeriodicTicketReadDto.from(ticket))
    }

    @PostMapping("/validate")
    @RequiredRole(Role.TICKET_COLLECTOR)
    fun validateTicket(
        @RequestBody validateTicketDto: ValidateTicketDto
    ) : ResponseEntity<TicketValidationResultDto> {
        val result = ticketValidationService.validateTicket(
            validateTicketDto.ticketId,
            validateTicketDto.courseId)
        return ResponseEntity.ok(result.asDto())
    }

}