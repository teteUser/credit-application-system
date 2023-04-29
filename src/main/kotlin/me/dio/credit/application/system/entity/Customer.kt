package me.dio.credit.application.system.entity

import org.hibernate.validator.constraints.br.CPF
import javax.persistence.*

@Entity
data class Customer(

        @Column(nullable = false)
        var firstName: String = "",

        @Column(nullable = false)
        var lastName: String = "",

        @CPF
        @Column(nullable = false, unique = true)
        var cpf: String,

        @Column(nullable = false, unique = true)
        var email: String = "",

        @Column(nullable = false)
        var password: String = "",

        @Column(nullable = false)
        @Embedded
        var address: Address = Address(),

        @Column(nullable = false)
        @OneToMany(fetch = FetchType.LAZY, cascade = arrayOf(CascadeType.REMOVE), mappedBy = "customer")
        var credits: List<Credit> = mutableListOf(),

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long? = null
)
