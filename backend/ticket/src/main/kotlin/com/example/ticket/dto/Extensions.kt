package com.example.ticket.dto

import com.example.ticket.model.PeriodicTicket
import com.example.ticket.model.SingleTicket
import com.example.ticket.model.TicketValidationResult

fun TicketValidationResult.asDto() = TicketValidationResultDto(
    this.isValid,
    this.reason,
    TicketValidationResultDetailsDto(
        this.owner!!.username,
        this.owner!!.email,
        this.owner!!.isEligibleForDiscount,
        this.ticket.isDiscounted,
        (this.ticket as? PeriodicTicket)?.startDate,
        (this.ticket as? PeriodicTicket)?.endDate,
        (this.ticket as? SingleTicket)?.courseId
    )
)
