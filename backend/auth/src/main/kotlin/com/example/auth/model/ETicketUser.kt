package com.example.auth.model

import javax.persistence.*

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
abstract class ETicketUser(
    open val username: String,
    open val email: String,
    open val password: String
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long = 0

    open var active: Boolean = true

    abstract val role: Role

}

enum class Role {
    PASSENGER, TICKET_COLLECTOR, ADMIN
}

@Entity
class Passenger(username: String, email: String, password: String): ETicketUser(username, email, password) {
    override val role: Role = Role.PASSENGER
    val isEligibleForDiscount: Boolean = false
}

@Entity
class TicketCollector(username: String, email: String, password: String): ETicketUser(username, email, password) {
    override val role: Role = Role.TICKET_COLLECTOR
}

@Entity
class Admin(username: String, email: String, password: String): ETicketUser(username, email, password) {
    override val role: Role = Role.ADMIN
}
