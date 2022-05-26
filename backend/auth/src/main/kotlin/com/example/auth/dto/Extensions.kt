package com.example.auth.dto

import com.example.auth.model.ETicketUser
import com.example.auth.model.Passenger

fun ETicketUser.asUserInfoDto(): UserInfoDto = UserInfoDto(
    username = this.username,
    email = this.email,
    role = this.role,
    isActive = this.active,
    isEligibleForDiscount = (this as? Passenger)?.isEligibleForDiscount
)

fun Passenger.asPassengerInfoDto(): PassengerInfoDto = PassengerInfoDto(
    email = this.email,
    isEligibleForDiscount = this.isEligibleForDiscount
)
