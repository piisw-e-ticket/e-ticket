package com.example.auth.service.impl

import com.example.auth.config.JwtProperties
import com.example.auth.model.User
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*


@Component
class JwtUtil(
        val jwtProperties: JwtProperties
) {

    fun getClaims(token: String?): Claims? {
        try {
            return Jwts.parser().setSigningKey(jwtProperties.secret).parseClaimsJws(token).body
        } catch (e: Exception) {
            println(e.message + " => " + e)
        }
        return null
    }

    fun generateToken(user: User): String {
        val nowMillis = System.currentTimeMillis()
        val expMillis: Long = nowMillis + jwtProperties.expireTime
        val expirationDate = Date(expMillis)
        val claims: Claims = Jwts.claims()
                .setSubject(user.username)
                .setIssuedAt(Date(nowMillis))
                .setExpiration(expirationDate)
        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, jwtProperties.secret)
                .compact()
    }

    fun validateToken(authToken: String?): Boolean {
        return try {
            Jwts.parser().setSigningKey(jwtProperties.secret).parseClaimsJws(authToken)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }

}