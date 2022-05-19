package com.example.auth.service.impl

import com.example.auth.error.UnauthorizedException
import com.example.auth.model.TokenFamily
import com.example.auth.model.TokenPair
import com.example.auth.model.ETicketUser
import com.example.auth.service.TokenFamilyService
import com.example.auth.service.TokenService
import io.jsonwebtoken.Claims
import org.springframework.stereotype.Service
import java.util.*

@Service
class TokenServiceImpl(
    private val tokenFamilyService: TokenFamilyService,
    private val jwtUtil: JwtUtil
) : TokenService {

    override fun createTokenPair(user: ETicketUser, tokenFamily: TokenFamily?): TokenPair {
        val family = tokenFamily ?: TokenFamily()

        val tokenPair = jwtUtil.generateTokenPair(user.username, family.id)
        family.validToken = tokenPair.refreshToken.token
        tokenFamilyService.save(family)

        return tokenPair
    }

    override fun createTokenPairUsingRefreshToken(
        refreshToken: String,
        provideUser: (String) -> ETicketUser
    ): TokenPair {
        val claims: Claims = jwtUtil.getClaims(refreshToken)
        val familyId: String = claims.getOrDefault("fid", null)?.toString()
            ?: throw IllegalArgumentException("Refresh token must come from a token family.")
        val tokenFamily = tokenFamilyService.getById(familyId)

        if (tokenFamily.isInvalidated)
            throw UnauthorizedException("Token family is invalidated.")

        if (Date().after(claims.expiration)) {
            tokenFamilyService.invalidate(tokenFamily)
            throw UnauthorizedException("Refresh token is expired.")
        }

        if (tokenFamily.validToken != refreshToken) {
            tokenFamilyService.invalidate(tokenFamily)
            throw UnauthorizedException("Refresh token has already been used.")
        }

        val user = provideUser(claims.subject)
        return createTokenPair(user, tokenFamily)
    }

}