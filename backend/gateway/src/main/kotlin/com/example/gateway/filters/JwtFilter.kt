package com.example.gateway.filters

import com.example.gateway.service.JwtUtil
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono


@Component
class JwtFilter(
    val jwtUtil: JwtUtil
): GatewayFilter {

    private val allowedEndpoints = listOf("/register", "/login", "/refresh", "/v3/api-docs")

    override fun filter(exchange: ServerWebExchange?, chain: GatewayFilterChain?): Mono<Void> {
        val request: ServerHttpRequest = exchange!!.request
        if (!isRequestSecured(request))
            return chain!!.filter(exchange)

        val token = getTokenFromRequest(request)
            ?: return exchange.response.apply { statusCode = HttpStatus.UNAUTHORIZED }.setComplete()

        val claims = jwtUtil.getClaims(token)
            ?: return exchange.response.apply { statusCode = HttpStatus.UNAUTHORIZED }.setComplete()

        exchange.request.mutate().header("username", claims.subject).build()
        exchange.request.mutate().header(
            "user-role", claims.getOrDefault("role", "").toString())
            .build()

        return chain!!.filter(exchange)
    }

    private fun isRequestSecured(request: ServerHttpRequest) = allowedEndpoints.stream()
        .noneMatch { uri: String? -> request.uri.path.contains(uri!!) }

    private fun getTokenFromRequest(request: ServerHttpRequest): String? = when {
        request.cookies.containsKey("AuthCookie") ->
            request.cookies["AuthCookie"]!!.first().value
        request.headers.containsKey("Authorization") ->
            parseTokenFromHeader(request.headers["Authorization"]!!.first())
        else -> null
    }

    private fun parseTokenFromHeader(header: String): String? {
        return if (header.lowercase().startsWith("bearer"))
            header.substring("bearer".length).trim()
        else null
    }
}