package com.example.auth.model

import java.util.UUID
import javax.persistence.*

@Entity
@Table(name = "token_family")
class TokenFamily {

    @Id
    var id: String = UUID.randomUUID().toString()

    @Column(length = 5000, nullable = false)
    var validToken: String? = null

    var isInvalidated: Boolean = false

}