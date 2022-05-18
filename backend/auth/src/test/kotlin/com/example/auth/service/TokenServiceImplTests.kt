package com.example.auth.service

import com.example.auth.error.UnauthorizedException
import com.example.auth.model.*
import com.example.auth.service.impl.JwtUtil
import com.example.auth.service.impl.TokenServiceImpl
import com.nhaarman.mockito_kotlin.argumentCaptor
import io.jsonwebtoken.Jwts
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.time.Instant
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.assertThrows
import java.util.*


class TokenServiceImplTests {
    @Test
    fun createTokenPair_NoTokenFamilyProvided_TokenPairWithNewTokenFamily() {
        // given
        val expectedTokenPair = TokenPair(
            Token("access.token", Instant.now()),
            Token("refresh.token", Instant.now()))

        val jwtUtilMock = Mockito.mock(JwtUtil::class.java)
        Mockito.`when`(jwtUtilMock.generateTokenPair(Mockito.anyString(), Mockito.anyString()))
            .then { expectedTokenPair }

        val tokenFamilyServiceMock = Mockito.mock(TokenFamilyService::class.java)
        val sut = TokenServiceImpl(tokenFamilyServiceMock, jwtUtilMock)

        // when
        val actualTokenPair = sut.createTokenPair(
            Passenger("test.user", "test.user@example.com", "password"))

        // then
        assertThat(actualTokenPair, equalTo(expectedTokenPair))

        argumentCaptor<TokenFamily>().apply {
            Mockito.verify(tokenFamilyServiceMock).save(capture())
            assertThat(firstValue.validToken, equalTo(expectedTokenPair.refreshToken.token))
        }
    }

    @Test
    fun createTokenPair_TokenFamilyProvided_TokenPairWithExistingTokenFamily() {
        // given
        val expectedTokenPair = TokenPair(
            Token("access.token", Instant.now()),
            Token("refresh.token", Instant.now()))
        val tokenFamily = TokenFamily()

        val jwtUtilMock = Mockito.mock(JwtUtil::class.java)
        Mockito.`when`(jwtUtilMock.generateTokenPair(Mockito.anyString(), Mockito.anyString()))
            .then { expectedTokenPair }

        val tokenFamilyServiceMock = Mockito.mock(TokenFamilyService::class.java)
        val sut = TokenServiceImpl(tokenFamilyServiceMock, jwtUtilMock)

        // when
        val actualTokenPair = sut.createTokenPair(
            Passenger("test.user", "test.user@example.com", "password"),
            tokenFamily)

        // then
        assertThat(actualTokenPair, equalTo(expectedTokenPair))

        argumentCaptor<TokenFamily>().apply {
            Mockito.verify(tokenFamilyServiceMock).save(capture())
            assertThat(firstValue, equalTo(tokenFamily))
        }
    }

    @Test
    fun createTokenPairUsingRefreshToken_MalformedToken_IllegalArgumentException() {
        // given
        val jwtUtilMock = Mockito.mock(JwtUtil::class.java)
        Mockito.`when`(jwtUtilMock.getClaims(Mockito.anyString()))
            .then { null }

        val tokenFamilyServiceMock = Mockito.mock(TokenFamilyService::class.java)
        val sut = TokenServiceImpl(tokenFamilyServiceMock, jwtUtilMock)

        // when
        val act = { sut.createTokenPairUsingRefreshToken("malformed.token") { username ->
            Passenger(username, "test.user@example.com", "password")
        } }

        // then
        val exception = assertThrows<java.lang.IllegalArgumentException> { act() }
        assertThat(exception.message, containsString("malformed"))
    }

    @Test
    fun createTokenPairUsingRefreshToken_TokenWithoutFamily_IllegalArgumentException() {
        // given
        val jwtUtilMock = Mockito.mock(JwtUtil::class.java)
        Mockito.`when`(jwtUtilMock.getClaims(Mockito.anyString()))
            .then { Jwts.claims() }

        val tokenFamilyServiceMock = Mockito.mock(TokenFamilyService::class.java)
        val sut = TokenServiceImpl(tokenFamilyServiceMock, jwtUtilMock)

        // when
        val act = { sut.createTokenPairUsingRefreshToken("token.without.family") { username ->
            Passenger(username, "test.user@example.com", "password")
        } }

        // then
        val exception = assertThrows<java.lang.IllegalArgumentException> { act() }
        assertThat(exception.message, stringContainsInOrder("must", "token", "family"))
    }

    @Test
    fun createTokenPairUsingRefreshToken_TokenWithNonExistentFamily_IllegalArgumentException() {
        // given
        val nonExistentTokenFamilyId = "NonExistentFamilyId"
        val jwtUtilMock = Mockito.mock(JwtUtil::class.java)
        Mockito.`when`(jwtUtilMock.getClaims(Mockito.anyString()))
            .then { Jwts.claims().apply { set("fid", nonExistentTokenFamilyId) } }

        val tokenFamilyServiceMock = Mockito.mock(TokenFamilyService::class.java)
        Mockito.`when`(tokenFamilyServiceMock.getById(nonExistentTokenFamilyId))
            .then { null }

        val sut = TokenServiceImpl(tokenFamilyServiceMock, jwtUtilMock)

        // when
        val act = { sut.createTokenPairUsingRefreshToken("token") { username ->
            Passenger(username, "test.user@example.com", "password")
        } }

        // then
        val exception = assertThrows<java.lang.IllegalArgumentException> { act() }
        assertThat(exception.message?.lowercase(), stringContainsInOrder("token", "family", "not", "exist"))
    }

