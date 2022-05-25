package com.example.ticket.model

class TicketValidationResult private constructor(val isValid: Boolean, val reason: String?) {

    fun ensure(condition: Boolean, reason: String): TicketValidationResult =
        if (!isValid || condition) this else TicketValidationResult(false, reason)

    companion object {
        fun success(): TicketValidationResult = TicketValidationResult(true, null)
    }

}
