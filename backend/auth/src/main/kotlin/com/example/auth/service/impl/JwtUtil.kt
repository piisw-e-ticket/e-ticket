package com.example.auth.service.impl

import com.example.auth.config.JwtProperties
import com.example.auth.model.Token
import com.example.auth.model.TokenPair
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*


@Component
class JwtUtil(
        val jwtProperties: JwtProperties
) {
    fun generateTokenPair(subject: String, familyId: String): TokenPair {
        val accessToken = createToken(
            createClaimsForSubject(subject, jwtProperties.accessTokenMaxAgeInMinutes),
            familyId)
        val refreshToke = createToken(
            createClaimsForSubject(subject, jwtProperties.refreshTokenMaxAgeInMinutes),
            familyId)
        return TokenPair(accessToken, refreshToke)
    }

    fun validateToken(authToken: String?): Boolean {
        return try {
            Jwts.parser().setSigningKey(jwtProperties.secret).parseClaimsJws(authToken)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    fun getClaims(token: String?): Claims? {
        return try {
            Jwts.parser().setSigningKey(jwtProperties.secret).parseClaimsJws(token).body
        } catch (e: Exception) {
            println(e.message + " => " + e)
            null
        }
    }

    private fun createClaimsForSubject(subject: String, maxAgeInMinutes: Long): Claims {
        return Jwts.claims()
            .setSubject(subject)
            .setIssuedAt(Date.from(Instant.now()))
            .setExpiration(Date.from(Instant.now().plusSeconds(maxAgeInMinutes * 60)))
    }

    private fun createToken(claims: Claims, fid: String): Token {
        val stringToken = Jwts.builder()
            .setClaims(claims)
            .claim("fid", fid)
            .signWith(SignatureAlgorithm.HS512, jwtProperties.secret)
            .compact()

        return Token(stringToken, claims.expiration.toInstant())
    }
}