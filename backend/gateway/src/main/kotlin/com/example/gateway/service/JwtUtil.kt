package com.example.gateway.service

import com.example.gateway.config.JwtProperties
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.security.SignatureException
import java.util.*


@Component
class JwtUtil {

    @Autowired
    private lateinit var jwtProperties: JwtProperties

    fun getClaims(token: String?): Claims? {
        try {
            return Jwts.parser().setSigningKey(jwtProperties.secret).parseClaimsJws(token).body
        } catch (e: Exception) {
            println(e.message + " => " + e)
        }
        return null
    }

    fun generateToken(id: String?): String? {
        val claims: Claims = Jwts.claims().setSubject(id)
        val nowMillis = System.currentTimeMillis()
        val expMillis: Long = nowMillis + jwtProperties.expireTime
        val exp = Date(expMillis)
        return Jwts.builder().setClaims(claims).setIssuedAt(Date(nowMillis)).setExpiration(exp)
                .signWith(SignatureAlgorithm.HS512, jwtProperties.secret).compact()
    }

    fun validateToken(token: String?) {
        try {
            Jwts.parser().setSigningKey(token).parseClaimsJws(token)
        } catch (ex: Exception) {
            throw Exception("Error parsing jwt.")
        }
    }

}