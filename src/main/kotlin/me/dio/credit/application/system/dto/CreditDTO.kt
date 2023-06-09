package me.dio.credit.application.system.dto

import me.dio.credit.application.system.entity.Credit
import me.dio.credit.application.system.entity.Customer
import java.math.BigDecimal
import java.time.LocalDate
import jakarta.validation.constraints.*

data class CreditDTO(
        @field:NotNull(message = "Please inform the credit value") val creditValue: BigDecimal,
        @field:Future(message = "Date of first installment cannot be a date from the past") val dayFirstInstallment: LocalDate,
        @field:Min(1) @field:Max(48) val numberOfInstallments: Int,
        @field:NotNull(message = "Please inform a valid customer Id") val customerId: Long
) {
    fun toEntity(): Credit = Credit(
            creditValue = this.creditValue,
            dayFirstInstallment = this.dayFirstInstallment,
            numberOfInstallments = this.numberOfInstallments,
            customer = Customer(id = this.customerId)
    )
}
