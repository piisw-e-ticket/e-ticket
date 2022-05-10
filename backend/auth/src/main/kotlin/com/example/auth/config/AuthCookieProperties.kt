package com.example.auth.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix="auth.cookie")
class AuthCookieProperties(
    var name: String = "AuthCookie",
    var durationInMinutes: Long = 15,
    var secure: Boolean = true,
    var sameSite: SameSite = SameSite.Strict,
    var httpOnly: Boolean = true,
    var path: String = "/",
)

enum class SameSite { Strict, Lax, None }
