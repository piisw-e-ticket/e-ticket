package com.example.auth.service.impl

import com.example.auth.model.TokenFamily
import com.example.auth.repository.TokenFamilyRepository
import com.example.auth.service.TokenFamilyService
import org.springframework.orm.ObjectRetrievalFailureException
import org.springframework.stereotype.Service

@Service
class TokenFamilyServiceImpl(
    val repository: TokenFamilyRepository
) : TokenFamilyService {

    override fun getById(id: String): TokenFamily? = try {
        repository.getById(id)
    } catch (e: ObjectRetrievalFailureException) {
        null
    }

    override fun save(tokenFamily: TokenFamily) {
        if (tokenFamily.validToken == null)
            throw IllegalArgumentException("Token family must have a valid token to be persisted")
        repository.save(tokenFamily)
    }

    override fun invalidate(tokenFamily: TokenFamily) {
        tokenFamily.isInvalidated = true
        repository.save(tokenFamily)
    }

}