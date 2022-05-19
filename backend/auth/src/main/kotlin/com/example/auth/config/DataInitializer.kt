package com.example.auth.config

import com.example.auth.model.Admin
import com.example.auth.model.Passenger
import com.example.auth.model.TicketCollector
import com.example.auth.repository.UserRepository
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(prefix = "app", name = ["init-test-data"])
class DataInitializer(val userRepository: UserRepository, val encoder: PasswordEncoder) : ApplicationRunner {

    override fun run(args: ApplicationArguments?) {
        val passenger = Passenger("passenger", "passenger@mail.com", encoder.encode("password"))
        val passenger1 = Passenger("passenger1", "passenger1@mail.com", encoder.encode("password"))
                .apply { isEligibleForDiscount = true }
        val passenger2 = Passenger("passenger2", "passenger2@mail.com", encoder.encode("password"))

        val ticketCollector = TicketCollector("ticketcollector", "ticketcollector@mail.com", encoder.encode("password"))
        val ticketCollector1 = TicketCollector("ticketcollector1", "ticketcollector1@mail.com", encoder.encode("password"))
        val ticketCollector2 = TicketCollector("ticketcollector2", "ticketcollector2@mail.com", encoder.encode("password"))

        val admin = Admin("admin", "admin@mail.com", encoder.encode("password"))

        userRepository.saveAll(listOf(passenger, passenger1, passenger2, ticketCollector, ticketCollector1, ticketCollector2, admin))
    }

}
