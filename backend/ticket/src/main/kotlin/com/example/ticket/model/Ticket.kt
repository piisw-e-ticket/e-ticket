package com.example.ticket.model

import javax.persistence.*

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
abstract class Ticket(
        open val passengerUsername: String,
        open val isDiscounted: Boolean = false
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long = 0

    abstract fun validate(courseId: Long): TicketValidationResult

}