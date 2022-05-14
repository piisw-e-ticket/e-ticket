package com.example.auth.model

import java.time.Instant

class Token(val token: String, val expiresAt: Instant)
class TokenPair(val accessToken: Token, val refreshToken: Token)
