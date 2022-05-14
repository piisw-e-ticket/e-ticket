package com.example.auth.controller

import com.example.auth.config.AuthCookieProperties
import com.example.auth.dto.JwtTokenPairDto
import com.example.auth.dto.LoginDto
import com.example.auth.dto.RegisterDto
import com.example.auth.error.UnauthorizedException
import com.example.auth.model.AuthCookie
import com.example.auth.model.TokenFamily
import com.example.auth.model.TokenPair
import com.example.auth.model.User
import com.example.auth.service.TokenFamilyService
import com.example.auth.service.UserService
import com.example.auth.service.impl.JwtUtil
import io.jsonwebtoken.Claims
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class AuthController(
        val jwtUtil: JwtUtil,
        val authCookieProperties: AuthCookieProperties,
        val userService: UserService,
        val tokenFamilyService: TokenFamilyService
) {

    @PostMapping("/auth/login")
    fun login(
        @RequestBody loginDto: LoginDto,
        @RequestParam(required = false) setCookie: Boolean = false
    ): ResponseEntity<JwtTokenPairDto> {
        val authenticatedUser = userService.authenticateUser(loginDto)
        val tokenPair = createTokenPairWithNewTokenFamily(authenticatedUser)
        return createResponse(authenticatedUser, tokenPair, setCookie)
    }

    @PostMapping("/auth/register")
    fun register(
        @RequestBody registerDto: RegisterDto,
        @RequestParam(required = false) setCookie: Boolean = false
    ): ResponseEntity<JwtTokenPairDto> {
        val registeredUser = userService.registerUser(registerDto)
        val tokenPair = createTokenPairWithNewTokenFamily(registeredUser)
        return createResponse(registeredUser, tokenPair, setCookie)
    }

    @PostMapping("/auth/refresh")
    fun refresh(
        @RequestHeader("Authorization", required = true) refreshToken: String,
        @RequestParam(required = false) setCookie: Boolean = false
    ): ResponseEntity<JwtTokenPairDto> {
        val claims: Claims = jwtUtil.getClaims(refreshToken)
            ?: throw IllegalArgumentException("Refresh token is malformed")
        val familyId: String = claims.getOrDefault("fid", null)?.toString()
            ?: throw IllegalArgumentException("Refresh token must come from a token family")

        val tokenFamily = tokenFamilyService.getById(familyId)
        if (tokenFamily.isInvalidated)
            throw UnauthorizedException("Token family is invalidated")

        if (Date().after(claims.expiration)) {
            tokenFamilyService.invalidate(tokenFamily)
            throw UnauthorizedException("Refresh token is expired")
        }

        if (tokenFamily.validToken != refreshToken) {
            tokenFamilyService.invalidate(tokenFamily)
            throw UnauthorizedException("Refresh token has already been used")
        }

        val loggedInUser = userService.getUserByUsername(claims.subject)
        val tokenPair = jwtUtil.generateTokenPair(loggedInUser.username, tokenFamily.id)
        tokenFamily.validToken = tokenPair.refreshToken.token
        tokenFamilyService.save(tokenFamily)

        return createResponse(loggedInUser, tokenPair, setCookie)
    }

    private fun createResponse(
        user: User,
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
            user.roles
        ))
    }

    private fun createTokenPairWithNewTokenFamily(user: User): TokenPair {
        val tokenFamily = TokenFamily()
        val tokenPair = jwtUtil.generateTokenPair(user.username, tokenFamily.id)
        tokenFamily.validToken = tokenPair.refreshToken.token
        tokenFamilyService.save(tokenFamily)

        return tokenPair
    }
}