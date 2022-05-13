package com.example.auth.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix="jwt")
data class JwtProperties(
    var secret: String = "",
    var accessTokenMaxAgeInMinutes: Long = 0,
    var refreshTokenMaxAgeInMinutes: Long = 0,
)