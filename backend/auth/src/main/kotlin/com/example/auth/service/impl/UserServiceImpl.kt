package com.example.auth.service.impl

import com.example.auth.dto.LoginDto
import com.example.auth.dto.RegisterDto
import com.example.auth.model.Role
import com.example.auth.model.User
import com.example.auth.repository.UserRepository
import com.example.auth.service.UserService
import org.apache.http.auth.InvalidCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import javax.persistence.EntityNotFoundException

@Service
class UserServiceImpl(
        val userRepository: UserRepository,
        val encoder: PasswordEncoder
): UserService {

    override fun getUserByUsername(username: String): User =
            userRepository.findByUsername(username).takeIf { it != null }
                    ?: throw EntityNotFoundException("Could not find user with username '$username'")

    override fun userExistsByUsername(username: String): Boolean = userRepository.existsByUsername(username)

    override fun userExistsByEmail(email: String): Boolean = userRepository.existsByEmail(email)

    override fun registerUser(registerDto: RegisterDto): User {
        if (userExistsByUsername(registerDto.username))
            throw IllegalArgumentException("User with username '${registerDto.username}' already exists.")

        if (userExistsByEmail(registerDto.email))
            throw IllegalArgumentException("User with email '${registerDto.email}' already exists.")

        val userAccount = User(
                registerDto.username,
                registerDto.email,
                encoder.encode(registerDto.password)
        )
        userAccount.roles.add(Role.PASSENGER)
        return userRepository.save(userAccount)
    }

    override fun authenticateUser(loginDto: LoginDto): User {
        val user = getUserByUsername(loginDto.username)
        return if(encoder.matches(loginDto.password, user.password)) user
        else throw InvalidCredentialsException("Could not authenticate user '${loginDto.username}'.")
    }


}