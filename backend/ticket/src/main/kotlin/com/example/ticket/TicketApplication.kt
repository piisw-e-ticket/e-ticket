package com.example.ticket

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient

@SpringBootApplication
@EnableEurekaClient
class TicketApplication

fun main(args: Array<String>) {
    runApplication<TicketApplication>(*args)
}
