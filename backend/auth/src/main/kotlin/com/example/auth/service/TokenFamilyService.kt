package com.example.auth.service

import com.example.auth.model.TokenFamily
import javax.persistence.EntityNotFoundException

interface TokenFamilyService {

    @Throws(EntityNotFoundException::class)
    fun getById(id: String): TokenFamily

    @Throws(IllegalArgumentException::class)
    fun save(tokenFamily: TokenFamily)

    fun invalidate(tokenFamily: TokenFamily)
    fun tryInvalidate(id: String)

}