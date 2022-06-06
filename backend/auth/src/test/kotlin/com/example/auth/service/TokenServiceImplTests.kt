package com.example.auth.service

import com.example.auth.model.ETicketUser
import com.example.auth.model.TokenFamily
import com.example.auth.model.TokenPair
import com.example.auth.service.impl.JwtUtil
import com.example.auth.service.impl.TokenServiceImpl
import io.jsonwebtoken.Claims
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.*
import javax.persistence.EntityNotFoundException

class TokenServiceImplTests {

    @MockK
    private lateinit var tokenFamilyService: TokenFamilyService

    @MockK
    private lateinit var jwtUtil: JwtUtil

    @SpyK
    @InjectMockKs
    private lateinit var tokenService: TokenServiceImpl

    @BeforeEach
    fun composeMocks() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `createTokenPair returns token pair and creates new family on no family provided`() {
        // given
        val expectedTokenPair = mockk<TokenPair> {
            every { refreshToken } returns mockk {
                every { token } returns "refresh.token"
            }
        }
        val mockUser = mockk<ETicketUser> {
            every { username } returns "username"
        }
        val captor = CapturingSlot<TokenFamily>()
        every { jwtUtil.generateTokenPair(any(), any()) } returns expectedTokenPair
        every { tokenFamilyService.save(capture(captor)) } returnsArgument 0

        // when
        val actualTokenPair = tokenService.createTokenPair(mockUser)

        // then
        assertAll(
                { assertThat(captor.captured.validToken).isEqualTo(expectedTokenPair.refreshToken.token) },
                { assertThat(actualTokenPair).isEqualTo(expectedTokenPair) }
        )
    }

    @Test
    fun `createTokenPair returns token pair and uses family provided`() {
        // given
        val expectedTokenPair = mockk<TokenPair> {
            every { refreshToken } returns mockk {
                every { token } returns "refresh.token"
            }
        }
        val tokenFamily = TokenFamily()
        val mockUser = mockk<ETicketUser> {
            every { username } returns "username"
        }
        val captor = CapturingSlot<TokenFamily>()
        every { jwtUtil.generateTokenPair(any(), any()) } returns expectedTokenPair
        every { tokenFamilyService.save(capture(captor)) } returnsArgument 0

        // when
        val actualTokenPair = tokenService.createTokenPair(mockUser, tokenFamily)

        // then
        assertAll(
                { assertThat(captor.captured).isEqualTo(tokenFamily) },
                { assertThat(actualTokenPair).isEqualTo(expectedTokenPair) }
        )
    }

    @Test
    fun `createTokenPairUsingRefreshToken throws an exception on malformed token`() {
        // given
        every { jwtUtil.getClaims(any()) } throws MalformedTokenException("Refresh token is malformed")

        // when
        val act = {
            tokenService.createTokenPairUsingRefreshToken("malformed.token") { _ -> mockk() }
        }

        // then
        val exception = assertThrows(MalformedTokenException::class.java) { act() }
        assertThat(exception.message).isEqualTo("Refresh token is malformed")
    }

    @Test
    fun `createTokenPairUsingRefreshToken throws an exception on token without a family`() {
        // given
        val claimsMock = mockk<Claims> {
            every { getOrDefault("fid", null) } returns null
        }
        val expectedException = MalformedTokenException("Refresh token must come from a token family")
        every { jwtUtil.getClaims(any()) } returns claimsMock

        // when
        val act = { tokenService.createTokenPairUsingRefreshToken("token.without.family") { _ -> mockk() } }

        // then
        val actualException = assertThrows(MalformedTokenException::class.java) { act() }
        assertThat(actualException.message).isEqualTo(expectedException.message)
    }

    @Test
    fun `createTokenPairUsingRefreshToken throws an exception on non-existing family`() {
        // given
        val claimsMock = mockk<Claims> {
            every { getOrDefault("fid", null) } returns "family.id"
        }
        val expectedException = EntityNotFoundException("Could not find token family with id 'family.id'")
        every { jwtUtil.getClaims(any()) } returns claimsMock
        every { tokenFamilyService.getById(any()) } throws expectedException

        // when
        val act = { tokenService.createTokenPairUsingRefreshToken("token.with.invalid.family") { _ -> mockk() } }

        // then
        val actualException = assertThrows(EntityNotFoundException::class.java) { act() }
        assertThat(actualException.message).isEqualTo(expectedException.message)
    }


