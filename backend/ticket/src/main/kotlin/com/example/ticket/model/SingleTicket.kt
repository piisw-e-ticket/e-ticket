package com.example.ticket.model

import javax.persistence.Entity

@Entity
class SingleTicket(
        passengerUsername: String,
        isDiscounted: Boolean = false
): Ticket(passengerUsername, isDiscounted) {

    var isPunched: Boolean = false

    var courseId: Long? = null

    override fun validate(courseId: Long): TicketValidationResult =
        TicketValidationResult.success()
            .ensure(isPunched, "Ticket has not been punched yet.")
            .ensure(
                this.courseId == courseId,
                "Ticket has been punched for a different course. " +
                        "Assigned course: $courseId, given course: $courseId.")

}