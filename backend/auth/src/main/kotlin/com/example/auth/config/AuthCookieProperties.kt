package com.example.auth.config

import com.example.auth.model.SameSite
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix="auth.cookie")
class AuthCookieProperties(
    var name: String = "AuthCookie",
    var secure: Boolean = false,
    var sameSite: SameSite = SameSite.Strict,
    var httpOnly: Boolean = true,
    var path: String = "/",
)
