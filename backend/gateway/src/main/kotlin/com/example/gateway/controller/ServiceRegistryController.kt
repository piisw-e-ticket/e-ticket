package com.example.gateway.controller

import com.example.gateway.config.swagger.ServicesRegistry
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/services-docs/{serviceName}")
class ServiceRegistryController(
    private val servicesRegistry: ServicesRegistry
) {

    @GetMapping
    fun getServiceRegistry(@PathVariable serviceName: String) = servicesRegistry.getServiceRegistry(serviceName)

}