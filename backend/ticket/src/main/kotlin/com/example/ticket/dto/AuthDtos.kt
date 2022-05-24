package com.example.ticket.dto

data class UserInfoDto(
    val username: String,
    val email: String,
    val role: Role,
    val active: Boolean,
    val eligibleForDiscount: Boolean?
)

enum class Role {
    PASSENGER, TICKET_COLLECTOR, ADMIN
}