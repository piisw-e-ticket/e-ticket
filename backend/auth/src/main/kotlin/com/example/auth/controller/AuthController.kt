package com.example.auth.controller

import com.example.auth.dto.JwtTokenDto
import com.example.auth.dto.LoginDto
import com.example.auth.dto.RegisterDto
import com.example.auth.model.User
import com.example.auth.service.UserService
import com.example.auth.service.impl.JwtUtil
import feign.Response
import io.jsonwebtoken.Jwt
import org.apache.http.auth.InvalidCredentialsException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController


@RestController
class AuthController(
        val jwtUtil: JwtUtil,
        val userService: UserService
) {

    @PostMapping("/auth/login")
    fun login(@RequestBody loginDto: LoginDto): ResponseEntity<Any> =
        try {
            val authenticatedUser = userService.authenticateUser(loginDto)
            ResponseEntity.ok(JwtTokenDto(jwtUtil.generateToken(authenticatedUser), authenticatedUser.roles))
        } catch (e: InvalidCredentialsException) {
            ResponseEntity.badRequest().body(e.message)
        }

    @PostMapping("/auth/register")
    fun register(@RequestBody registerDto: RegisterDto): ResponseEntity<Any> {
        val user = userService.registerUser(registerDto)
        val userToken = jwtUtil.generateToken(user)
        return ResponseEntity.ok(JwtTokenDto(userToken, user.roles))
    }

}