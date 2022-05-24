package com.example.ticket.service.impl

import com.example.ticket.client.AuthClient
import com.example.ticket.dto.PeriodicTicketCreateDto
import com.example.ticket.dto.Role
import com.example.ticket.model.PeriodicTicket
import com.example.ticket.repository.PeriodicTicketRepository
import com.example.ticket.service.PeriodicTicketService
import org.springframework.stereotype.Service
import javax.persistence.EntityNotFoundException

@Service
class PeriodicTicketServiceImpl(
        private val periodicTicketRepository: PeriodicTicketRepository,
        private val authClient: AuthClient
): PeriodicTicketService {

    override fun getTicketById(id: Long): PeriodicTicket = periodicTicketRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Could not find periodic ticket with id '${id}'.") }

    override fun getTicketsByUsername(username: String): List<PeriodicTicket> =
            periodicTicketRepository.getAllByPassengerUsername(username)

    override fun createTicket(passengerUsername: String, discounted: Boolean, createDto: PeriodicTicketCreateDto): PeriodicTicket {
        val userInfo = authClient.getUserInfo()
        if (userInfo.role != Role.PASSENGER)
            throw IllegalArgumentException("Only passenger is allowed to buy tickets.")
        if (discounted and !userInfo.eligibleForDiscount!!)
            throw IllegalArgumentException("Passenger '${userInfo.username}' is not eligible for a discounted ticket.")

        val ticket = PeriodicTicket(
                startDate = createDto.startDate,
                endDate = createDto.endDate,
                passengerUsername = passengerUsername,
                isDiscounted = discounted
        )
        return periodicTicketRepository.save(ticket)
    }

}