    @Test
    fun `createTokenPairUsingRefreshToken throws exception on invalidated family`() {
        // given
        val tokenFamily = TokenFamily().apply {
            id = "family.id"
            isInvalidated = true
        }
        val mockClaims = mockk<Claims> {
            every { getOrDefault("fid", null) } returns "family.id"
        }
        val expectedException = RefreshTokenReuseAttemptException("Token family is invalidated")
        every { jwtUtil.getClaims(any()) } returns mockClaims
        every { tokenFamilyService.getById("family.id") } returns tokenFamily

        // when
        val act = { tokenService.createTokenPairUsingRefreshToken("token.with.invalidated.family") { _ -> mockk() } }

        // then
        val actualException = assertThrows(RefreshTokenReuseAttemptException::class.java) { act() }
        assertThat(actualException.message).isEqualTo(expectedException.message)
    }

    @Test
    fun `createTokenPairUsingRefreshToken invalidates family and throws exception on expired token`() {
        // given
        val tokenFamily = TokenFamily()
        val mockClaims = mockk<Claims> {
            every { getOrDefault("fid", null) } returns "family.id"
        }
        val expectedException = ExpiredTokenException("Token is expired")
        every { jwtUtil.getClaims(any()) } throws ExpiredTokenException("Token is expired", mockClaims)
        every { tokenFamilyService.getById(any()) } returns tokenFamily
        every { tokenFamilyService.tryInvalidate("family.id") } just runs

        // when
        val act = { tokenService.createTokenPairUsingRefreshToken("expired.token") { _ -> mockk() } }

        // then
        val actualException = assertThrows(ExpiredTokenException::class.java) { act() }
        assertThat(actualException.message).isEqualTo(expectedException.message)
    }

    @Test
    fun `createTokenPairUsingRefreshToken invalidates family and throws exception on used token`() {
        // given
        val tokenFamily = TokenFamily().apply { validToken = "fresh.token" }
        val mockClaims = mockk<Claims> {
            every { getOrDefault("fid", null) } returns "family.id"
            every { expiration } returns Date.from(Instant.now().plusSeconds(100))
        }
        val expectedException = RefreshTokenReuseAttemptException("Refresh token has already been used")
        every { jwtUtil.getClaims(any()) } returns mockClaims
        every { tokenFamilyService.getById(any()) } returns tokenFamily
        every { tokenFamilyService.invalidate(any()) } just runs

        // when
        val act = { tokenService.createTokenPairUsingRefreshToken("used.token") { _ -> mockk() } }

        // then
        val actualException = assertThrows(RefreshTokenReuseAttemptException::class.java) { act() }
        assertThat(actualException.message).isEqualTo(expectedException.message)
    }

    @Test
    fun `createTokenPairUsingRefresh returns token pair on valid parameters`() {
        // given
        val validToken = "valid.token"
        val expectedTokenPair = mockk<TokenPair> {
            every { refreshToken } returns mockk {
                every { token } returns validToken
            }
        }
        val tokenFamily = TokenFamily().apply { this.validToken = validToken }
        val mockClaims = mockk<Claims> {
            every { getOrDefault("fid", null) } returns "family.id"
            every { expiration } returns Date.from(Instant.now().plusSeconds(100))
            every { subject } returns "test.user"
        }
        val mockUser = mockk<ETicketUser>()
        every { jwtUtil.getClaims(any()) } returns mockClaims
        every { jwtUtil.generateTokenPair(any(), any()) } returns expectedTokenPair
        every { tokenFamilyService.getById(any()) } returns tokenFamily
        every { tokenService.createTokenPair(mockUser, tokenFamily) } returns expectedTokenPair

        // when
        val tokenPair = tokenService.createTokenPairUsingRefreshToken(validToken) { _ -> mockUser }

        // then
        assertAll(
                { assertThat(tokenPair).isEqualTo(expectedTokenPair) },
                { assertThat(tokenFamily.validToken).isEqualTo(expectedTokenPair.refreshToken.token) },
        )
    }

}