package com.example.gateway.config

import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.time.Duration

@Component
class ExtendAuthCookieValidityFilter: GatewayFilter {

    companion object {
        const val AUTH_COOKIE_NAME = "AuthCookie"
    }

    override fun filter(exchange: ServerWebExchange?, chain: GatewayFilterChain?): Mono<Void> {
        return chain!!.filter(exchange)
            .then(Mono.fromRunnable {extendAuthCookieValidity(exchange!!)})
    }

    private fun extendAuthCookieValidity(exchange: ServerWebExchange) {
        val authCookie = exchange.request.cookies.getFirst(AUTH_COOKIE_NAME) ?: return

        // If cookie has been set by the proxied service, leave it as it is
        if (exchange.response.cookies.contains(AUTH_COOKIE_NAME))
            return

        // TODO: Create AuthCookieBuilder shared by both gateway and auth services
        val refreshedCookie = ResponseCookie.from(AUTH_COOKIE_NAME, authCookie.value)
            .maxAge(Duration.ofMinutes(15))
            .httpOnly(true)
            .sameSite("strict")
            .build()

        exchange.response.cookies.add("AuthCookie", refreshedCookie)
    }
}