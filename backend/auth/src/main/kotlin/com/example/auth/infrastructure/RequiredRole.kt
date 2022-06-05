package com.example.auth.infrastructure

import com.example.auth.model.Role


@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequiredRole(vararg val roles: Role)
