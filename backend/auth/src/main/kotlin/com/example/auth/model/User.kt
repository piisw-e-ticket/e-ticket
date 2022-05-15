package com.example.auth.model

import javax.persistence.*

@Entity
@Table(name = "`user`")
class User(
    val username: String,
    val email: String,
    val password: String
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    var active: Boolean = true

    @ElementCollection(fetch = FetchType.EAGER)
    val roles: MutableList<Role> = ArrayList()

}

enum class Role {
    PASSENGER, TICKET_COLLECTOR, ADMIN
}