package com.example.auth.service

import com.example.auth.model.TokenFamily
import com.example.auth.model.TokenPair
import com.example.auth.model.User

interface TokenService {
    fun createTokenPair(user: User, tokenFamily: TokenFamily? = null): TokenPair
    fun createTokenPairUsingRefreshToken(refreshToken: String, provideUser: (String) -> User): TokenPair
}
