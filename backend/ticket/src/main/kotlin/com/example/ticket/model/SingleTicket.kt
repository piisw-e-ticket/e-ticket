package com.example.ticket.model

import javax.persistence.Entity

@Entity
class SingleTicket(
        passengerUsername: String,
        isDiscounted: Boolean = false
): Ticket(passengerUsername, isDiscounted) {

    var isPunched: Boolean = false

    var courseId: Long? = null

    override fun validate(courseId: Long): Boolean = isPunched && this.courseId == courseId

}