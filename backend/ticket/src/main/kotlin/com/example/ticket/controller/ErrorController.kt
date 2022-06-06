package com.example.ticket.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import javax.persistence.EntityNotFoundException

@ControllerAdvice
class RestResponseEntityExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(value = [Exception::class])
    protected fun handleError(
        e: Exception
    ): ResponseEntity<Any> = when(e) {
        is EntityNotFoundException -> ResponseEntity.badRequest().body(e.message)
        is IllegalArgumentException -> ResponseEntity.badRequest().body(e.message)
        is AccessDeniedException -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.message)
        else -> ResponseEntity.internalServerError().body(e.message)
    }

}