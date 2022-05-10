package com.example.auth.controller

import org.apache.http.auth.InvalidCredentialsException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler


@ControllerAdvice
class RestResponseEntityExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(value = [InvalidCredentialsException::class])
    protected fun handleInvalidCredentialsError(
        e: InvalidCredentialsException,
        request: WebRequest
    ): ResponseEntity<Any> = ResponseEntity.badRequest().body(e.message)

}