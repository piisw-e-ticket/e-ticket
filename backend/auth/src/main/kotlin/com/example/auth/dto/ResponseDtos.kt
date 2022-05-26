package com.example.auth.dto

import com.example.auth.model.Role
import com.fasterxml.jackson.annotation.JsonInclude
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UserInfoDto(
    val username: String,
    val email: String,
    val role: Role,
    val isActive: Boolean,
    val isEligibleForDiscount: Boolean? = null
)

data class PassengerInfoDto(
    val email: String,
    val isEligibleForDiscount: Boolean
)

data class JwtTokenPairDto(
    val accessToken: String,
    val accessTokenExpirationDate: Date,
    val refreshToken: String,
    val refreshTokenExpirationDate: Date,
    val role: Role,
    val type: String = "Bearer"
)
