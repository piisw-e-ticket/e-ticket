package com.example.gateway.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder

import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec
import org.springframework.cloud.gateway.route.builder.PredicateSpec
import org.springframework.context.annotation.Bean


@Configuration
class GatewayConfig {

    @Autowired
    private lateinit var filter: JwtFilter

    @Bean
    fun routes(builder: RouteLocatorBuilder): RouteLocator? {
        return builder.routes().route("auth") { r: PredicateSpec -> r.path("/auth/**").filters { f: GatewayFilterSpec -> f.filter(filter) }.uri("lb://auth") }
//                .route("alert") { r: PredicateSpec -> r.path("/alert/**").filters { f: GatewayFilterSpec -> f.filter(filter) }.uri("lb://alert") }
//                .route("echo") { r: PredicateSpec -> r.path("/echo/**").filters { f: GatewayFilterSpec -> f.filter(filter) }.uri("lb://echo") }
//                .route("hello") { r: PredicateSpec -> r.path("/hello/**").filters { f: GatewayFilterSpec -> f.filter(filter) }.uri("lb://hello") }
                .build()
    }

}