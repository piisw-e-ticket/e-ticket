package com.example.gateway.config.swagger

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import springfox.documentation.swagger.web.InMemorySwaggerResourcesProvider
import springfox.documentation.swagger.web.SwaggerResource
import springfox.documentation.swagger.web.SwaggerResourcesProvider


@Configuration
class SwaggerConfig(
    private val servicesRegistry: ServicesRegistry
) {

    @Primary
    @Bean
    fun swaggerResource(provider: InMemorySwaggerResourcesProvider): SwaggerResourcesProvider =
        SwaggerResourcesProvider {
            val resources: MutableList<SwaggerResource> = ArrayList(provider.get())
            resources.clear()
            resources.addAll(servicesRegistry.getSwaggerDefinitions())
            resources
        }
}