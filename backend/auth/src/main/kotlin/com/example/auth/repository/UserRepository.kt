package com.example.auth.repository

import com.example.auth.model.ETicketUser
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<ETicketUser, Long> {

    fun findByUsername(username: String): ETicketUser?
    fun existsByUsername(username: String): Boolean
    fun existsByEmail(email: String): Boolean

}