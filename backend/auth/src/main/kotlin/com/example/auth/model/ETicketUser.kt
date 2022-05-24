package com.example.auth.model

import com.example.auth.dto.UserInfoDto
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

    open fun asUserInfoDto(): UserInfoDto = UserInfoDto(
        username = this.username,
        email = this.email,
        role = this.role,
        isActive = this.active
    )
}

enum class Role {
    PASSENGER, TICKET_COLLECTOR, ADMIN
}

@Entity
class Passenger(username: String, email: String, password: String) : ETicketUser(username, email, password) {
    override val role: Role = Role.PASSENGER
    var isEligibleForDiscount: Boolean = false

    override fun asUserInfoDto(): UserInfoDto = UserInfoDto(
        username = this.username,
        email = this.email,
        role = this.role,
        isActive = this.active,
        isEligibleForDiscount = this.isEligibleForDiscount
    )
}

@Entity
class TicketCollector(username: String, email: String, password: String) : ETicketUser(username, email, password) {
    override val role: Role = Role.TICKET_COLLECTOR
}

@Entity
class Admin(username: String, email: String, password: String) : ETicketUser(username, email, password) {
    override val role: Role = Role.ADMIN
}