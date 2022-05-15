package com.example.auth.service

import com.example.auth.dto.LoginDto
import com.example.auth.dto.RegisterDto
import com.example.auth.model.User

interface UserService {

    fun getUserByUsername(username: String): User
    fun userExistsByUsername(username: String): Boolean
    fun userExistsByEmail(email: String): Boolean
    fun registerUser(registerDto: RegisterDto): User
    fun authenticateUser(loginDto: LoginDto): User

}