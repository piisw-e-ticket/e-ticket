package com.example.ticket.dto

import com.example.ticket.model.PeriodicTicket
import com.example.ticket.model.SingleTicket
import java.time.LocalDateTime


data class TicketsReadDto(
    val singleTickets: List<SingleTicketReadDto>,
    val periodicTickets: List<PeriodicTicketReadDto>
) {
    companion object {
        fun from(singleTickets: List<SingleTicket>, periodicTickets: List<PeriodicTicket>): TicketsReadDto = TicketsReadDto(
            singleTickets = singleTickets.map { SingleTicketReadDto.from(it) },
            periodicTickets = periodicTickets.map { PeriodicTicketReadDto.from(it) }
        )
    }
}

data class SingleTicketReadDto(
    val id: Long,
    val passengerUsername: String,
    val isDiscounted: Boolean,
    val isPunched: Boolean,
    val courseId: Long?
) {
    companion object {
        fun from(singleTicket: SingleTicket): SingleTicketReadDto = SingleTicketReadDto(
            id = singleTicket.id,
            passengerUsername = singleTicket.passengerUsername,
            isDiscounted = singleTicket.isDiscounted,
            isPunched = singleTicket.isPunched,
            courseId = singleTicket.courseId
        )
    }
}

data class PeriodicTicketReadDto(
    val id: Long,
    val passengerUsername: String,
    val isDiscounted: Boolean,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
) {
    companion object {
        fun from(periodicTicket: PeriodicTicket): PeriodicTicketReadDto = PeriodicTicketReadDto(
            id = periodicTicket.id,
            passengerUsername = periodicTicket.passengerUsername,
            isDiscounted = periodicTicket.isDiscounted,
            startDate = periodicTicket.startDate,
            endDate = periodicTicket.endDate
        )
    }
}


data class PunchTicketDto(
    val courseId: Long
)

data class PeriodicTicketCreateDto(
    val startDate: LocalDateTime,
    val endDate: LocalDateTime
)

data class ValidateTicketDto(
    val ticketId: Long,
    val courseId: Long
)

data class TicketValidationResultDto(
    val isValid: Boolean,
    val errors: List<String>,
    val details: TicketValidationResultDetailsDto
)

data class TicketValidationResultDetailsDto(
    val username: String,
    val email: String,
    val isUserEligibleForDiscount: Boolean,
    val isTicketDiscounted: Boolean,
    val startDate: LocalDateTime?,
    val endDate: LocalDateTime?,
    val assignedCourseId: Long?
)
