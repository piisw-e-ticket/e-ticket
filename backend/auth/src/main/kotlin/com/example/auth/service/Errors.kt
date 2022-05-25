package com.example.auth.service

import io.jsonwebtoken.Claims

class MalformedTokenException(message: String): Exception(message)
class ExpiredTokenException(message: String, val claims: Claims? = null): Exception(message)
class RefreshTokenReuseAttemptException(message: String): Exception(message)
