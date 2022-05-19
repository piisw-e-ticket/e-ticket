package com.example.auth.service

import com.example.auth.dto.LoginDto
import com.example.auth.dto.RegisterDto
import com.example.auth.model.ETicketUser
import com.example.auth.model.Role
import com.example.auth.repository.UserRepository
import com.example.auth.service.impl.UserServiceImpl
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.apache.http.auth.InvalidCredentialsException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.security.crypto.password.PasswordEncoder
import javax.persistence.EntityNotFoundException

class UserServiceImplTests {

    @MockK
    private lateinit var userRepository: UserRepository
    @MockK
    private lateinit var encoder: PasswordEncoder
    private lateinit var userService: UserServiceImpl

    @BeforeEach
    fun composeMocks() {
        MockKAnnotations.init(this)
        userService = UserServiceImpl(userRepository, encoder)
    }

    @Test
    fun `getUserByUsername returns user on valid parameters`() {
        val expectedUser = mockk<ETicketUser> {
            every { username } returns "username"
        }
        every { userRepository.findByUsername(any()) } returns expectedUser

        val actualUser = userService.getUserByUsername(expectedUser.username)
        assertThat(actualUser).isEqualTo(expectedUser)
    }

    @Test
    fun `getUserByUsername throws an exception on non-existing user`() {
        every { userRepository.findByUsername(any()) } returns null

        val exception = assertThrows(EntityNotFoundException::class.java) {
            userService.getUserByUsername("username")
        }
        assertThat(exception.message).isEqualTo("Could not find user with username 'username'.")
    }

    @Test
    fun `registerUser returns passenger on valid parameters`() {
        val registerDto = RegisterDto(
                username = "username",
                password = "password",
                email = "mail@mail.com"
        )
        every { userRepository.existsByUsername(registerDto.username) } returns false
        every { userRepository.existsByEmail(registerDto.email) } returns false
        every { encoder.encode(registerDto.password) } returns "encodedPassword"
        every { userRepository.save(any()) } returnsArgument 0

        val actualUser = userService.registerUser(registerDto)
        assertAll(
            { assertThat(actualUser.username).isEqualTo(registerDto.username) },
            { assertThat(actualUser.password).isEqualTo("encodedPassword") },
            { assertThat(actualUser.email).isEqualTo(registerDto.email) },
            { assertThat(actualUser.active).isTrue },
            { assertThat(actualUser.role).isEqualTo(Role.PASSENGER) },
        )
    }

    @Test
    fun `registerUser throws an exception when username already exists`() {
        val registerDto = RegisterDto(
                username = "username",
                password = "password",
                email = "mail@mail.com"
        )
        every { userRepository.existsByUsername(registerDto.username) } returns true

        val exception = assertThrows(IllegalArgumentException::class.java) {
            userService.registerUser(registerDto)
        }
        assertThat(exception.message).isEqualTo("User with username '${registerDto.username}' already exists.")
    }

    @Test
    fun `registerUser throws an exception when email already exists`() {
        val registerDto = RegisterDto(
                username = "username",
                password = "password",
                email = "mail@mail.com"
        )
        every { userRepository.existsByUsername(registerDto.username) } returns false
        every { userRepository.existsByEmail(registerDto.email) } returns true

        val exception = assertThrows(IllegalArgumentException::class.java) {
            userService.registerUser(registerDto)
        }
        assertThat(exception.message).isEqualTo("User with email '${registerDto.email}' already exists.")
    }

    @Test
    fun `authenticateUser returns user on matching credentials`() {
        val loginDto = LoginDto(
                username = "username",
                password = "password"
        )
        val expectedUser = mockk<ETicketUser>() {
            every { password } returns "encodedPassword"
        }
        every { userRepository.findByUsername(loginDto.username) } returns expectedUser
        every { encoder.matches("password", "encodedPassword") } returns true

        val actualUser = userService.authenticateUser(loginDto)
        assertThat(actualUser).isEqualTo(expectedUser)
    }

    @Test
    fun `authenticateUser throws an exception on non-matching password`() {
        val loginDto = LoginDto(
                username = "username",
                password = "password"
        )
        val expectedUser = mockk<ETicketUser>() {
            every { password } returns "encodedPassword"
        }
        every { userRepository.findByUsername(loginDto.username) } returns expectedUser
        every { encoder.matches("password", "encodedPassword") } returns false

        val exception = assertThrows(InvalidCredentialsException::class.java) {
            userService.authenticateUser(loginDto)
        }
        assertThat(exception.message).isEqualTo("Could not authenticate user '${loginDto.username}'.")
    }

}