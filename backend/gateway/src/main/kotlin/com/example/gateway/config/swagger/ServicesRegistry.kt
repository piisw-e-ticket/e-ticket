package com.example.gateway.config.swagger

import org.springframework.stereotype.Component
import springfox.documentation.swagger.web.SwaggerResource
import java.util.concurrent.ConcurrentHashMap
import java.util.stream.Collectors


@Component
class ServicesRegistry {

    companion object {
        private val SKIPPED_SERVICES = listOf("gateway")
    }

    private var swaggerResources: ConcurrentHashMap<String, String> = ConcurrentHashMap()

    fun addService(name: String, content: String) {
        swaggerResources[name] = content
    }

    fun getSwaggerDefinitions(): List<SwaggerResource> {
        return swaggerResources.entries.stream()
            .filter { !SKIPPED_SERVICES.contains(it.key) }
            .map { (serviceName): Map.Entry<String, String> ->
                val resource = SwaggerResource()
                resource.name = serviceName
                resource.url = "/services-docs/$serviceName"
                resource.swaggerVersion = "3.0"
                resource
            }.collect(Collectors.toList())
    }

    fun getServiceRegistry(name: String) = swaggerResources[name]

}