package com.example.auth.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.servers.Server
import org.springdoc.core.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SpringDocConfig {

    @Bean
    fun authApi(): GroupedOpenApi =
        GroupedOpenApi.builder()
            .group("auth")
            .pathsToMatch("/auth/**")
            .build()


    @Bean
    fun authOpenApi(): OpenAPI = OpenAPI()
        .info(
            Info()
                .title("Auth Microservice API")
                .description("Microservice for user-related operations")
                .version("v0.0.1")
        )
        .servers(
            listOf(Server().url("http://localhost:8080"))
        )

}