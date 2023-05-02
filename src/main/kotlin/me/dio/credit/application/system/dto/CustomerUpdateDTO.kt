package me.dio.credit.application.system.dto

import me.dio.credit.application.system.entity.Customer
import java.math.BigDecimal
import jakarta.validation.constraints.*

data class CustomerUpdateDTO(
        @field:NotEmpty(message = "Please inform the first name") val firstName: String,
        @field:NotEmpty(message = "Please inform the last name") val lastName: String,
        @field:NotNull(message = "Please inform the income") val income: BigDecimal,
        @field:NotEmpty(message = "Please inform the zip code") val zipCode: String,
        @field:NotEmpty(message = "Please inform the street") val street: String
) {
    fun toEntity(customer: Customer): Customer {
        customer.firstName = this.firstName
        customer.lastName = this.lastName
        customer.income = this.income
        customer.address.zipCode = this.zipCode
        customer.address.street = this.street
        return customer
    }
}
