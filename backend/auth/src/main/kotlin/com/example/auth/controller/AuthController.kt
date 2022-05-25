package com.example.auth.controller

import com.example.auth.config.AuthCookieProperties
import com.example.auth.dto.*
import com.example.auth.model.AuthCookie
import com.example.auth.model.TokenPair
import com.example.auth.model.ETicketUser
import com.example.auth.service.TokenService
import com.example.auth.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*
import javax.validation.Valid

@RestController
class AuthController(
        val authCookieProperties: AuthCookieProperties,
        val userService: UserService,
        val tokenService: TokenService
) {

    @PostMapping("/auth/login")
    fun login(
        @RequestBody loginDto: LoginDto,
        @RequestParam(required = false) setCookie: Boolean = false
    ): ResponseEntity<JwtTokenPairDto> {
        val authenticatedUser = userService.authenticateUser(loginDto)
        val tokenPair = tokenService.createTokenPair(authenticatedUser)
        return createResponse(authenticatedUser, tokenPair, setCookie)
    }

    @PostMapping("/auth/register")
    fun register(
        @Valid @RequestBody registerDto: RegisterDto,
        @RequestParam(required = false) setCookie: Boolean = false
    ): ResponseEntity<JwtTokenPairDto> {
        val registeredUser = userService.registerUser(registerDto)
        val tokenPair = tokenService.createTokenPair(registeredUser)
        return createResponse(registeredUser, tokenPair, setCookie)
    }

    @PostMapping("/auth/refresh")
    fun refresh(
        @RequestHeader("Authorization", required = true) authHeader: String,
        @RequestParam(required = false) setCookie: Boolean = false
    ): ResponseEntity<JwtTokenPairDto> {
        val refreshToken = authHeader.substring("bearer".length).trim()
        lateinit var user: ETicketUser
        val tokenPair = tokenService.createTokenPairUsingRefreshToken(refreshToken) { username ->
            userService.getUserByUsername(username).apply { user = this }
        }

        return createResponse(user, tokenPair, setCookie)
    }

    @GetMapping("/auth/info")
    fun getUserInfo(@RequestHeader("username") username: String): ResponseEntity<UserInfoDto> {
        val user = userService.getUserByUsername(username)
        return ResponseEntity.ok(user.asUserInfoDto())
    }

    private fun createResponse(
            user: ETicketUser,
            tokenPair: TokenPair,
            setCookie: Boolean
    ): ResponseEntity<JwtTokenPairDto> {
        val response = ResponseEntity.ok()
        if (setCookie)
            response.header("Set-Cookie", AuthCookie(
                name = authCookieProperties.name,
                accessToken = tokenPair.accessToken,
                secure = authCookieProperties.secure,
                sameSite = authCookieProperties.sameSite,
                httpOnly = authCookieProperties.httpOnly,
                path = authCookieProperties.path
            ).asHeaderValue())

        return response.body(JwtTokenPairDto(
            tokenPair.accessToken.token,
            Date.from(tokenPair.accessToken.expiresAt),
            tokenPair.refreshToken.token,
            Date.from(tokenPair.refreshToken.expiresAt),
            user.role
        ))
    }
}