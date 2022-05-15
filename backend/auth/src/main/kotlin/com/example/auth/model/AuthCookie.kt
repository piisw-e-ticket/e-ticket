package com.example.auth.model

import java.time.Instant

class AuthCookie(
    val name: String = "AuthCookie",
    val accessToken: Token,
    val secure: Boolean = false,
    val sameSite: SameSite = SameSite.None,
    val httpOnly: Boolean = true,
    val path: String = "/",
) {
    fun asHeaderValue(): String {
        var value = "${name}=${accessToken.token}" +
                "; Max-Age=${accessToken.expiresAt.minusSeconds(Instant.now().epochSecond).epochSecond}" +
                "; Path=${path}" +
                "; SameSite=${sameSite.name}"

        if (secure)
            value += "; Secure"
        if (httpOnly)
            value += "; HttpOnly"

        return value
    }
}

enum class SameSite { Strict, Lax, None }
