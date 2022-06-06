package com.example.auth.controller

import com.example.auth.service.ExpiredTokenException
import com.example.auth.service.MalformedTokenException
import com.example.auth.service.RefreshTokenReuseAttemptException
import org.apache.http.auth.InvalidCredentialsException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class RestResponseEntityExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(value = [Exception::class])
    protected fun handleError(
        e: Exception
    ): ResponseEntity<Any> = when(e) {
        is IllegalArgumentException,
        is InvalidCredentialsException,
        is MalformedTokenException -> ResponseEntity.badRequest().body(e.message)
        is ExpiredTokenException,
        is RefreshTokenReuseAttemptException -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.message)
        is WebExchangeBindException -> handleMethodArgumentNotValid(e)
        else -> ResponseEntity.internalServerError().body(e.message)
    }

    fun handleMethodArgumentNotValid(ex: WebExchangeBindException): ResponseEntity<Any> {
        val errors: MutableMap<String, String?> = HashMap()
        ex.bindingResult.allErrors.forEach { error ->
            val fieldName = (error as FieldError).field
            val errorMessage = error.defaultMessage
            errors[fieldName] = errorMessage
        }
        return ResponseEntity.badRequest().body(errors)
    }

}