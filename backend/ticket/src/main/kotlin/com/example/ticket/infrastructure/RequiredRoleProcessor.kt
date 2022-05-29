package com.example.ticket.infrastructure

import com.example.ticket.model.Role
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@Aspect
@Component
class RequiredRoleProcessor {

    @Around("@annotation(com.example.ticket.infrastructure.RequiredRole)")
    fun process(call: ProceedingJoinPoint) : ResponseEntity<*> {
        val request = (RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes).request
        val annotation = (call.signature as MethodSignature).method.getAnnotation(RequiredRole::class.java)
        val res = request.getHeader("user-role")?.runCatching { Role.valueOf(this) }

        return if (res?.isSuccess == true && annotation.roles.contains(res.getOrThrow()))
            call.proceed() as ResponseEntity<*>
        else
            ResponseEntity.status(HttpStatus.FORBIDDEN).build<Any>()
    }

}
