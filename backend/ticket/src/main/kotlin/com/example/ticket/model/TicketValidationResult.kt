package com.example.ticket.model

class TicketValidationResult private constructor(
    val ticket: Ticket,
    val isValid: Boolean,
    val reason: String?
) {

    var owner: TicketOwner? = null
        private set

    fun setOwner(owner: TicketOwner): TicketValidationResult =
        this.apply { this.owner = owner }

    fun ensure(predicate: Boolean, reason: String): TicketValidationResult =
        if (!isValid || predicate) this else TicketValidationResult(ticket, false, reason)

    fun ensureIf(condition: Boolean, predicate: Boolean, reason: String): TicketValidationResult =
        if (condition) this.ensure(predicate, reason) else this

    companion object {
        fun success(ticket: Ticket): TicketValidationResult = TicketValidationResult(ticket,true, null)
    }

}
