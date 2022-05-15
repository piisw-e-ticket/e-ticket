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

    fun getClaims(token: String): Claims? {
        return try {
            Jwts.parser().setSigningKey(jwtProperties.secret).parseClaimsJws(token).body
        } catch (e: Exception) {
            println(e.message + " => " + e)
            null
        }
    }

}