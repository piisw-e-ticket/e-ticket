package com.example.auth.service.impl

import com.example.auth.config.JwtProperties
import com.example.auth.model.ETicketUser
import com.example.auth.model.Token
import com.example.auth.model.TokenPair
import com.example.auth.service.ExpiredTokenException
import com.example.auth.service.MalformedTokenException
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*


@Component
class JwtUtil(
        val jwtProperties: JwtProperties
) {
    fun generateTokenPair(user: ETicketUser, familyId: String): TokenPair {
        val accessToken = createTokenForUser(user, familyId, jwtProperties.accessTokenMaxAgeInMinutes)
        val refreshToke = createTokenForUser(user, familyId, jwtProperties.refreshTokenMaxAgeInMinutes)
        return TokenPair(accessToken, refreshToke)
    }

    fun getClaims(token: String?): Claims {
        return try {
            Jwts.parser()
                .setSigningKey(jwtProperties.secret)
                .parseClaimsJws(token).body
        } catch (e: JwtException) {
            when (e) {
                is ExpiredJwtException -> throw ExpiredTokenException("Token is expired", e.claims)
                else -> throw MalformedTokenException("Token is malformed. Details: ${e.message}")
            }
        }
    }

    private fun createTokenForUser(
        user: ETicketUser,
        fid: String,
        maxAgeInMinutes: Long
    ): Token {
        val claims = Jwts.claims()
            .setIssuedAt(Date.from(Instant.now()))
            .setExpiration(Date.from(Instant.now().plusSeconds(maxAgeInMinutes * 60)))

        val stringToken = Jwts.builder()
            .setClaims(claims)
            .setSubject(user.username)
            .claim("role", user.role)
            .claim("fid", fid)
            .signWith(SignatureAlgorithm.HS512, jwtProperties.secret)
            .compact()

        return Token(stringToken, claims.expiration.toInstant())
    }
}