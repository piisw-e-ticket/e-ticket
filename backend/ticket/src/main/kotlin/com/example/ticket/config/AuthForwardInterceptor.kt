package com.example.ticket.config

import feign.RequestInterceptor
import feign.RequestTemplate
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes


@Component
class AuthForwardInterceptor: RequestInterceptor {

    override fun apply(template: RequestTemplate) {
        val request = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes?)!!.request
        template.header(HttpHeaders.AUTHORIZATION, request.getHeader(HttpHeaders.AUTHORIZATION))
    }

}