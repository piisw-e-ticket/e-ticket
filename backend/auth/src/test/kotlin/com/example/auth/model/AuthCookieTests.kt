package com.example.auth.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant

class AuthCookieTests {
    @Test
    fun `asHeaderValue produces valid header on all attributes specified`() {
        // given
        val token = Token("token", Instant.now().plusSeconds(1))
        val cookie = AuthCookie(
            name = "Name",
            accessToken = token,
            secure = true,
            sameSite = SameSite.Lax,
            httpOnly = true,
            path = "/path"
        )

        // when
        val expectedHeaderValue = cookie.asHeaderValue()

        // then
        assertThat(expectedHeaderValue.split(";").map { it.trim() })
                .containsExactlyInAnyOrder("${cookie.name}=${cookie.accessToken.token}",
                        "Secure", "HttpOnly", "SameSite=Lax", "Path=/path", "Max-Age=1")
    }

    @Test
    fun `asHeaderValue produces correct header based on boolean attributes`() {
        // given
        val token = Token("token", Instant.now().plusSeconds(1))
        val cookie = AuthCookie(
            accessToken = token,
            secure = false,
            httpOnly = false
        )

        // when
        val expectedHeaderValue = cookie.asHeaderValue()

        // then
        assertThat(expectedHeaderValue.contains("Secure")).isFalse
        assertThat(expectedHeaderValue.contains("HttpOnly")).isFalse
    }
}