package com.example.ticket.controller

import com.example.ticket.model.Role
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

fun addRoleToRequestHeaders(role: Role) {
    ((RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes)
        .request as MockHttpServletRequest).addHeader("user-role", role)
}