    @Test
    fun createTokenPairUsingRefreshToken_TokenFamilyIsInvalidated_UnauthorizedException() {
        // given
        val tokenFamily = TokenFamily().apply { isInvalidated = true }
        val jwtUtilMock = Mockito.mock(JwtUtil::class.java)
        Mockito.`when`(jwtUtilMock.getClaims(Mockito.anyString()))
            .then { Jwts.claims().apply { set("fid", tokenFamily.id) } }

        val tokenFamilyServiceMock = Mockito.mock(TokenFamilyService::class.java)
        Mockito.`when`(tokenFamilyServiceMock.getById(tokenFamily.id))
            .then { tokenFamily }

        val sut = TokenServiceImpl(tokenFamilyServiceMock, jwtUtilMock)

        // when
        val act = { sut.createTokenPairUsingRefreshToken("token") { username ->
            Passenger(username, "test.user@example.com", "password")
        } }

        // then
        val exception = assertThrows<UnauthorizedException> { act() }
        assertThat(exception.message?.lowercase(), stringContainsInOrder("token", "family", "invalidated"))
    }

    @Test
    fun createTokenPairUsingRefreshToken_TokenIsExpired_UnauthorizedException() {
        // given
        val tokenFamily = TokenFamily()
        val jwtUtilMock = Mockito.mock(JwtUtil::class.java)
        Mockito.`when`(jwtUtilMock.getClaims(Mockito.anyString()))
            .then { Jwts.claims()
                .setExpiration(Date.from(Instant.now().minusMillis(100)))
                .apply { set("fid", tokenFamily.id) }
            }

        val tokenFamilyServiceMock = Mockito.mock(TokenFamilyService::class.java)
        Mockito.`when`(tokenFamilyServiceMock.getById(tokenFamily.id))
            .then { tokenFamily }

        val sut = TokenServiceImpl(tokenFamilyServiceMock, jwtUtilMock)

        // when
        val act = { sut.createTokenPairUsingRefreshToken("token") { username ->
            Passenger(username, "test.user@example.com", "password")
        } }

        // then
        val exception = assertThrows<UnauthorizedException> { act() }
        assertThat(exception.message?.lowercase(), stringContainsInOrder("token", "is", "expired"))
        Mockito.verify(tokenFamilyServiceMock).invalidate(tokenFamily)
    }

    @Test
    fun createTokenPairUsingRefreshToken_TokenIsUsed_UnauthorizedException() {
        // given
        val tokenFamily = TokenFamily().apply { validToken = "fresh.token" }

        val jwtUtilMock = Mockito.mock(JwtUtil::class.java)
        Mockito.`when`(jwtUtilMock.getClaims(Mockito.anyString()))
            .then { Jwts.claims()
                .setExpiration(Date.from(Instant.now().plusSeconds(5)))
                .apply { set("fid", tokenFamily.id) }
            }

        val tokenFamilyServiceMock = Mockito.mock(TokenFamilyService::class.java)
        Mockito.`when`(tokenFamilyServiceMock.getById(tokenFamily.id))
            .then { tokenFamily }

        val sut = TokenServiceImpl(tokenFamilyServiceMock, jwtUtilMock)

        // when
        val act = { sut.createTokenPairUsingRefreshToken("used.token") { username ->
            Passenger(username, "test.user@example.com", "password")
        } }

        // then
        val exception = assertThrows<UnauthorizedException> { act() }
        assertThat(exception.message?.lowercase(), stringContainsInOrder("token", "used"))
        Mockito.verify(tokenFamilyServiceMock).invalidate(tokenFamily)
    }

    @Test
    fun createTokenPairUsingRefreshToken_ValidToken_TokenPairWithExistingFamily() {
        // given
        val expectedTokenPair = TokenPair(
            Token("access.token", Instant.now()),
            Token("refresh.token", Instant.now()))
        val validToken = "valid.token"
        val tokenFamily = TokenFamily().apply { this.validToken = validToken }

        val jwtUtilMock = Mockito.mock(JwtUtil::class.java)
        Mockito.`when`(jwtUtilMock.getClaims(Mockito.anyString()))
            .then { Jwts.claims()
                .setExpiration(Date.from(Instant.now().plusSeconds(5)))
                .setSubject("test.user")
                .apply { set("fid", tokenFamily.id) }
            }
        Mockito.`when`(jwtUtilMock.generateTokenPair(Mockito.anyString(), Mockito.anyString()))
            .then { expectedTokenPair }


        val tokenFamilyServiceMock = Mockito.mock(TokenFamilyService::class.java)
        Mockito.`when`(tokenFamilyServiceMock.getById(tokenFamily.id))
            .then { tokenFamily }

        lateinit var expectedUser: ETicketUser

        val sut = TokenServiceImpl(tokenFamilyServiceMock, jwtUtilMock)

        // when
        val tokenPair = sut.createTokenPairUsingRefreshToken(validToken) { username ->
            Passenger(username, "test.user@example.com", "password").apply { expectedUser = this }
        }

        // then
        assertThat(tokenPair, equalTo(expectedTokenPair))
        assertThat(tokenFamily.validToken, equalTo(expectedTokenPair.refreshToken.token))
        assertThat(expectedUser.username, equalTo("test.user"))
        Mockito.verify(tokenFamilyServiceMock).save(tokenFamily)
    }

}