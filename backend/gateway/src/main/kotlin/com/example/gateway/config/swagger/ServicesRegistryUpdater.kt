package com.example.gateway.config.swagger

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.cloud.client.ServiceInstance
import org.springframework.cloud.client.discovery.DiscoveryClient
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate


@Component
class ServicesRegistryUpdater(
    private val discoveryClient: DiscoveryClient,
    private val servicesRegistry: ServicesRegistry
) {

    private var template: RestTemplate = RestTemplate()

    @Scheduled(fixedDelayString = "\${swagger.config.refreshRate}")
    fun refreshSwaggerRegistry() {
        discoveryClient.services.stream().forEach { serviceId ->
            val serviceInstances: List<ServiceInstance> = discoveryClient.getInstances(serviceId)
            if (serviceInstances.isEmpty()) {
                println("Swagger === No instances available for service '$serviceId'. ")
            } else {
                val instance = serviceInstances[0]
                val swaggerURL = "${instance.uri}/v3/api-docs/"
                val jsonData = template.getForObject(swaggerURL, Any::class.java)
                val content = getJSON(serviceId, jsonData)
                servicesRegistry.addService(serviceId, content)
            }
        }
    }

    fun getJSON(serviceId: String, jsonData: Any?): String {
        return try {
            ObjectMapper().writeValueAsString(jsonData)
        } catch (e: JsonProcessingException) {
            ""
        }
    }

}