package com.example.auth.service

import com.example.auth.model.TokenFamily

interface TokenFamilyService {
    fun getById(id: String): TokenFamily
    fun save(tokenFamily: TokenFamily)
    fun invalidate(tokenFamily: TokenFamily)
}