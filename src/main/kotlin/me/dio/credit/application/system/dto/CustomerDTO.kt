package me.dio.credit.application.system.dto

import me.dio.credit.application.system.entity.Address
import me.dio.credit.application.system.entity.Customer
import org.hibernate.validator.constraints.br.CPF
import java.math.BigDecimal
import javax.validation.constraints.Email
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class CustomerDTO(
        @field:NotEmpty(message = "Please inform the first name") val firstName: String,
        @field:NotEmpty(message = "Please inform the last name") val lastName: String,
        @field:NotEmpty(message = "Please inform the CPF") @field:CPF(message = "Invalid CPF") val cpf: String,
        @field:NotNull(message = "Please inform the income") val income: BigDecimal,
        @field:Email(message = "Please inform a valid email") val email: String,
        @field:NotEmpty(message = "Please inform the password") val password: String,
        @field:NotEmpty(message = "Please inform the zip code") val zipCode: String,
        @field:NotEmpty(message = "Please inform the street") val street: String
) {
    fun toEntity(): Customer = Customer(
            firstName = this.firstName,
            lastName = this.lastName,
            cpf = this.cpf,
            income = this.income,
            email = this.email,
            password = this.password,
            address = Address(
                    zipCode = this.zipCode,
                    street = this.street
            )

    )
}
