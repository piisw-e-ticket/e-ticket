package com.example.auth.service

import com.example.auth.dto.LoginDto
import com.example.auth.dto.RegisterDto
import com.example.auth.model.ETicketUser

interface UserService {

    fun getUserByUsername(username: String): ETicketUser
    fun userExistsByUsername(username: String): Boolean
    fun userExistsByEmail(email: String): Boolean
    fun registerUser(registerDto: RegisterDto): ETicketUser
    fun authenticateUser(loginDto: LoginDto): ETicketUser

}