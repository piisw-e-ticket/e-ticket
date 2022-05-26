package com.example.auth.infrastructure

import com.example.auth.model.Role
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import org.springframework.http.server.reactive.ServerHttpRequest

@Aspect
@Component
class RequiredRoleProcessor {

    @Around("@annotation(com.example.auth.infrastructure.RequiredRole)")
    fun process(call: ProceedingJoinPoint) : Mono<*> {
        return Mono.deferContextual { ctx ->
            val request = ctx[ServerHttpRequest::class.java]
            val annotation = (call.signature as MethodSignature).method.getAnnotation(RequiredRole::class.java)
            val res = request.headers["user-role"]?.first()?.runCatching { Role.valueOf(this) }

            if (res?.isSuccess == true && annotation.roles.contains(res.getOrThrow()))
                call.proceed() as Mono<*>
            else
                Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).build<Any>())
        }
    }

}
