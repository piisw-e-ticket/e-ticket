package com.example.auth.controller

import com.example.auth.dto.JwtTokenPairDto
import com.example.auth.dto.LoginDto
import com.example.auth.dto.RegisterDto
import com.example.auth.repository.TokenFamilyRepository
import com.example.auth.service.UserService
import org.apache.http.auth.InvalidCredentialsException
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.transaction.TestTransaction
import org.springframework.transaction.annotation.Transactional
import java.lang.Thread.sleep

@SpringBootTest
@Transactional
class AuthControllerTest {

    @Autowired
    lateinit var sut: AuthController
    @Autowired
    lateinit var userService: UserService
    @Autowired
    lateinit var tokenFamilyRepository: TokenFamilyRepository

    @Test
    fun refresh_ValidRefreshToken_NewTokenPair() {
        // given
        val tokenPairDto = register()

        // when
        sleep(1000)
        val response = sut.refresh("Bearer " + tokenPairDto.refreshToken, setCookie = true)

        // then
        assertThat(response.statusCode, equalTo(HttpStatus.OK))
        assertThat(response.body!!.refreshToken, not(tokenPairDto.refreshToken))
        assertThat(response.headers, hasKey("Set-Cookie"))
        assertThat(tokenFamilyRepository.findAll().size, equalTo(1))
        assertDoesNotThrow { userService.getUserByUsername("test.user") }
    }

    @Test
    fun login_InvalidPassword_BadRequest() {
        // given
        register()

        // when
        val act = { sut.login(LoginDto("test.user", "invalid.password")) }

        // then
        assertThrows<InvalidCredentialsException> { act() }
    }

    @Test
    fun login_ValidPassword_BadRequest() {
        // given
        register()

        // when
        val response =  sut.login(LoginDto("test.user", "password"))

        // then
        assertThat(response.statusCode, equalTo(HttpStatus.OK))
        TestTransaction.end()
    }

    private fun register(): JwtTokenPairDto {
        val response = sut.register(
            RegisterDto("test.user", "password", "test.user@example.com"))
        assertThat(response.statusCode, equalTo(HttpStatus.OK))
        return response.body!!
    }
}