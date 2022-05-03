package com.example.auth.repository

import com.example.auth.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<User, Long> {

    fun findByUsername(username: String): User?
    fun existsByUsername(username: String): Boolean

}