package com.example.ticket.infrastructure

import com.example.ticket.model.Role

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequiredRole(vararg val roles: Role)
