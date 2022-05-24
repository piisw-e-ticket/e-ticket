package com.example.ticket.client

import com.example.ticket.dto.UserInfoDto
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping

@FeignClient("auth", url = "http://localhost:8080/auth")
interface AuthClient {

    @GetMapping("/info")
    fun getUserInfo(): UserInfoDto

}