package com.example.auth.dto

import com.example.auth.model.Role

data class LoginDto(val username: String, val password: String)
data class RegisterDto(val username: String, val password: String, val email: String)
data class JwtTokenDto(val token: String, val roles: List<Role>, val type: String = "Bearer")
