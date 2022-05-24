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

    override fun validate(courseId: Long): Boolean {
        val now = LocalDateTime.now()
        return now.isAfter(startDate) and now.isBefore(endDate)
    }

}