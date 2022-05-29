package com.example.ticket.model

import java.util.LinkedList

class ValidationChain<out T> private constructor(
    val target: T,
    private val _errors: LinkedList<String>
) {
    val errors get() = _errors as List<String>

    fun ensure(predicate: (ticket: T) -> Boolean, error: String): ValidationChain<T> =
        if (predicate(target)) this else ValidationChain(target, _errors.apply { add(error) })

    fun <E> map(func: (ValidationChain<T>) -> E): E = func(this)

    companion object {
        fun <T> begin(target: T): ValidationChain<T> = ValidationChain(target, LinkedList<String>())
    }
}

data class TicketValidationResult(
    val ticket: Ticket,
    val owner: TicketOwner,
    val errors: List<String>,
    val isSuccess: Boolean = errors.isEmpty()
)

data class TicketOwner (
    val username: String,
    val email: String,
    val isEligibleForDiscount: Boolean
)