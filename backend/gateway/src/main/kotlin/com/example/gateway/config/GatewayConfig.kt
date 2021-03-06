package com.example.gateway.config

import com.example.gateway.filters.JwtFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec
import org.springframework.cloud.gateway.route.builder.PredicateSpec
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class GatewayConfig {

    @Autowired
    private lateinit var filter: JwtFilter

    @Bean
    fun routes(builder: RouteLocatorBuilder): RouteLocator? {
        return builder.routes()
                .route("auth") { p: PredicateSpec ->
                    p.path("/auth/**").filters { f: GatewayFilterSpec -> f.filter(filter) }.uri("lb://auth") }
                .route("ticket") { p: PredicateSpec ->
                    p.path("/tickets/**").filters { f: GatewayFilterSpec -> f.filter(filter) }.uri("lb://ticket") }
                .build()
    }

}