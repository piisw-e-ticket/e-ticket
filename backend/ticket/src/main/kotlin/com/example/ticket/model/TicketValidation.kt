package com.example.ticket.model

import java.util.*

data class Validator<in T>(
    val predicate: (T) -> Boolean,
    val error: String
)

fun <T> ensure(predicate: (T) -> Boolean, error: String) = Validator(predicate, error)

class ValidationChain<out T> private constructor(
    val target: T,
    private val brokeOnError: Boolean = false,
    private val _errors: LinkedList<String>,
) {
    val errors get() = _errors as List<String>

    fun link(validator: Validator<T>): ValidationChain<T> =
        if (isBroken() || validator.predicate(target)) this else addError(validator.error)

    fun <E> include(other: ValidationChain<E>): ValidationChain<T> =
        ValidationChain(this.target, this.brokeOnError, _errors.apply { addAll(other.errors) })

    fun scoped(breakOnError: Boolean, x: (ValidationChain<T>) -> ValidationChain<*>): ValidationChain<T> =
        begin(target, breakOnError).apply { x(this) }.map {
            ValidationChain(it.target, false, _errors.apply { addAll(it.errors) })
        }

    fun <E> map(func: (ValidationChain<T>) -> E): E = func(this)

    private fun addError(error: String) = ValidationChain(target, brokeOnError, _errors.apply { add(error) })
    private fun isBroken() = brokeOnError && errors.isNotEmpty()

    companion object {
        fun <T> begin(target: T, breakOnError: Boolean = false): ValidationChain<T> = ValidationChain(
            target, breakOnError, LinkedList<String>())
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