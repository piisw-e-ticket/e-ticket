package com.example.gateway.config

import com.example.gateway.filters.ExtendAuthCookieValidityFilter
import com.example.gateway.filters.JwtFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.context.annotation.Configuration
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder

import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec
import org.springframework.context.annotation.Bean
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono


@Configuration
class GatewayConfig {
    @Autowired
    private lateinit var filter: JwtFilter
    @Autowired
    private lateinit var extendAuthCookieValidityFilter: ExtendAuthCookieValidityFilter

    @Bean
    fun extendAuthCookieValidityGlobalFilter() = GlobalFilter { exchange, chain ->
        extendAuthCookieValidityFilter.filter(exchange, chain)
    }

    @Bean
    fun routes(builder: RouteLocatorBuilder): RouteLocator? {
        return builder.routes().route("auth") { p ->
            p.path("/auth/**").filters { f: GatewayFilterSpec -> f.filter(filter) }.uri("lb://auth")
        }.route("app") { p ->
            p.path("/**").uri("lb://webserver")
        }.build()
    }
}