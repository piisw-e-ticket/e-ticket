package com.example.ticket.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.servers.Server
import org.springdoc.core.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SpringDocConfig {

    @Bean
    fun ticketsApi(): GroupedOpenApi =
        GroupedOpenApi.builder()
            .group("tickets")
            .pathsToMatch("/tickets/**")
            .build()


    @Bean
    fun ticketsOpenAPI(): OpenAPI = OpenAPI()
        .info(
            Info()
                .title("Tickets Microservice API")
                .description("Microservice for buying, punching and validating tickets.")
                .version("v0.0.1")
        )
        .servers(
            listOf(Server().url("http://localhost:8080"))
        )

}