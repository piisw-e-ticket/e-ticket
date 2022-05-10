package com.example.gateway.filters

import com.example.gateway.service.JwtUtil
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.function.Predicate


@Component
class JwtFilter(
        val jwtUtil: JwtUtil
): GatewayFilter {

    override fun filter(exchange: ServerWebExchange?, chain: GatewayFilterChain?): Mono<Void> {
        val request: ServerHttpRequest = exchange!!.request

        val apiEndpoints = listOf("/register", "/login")

        val isApiSecured: Predicate<ServerHttpRequest> = Predicate<ServerHttpRequest> { r ->
            apiEndpoints.stream()
                    .noneMatch { uri: String? -> r.uri.path.contains(uri!!) }
        }

        if (isApiSecured.test(request)) {
            val token = getTokenFromRequest(request)
                ?: return exchange.response.apply { statusCode = HttpStatus.UNAUTHORIZED }
                    .setComplete()

            try {
                jwtUtil.validateToken(token)
            } catch (e: Exception) {
                // e.printStackTrace();
                return exchange.response.apply { statusCode = HttpStatus.BAD_REQUEST }
                    .setComplete()
            }

            val claims = jwtUtil.getClaims(token)
            exchange.request.mutate().header("id", claims!!["id"].toString()).build()
        }

        return chain!!.filter(exchange)
    }

    private fun getTokenFromRequest(request: ServerHttpRequest): String? =
        when(true) {
            request.cookies.containsKey("AuthCookie") ->
                request.cookies["AuthCookie"]!!.first().value
            request.headers.containsKey("Authorization") ->
                parseTokenFromHeader(request.headers["Authorization"]!!.first())
            else -> null
        }

    private fun parseTokenFromHeader(header: String): String? {
        return if (header.lowercase().startsWith("bearer"))
            header.substring(0, "bearer".length).trim()
        else null
    }
}