package com.example.webserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient

@SpringBootApplication
@EnableEurekaClient
class WebServerApplication

fun main(args: Array<String>) {
    runApplication<WebServerApplication>(*args)
}
