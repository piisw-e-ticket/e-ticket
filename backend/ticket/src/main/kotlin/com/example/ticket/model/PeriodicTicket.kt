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

    override fun validate(courseId: Long): ValidationChain<PeriodicTicket> {
        val now = LocalDateTime.now()

        return ValidationChain.begin(this, breakOnError = true)
            .link(ensure({ t -> now.isAfter(t.startDate) }, "Ticket validity period has not started yet."))
            .link(ensure({ t -> now.isBefore(t.endDate) }, "Ticket validity period has been exceeded already."))
    }
}