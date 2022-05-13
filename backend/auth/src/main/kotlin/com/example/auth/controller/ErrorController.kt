package com.example.auth.controller

import org.apache.http.auth.InvalidCredentialsException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler


@ControllerAdvice
class RestResponseEntityExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(value = [Exception::class])
    protected fun handleError(
        e: Exception
    ): ResponseEntity<Any> = when(e) {
        is InvalidCredentialsException -> ResponseEntity.badRequest().body(e.message)
        is IllegalArgumentException -> ResponseEntity.badRequest().body(e.message)
        is AccessDeniedException -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.message)
        else -> ResponseEntity.internalServerError().body(e.message)
    }

}