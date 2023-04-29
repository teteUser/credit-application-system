package me.dio.credit.application.system.entity

import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
data class Address(

        @Column(nullable = false)
        var zipCode: String = "",

        @Column(nullable = false)
        var street: String = "",
)
