package com.example.ticket.dto

import com.example.ticket.model.Role

data class UserInfoDto(
    val username: String,
    val email: String,
    val role: Role,
    val active: Boolean,
    val eligibleForDiscount: Boolean?
)

data class PassengerInfoDto(
    val email: String,
    val eligibleForDiscount: Boolean
)
