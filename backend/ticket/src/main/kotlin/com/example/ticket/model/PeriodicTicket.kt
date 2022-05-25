package com.example.ticket.model

import java.time.LocalDateTime
import javax.persistence.Entity

@Entity
class PeriodicTicket(
        val startDate: LocalDateTime,
        val endDate: LocalDateTime,
        passengerUsername: String,
        isDiscounted: Boolean = false
): Ticket(passengerUsername, isDiscounted) {

    override fun validate(courseId: Long): TicketValidationResult {
        val now = LocalDateTime.now()

        return TicketValidationResult.success()
            .ensure(now.isAfter(startDate), "Ticket validity period has not started yet.")
            .ensure(now.isBefore(endDate), "Ticket validity period has been exceeded already.")
    }
}