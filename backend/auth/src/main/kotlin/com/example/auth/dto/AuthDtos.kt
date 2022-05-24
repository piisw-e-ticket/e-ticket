package com.example.auth.dto

import com.example.auth.model.Role
import com.fasterxml.jackson.annotation.JsonInclude
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern

// Requests
data class LoginDto(val username: String, val password: String)
data class RegisterDto(

    @field:Pattern(
        regexp = "[a-z0-9]{4,15}",
        message = "Should contain only lowercase letters or numbers and be 4-15 characters long."
    )
    val username: String,

    @field:Pattern(
        regexp = "[a-zA-Z0-9]{4,}",
        message = "Should contain only letters or numbers and be minimum 4 characters long."
    )
    val password: String,

    @field:NotBlank
    @field:Pattern(regexp = "[a-z0-9.]+@[a-z0-9][a-z0-9.]*\\.[a-z]{2,3}", message = "The email address is incorrect.")
    val email: String
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UserInfoDto(
    val username: String,
    val email: String,
    val role: Role,
    val isActive: Boolean,
    val isEligibleForDiscount: Boolean? = null
)

// Responses
data class JwtTokenPairDto(
    val accessToken: String,
    val accessTokenExpirationDate: Date,
    val refreshToken: String,
    val refreshTokenExpirationDate: Date,
    val role: Role,
    val type: String = "Bearer"
)
