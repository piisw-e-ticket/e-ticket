package com.example.auth.service.impl

import com.example.auth.model.TokenFamily
import com.example.auth.model.TokenPair
import com.example.auth.model.ETicketUser
import com.example.auth.service.*
import io.jsonwebtoken.Claims
import org.springframework.stereotype.Service

@Service
class TokenServiceImpl(
    private val tokenFamilyService: TokenFamilyService,
    private val jwtUtil: JwtUtil
) : TokenService {

    override fun createTokenPair(user: ETicketUser, tokenFamily: TokenFamily?): TokenPair {
        val family = tokenFamily ?: TokenFamily()

        val tokenPair = jwtUtil.generateTokenPair(user, family.id)
        family.validToken = tokenPair.refreshToken.token
        tokenFamilyService.save(family)

        return tokenPair
    }

    override fun createTokenPairUsingRefreshToken(
        refreshToken: String,
        provideUser: (String) -> ETicketUser
    ): TokenPair {
        lateinit var claims: Claims
        try {
            claims = jwtUtil.getClaims(refreshToken)
        } catch (e: ExpiredTokenException) {
            tryInvalidateTokenFamily(e.claims)
            throw e
        }

        val familyId: String = claims.getOrDefault("fid", null)?.toString()
            ?: throw MalformedTokenException("Refresh token must come from a token family")
        val tokenFamily = tokenFamilyService.getById(familyId)

        if (tokenFamily.isInvalidated)
            throw RefreshTokenReuseAttemptException("Token family is invalidated")

        if (tokenFamily.validToken != refreshToken) {
            tokenFamilyService.invalidate(tokenFamily)
            throw RefreshTokenReuseAttemptException("Refresh token has already been used")
        }

        val user = provideUser(claims.subject)
        return createTokenPair(user, tokenFamily)
    }

    private fun tryInvalidateTokenFamily(claims: Claims?) {
        claims?.getOrDefault("fid", null)?.toString()?.let { fid ->
            tokenFamilyService.tryInvalidate(fid)
        }
    }
}