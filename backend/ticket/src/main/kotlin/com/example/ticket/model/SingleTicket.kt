package com.example.ticket.model

import javax.persistence.Entity

@Entity
class SingleTicket(
        passengerUsername: String,
        isDiscounted: Boolean = false
): Ticket(passengerUsername, isDiscounted) {

    var isPunched: Boolean = false

    var courseId: Long? = null

    override fun validate(courseId: Long): ValidationChain<SingleTicket> =
        ValidationChain.begin(this, breakOnError = true)
            .link(ensure({ t-> t.isPunched }, "Ticket has not been punched yet."))
            .link(ensure(
                { t-> t.courseId == courseId },
                "Ticket has been punched for a different course. " +
                "Assigned course: ${this.courseId}, given course: $courseId."))

}