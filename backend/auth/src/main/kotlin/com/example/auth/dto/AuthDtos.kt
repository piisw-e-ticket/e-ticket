package com.example.auth.dto

import com.example.auth.model.Role
import com.fasterxml.jackson.annotation.JsonValue
import java.util.*

// Requests
data class LoginDto(val username: String, val password: String)
data class RegisterDto(val username: String, val password: String, val email: String)

// Responses
data class JwtTokenPairDto(
    val accessToken: String,
    val accessTokenExpirationDate: Date,
    val refreshToken: String,
    val refreshTokenExpirationDate: Date,
    val roles: List<Role>,
    val type: String = "Bearer")
