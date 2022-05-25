package com.example.gateway.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix="jwt")
data class JwtProperties(
        var secret: String = ""
)
