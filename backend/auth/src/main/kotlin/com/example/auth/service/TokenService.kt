package com.example.auth.service

import com.example.auth.model.TokenFamily
import com.example.auth.model.TokenPair
import com.example.auth.model.ETicketUser

interface TokenService {
    fun createTokenPair(user: ETicketUser, tokenFamily: TokenFamily? = null): TokenPair
    fun createTokenPairUsingRefreshToken(refreshToken: String, provideUser: (String) -> ETicketUser): TokenPair
}
