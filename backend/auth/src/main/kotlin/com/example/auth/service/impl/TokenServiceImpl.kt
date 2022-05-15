package com.example.auth.service.impl

import com.example.auth.error.UnauthorizedException
import com.example.auth.model.TokenFamily
import com.example.auth.model.TokenPair
import com.example.auth.model.User
import com.example.auth.service.TokenFamilyService
import com.example.auth.service.TokenService
import io.jsonwebtoken.Claims
import java.util.*

class TokenServiceImpl(
    private val tokenFamilyService: TokenFamilyService,
    private val jwtUtil: JwtUtil
) : TokenService {

    override fun createTokenPair(user: User, tokenFamily: TokenFamily?): TokenPair {
        val family = tokenFamily ?: TokenFamily()

        val tokenPair = jwtUtil.generateTokenPair(user.username, family.id)
        family.validToken = tokenPair.refreshToken.token
        tokenFamilyService.save(family)

        return tokenPair
    }

    override fun createTokenPairUsingRefreshToken(
        refreshToken: String,
        provideUser: (String) -> User
    ): TokenPair {
        val claims: Claims = jwtUtil.getClaims(refreshToken)
            ?: throw IllegalArgumentException("Refresh token is malformed")
        val familyId: String = claims.getOrDefault("fid", null)?.toString()
            ?: throw IllegalArgumentException("Refresh token must come from a token family")
        val tokenFamily = tokenFamilyService.getById(familyId)
            ?: throw IllegalArgumentException("Token family does not exist")

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

        val user = provideUser(claims.subject)
        return createTokenPair(user, tokenFamily)
    }

}