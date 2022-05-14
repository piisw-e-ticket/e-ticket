package com.example.auth.repository

import com.example.auth.model.TokenFamily
import org.springframework.data.jpa.repository.JpaRepository

interface TokenFamilyRepository: JpaRepository<TokenFamily, String> {}