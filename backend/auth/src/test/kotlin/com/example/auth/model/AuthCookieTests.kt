package com.example.auth.model

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import java.time.Instant

class AuthCookieTests {
    @Test
    fun asHeaderValue_AllAttributesIncluded_ValidCookieHeaderValue() {
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
        assertThat(expectedHeaderValue.split(";").map { it.trim() }, containsInAnyOrder(
            "${cookie.name}=${cookie.accessToken.token}",
            "Secure", "HttpOnly", "SameSite=Lax", "Path=/path", "Max-Age=1")
        )
    }

    @Test
    fun asHeaderValue_BooleanAttributesAreNotIncluded_ValidCookieHeaderValue() {
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
        assertThat(expectedHeaderValue.contains("Secure"), equalTo(false))
        assertThat(expectedHeaderValue.contains("HttpOnly"), equalTo(false))
    }
}