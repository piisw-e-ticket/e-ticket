package com.example.auth.controller

import com.example.auth.config.AuthCookieProperties
import com.example.auth.dto.JwtTokenDto
import com.example.auth.dto.LoginDto
import com.example.auth.dto.RegisterDto
import com.example.auth.service.UserService
import com.example.auth.service.impl.JwtUtil
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController(
        val jwtUtil: JwtUtil,
        val authCookieProperties: AuthCookieProperties,
        val userService: UserService
) {

    @PostMapping("/auth/login")
    fun login(@RequestBody loginDto: LoginDto): ResponseEntity<JwtTokenDto> {
        val authenticatedUser = userService.authenticateUser(loginDto)
        val token = jwtUtil.generateToken(authenticatedUser)
        return ResponseEntity.ok(JwtTokenDto(token, authenticatedUser.roles)).apply {
            headers.set("AuthCookie", createAuthCookie(token))
        }
    }

    @PostMapping("/auth/register")
    fun register(@RequestBody registerDto: RegisterDto): ResponseEntity<JwtTokenDto> {
        val user = userService.registerUser(registerDto)
        val token = jwtUtil.generateToken(user)
        return ResponseEntity.ok(JwtTokenDto(token, user.roles)).apply {
            headers.set("AuthCookie", createAuthCookie(token))
        }
    }

    private fun createAuthCookie(token: String): String {
        var cookie = "${authCookieProperties.name}=$token"
        cookie += "; Max-Age=${authCookieProperties.durationInMinutes * 60}"
        cookie += "; Path=${authCookieProperties.path}"
        cookie += "; SameSite=${authCookieProperties.sameSite.name}"
        if (authCookieProperties.secure)
            cookie += "; Secure"
        if (authCookieProperties.httpOnly)
            cookie += "; HttpOnly"

        return cookie
    }
}