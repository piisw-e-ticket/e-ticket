package com.example.auth.controller

import com.example.auth.config.AuthCookieProperties
import com.example.auth.dto.*
import com.example.auth.infrastructure.RequiredRole
import com.example.auth.model.*
import com.example.auth.service.TokenService
import com.example.auth.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.util.*
import javax.validation.Valid


@RestController
@RequestMapping("/auth")
class AuthController(
        val authCookieProperties: AuthCookieProperties,
        val userService: UserService,
        val tokenService: TokenService
) {

    @PostMapping("/login")
    fun login(
        @RequestBody loginDto: LoginDto,
        @RequestParam(required = false) setCookie: Boolean = false
    ): ResponseEntity<JwtTokenPairDto> {
        val authenticatedUser = userService.authenticateUser(loginDto)
        val tokenPair = tokenService.createTokenPair(authenticatedUser)
        return createResponse(authenticatedUser, tokenPair, setCookie)
    }

    @PostMapping("/register")
    fun register(
        @Valid @RequestBody registerDto: RegisterDto,
        @RequestParam(required = false) setCookie: Boolean = false
    ): ResponseEntity<JwtTokenPairDto> {
        val registeredUser = userService.registerUser(registerDto)
        val tokenPair = tokenService.createTokenPair(registeredUser)
        return createResponse(registeredUser, tokenPair, setCookie)
    }

    @PostMapping("/refresh")
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

    @GetMapping("/info")
    fun getUserInfo(
        @RequestHeader("username") username: String
    ): ResponseEntity<UserInfoDto> {
        val user = userService.getUserByUsername(username)
        return ResponseEntity.ok(user.asUserInfoDto())
    }

    @GetMapping("/passenger-info/{username}")
    @RequiredRole(Role.TICKET_COLLECTOR)
    fun getPassengerInfo(
        @PathVariable("username") username: String
    ): Mono<ResponseEntity<PassengerInfoDto>> {
        val user = userService.getUserByUsername(username)
        if (user !is Passenger)
            throw IllegalArgumentException("Provided username: '$username' does not belong to passenger")
        return Mono.just(ResponseEntity.ok(user.asPassengerInfoDto()))
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