package me.dio.credit.application.system.service.impl

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import me.dio.credit.application.system.entity.Address
import me.dio.credit.application.system.entity.Credit
import me.dio.credit.application.system.entity.Customer
import me.dio.credit.application.system.enums.Status
import me.dio.credit.application.system.exception.BusinessException
import me.dio.credit.application.system.repository.CreditRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
internal class CreditServiceTest {

    @MockK
    lateinit var creditRepository: CreditRepository

    @MockK
    lateinit var customerService: CustomerService

    @InjectMockKs
    lateinit var creditService: CreditService

    @Test
    fun shouldSaveCredit() {
        //given
        val fakeCustomer: Customer = buildCustomer()
        every { customerService.findById(any()) } returns fakeCustomer
        val fakeCredit: Credit = buildCredit(customer = fakeCustomer)
        every { creditRepository.save(any()) } returns fakeCredit
        //when
        val actual: Credit = creditService.save(fakeCredit)
        //then
        Assertions.assertThat(actual).isNotNull
        Assertions.assertThat(actual).isSameAs(fakeCredit)
        verify(exactly = 1) { creditRepository.save(fakeCredit) }
    }

    @Test
    fun shouldFindAllCreditsByCustomerId() {
        //given
        val fakeCredit: Credit = buildCredit(customer = buildCustomer())
        every { creditRepository.findAllByCustomerId(any()) } returns listOf(fakeCredit)
        //when
        val actual: List<Credit> = creditService.findAllByCustomer(1L)
        //then
        verify(exactly = 1) { creditRepository.findAllByCustomerId(any()) }
        Assertions.assertThat(actual).isNotEmpty
        Assertions.assertThat(actual).hasSize(1)
    }

    @Test
    fun shouldFindACreditByCreditCode() {
        //given
        val fakeCredit: Credit = buildCredit(customer = buildCustomer())
        every { creditRepository.findByCreditCode(any()) } returns fakeCredit
        //when
        val actual: Credit = creditService.findByCreditCode(1L, UUID.randomUUID())
        //then
        Assertions.assertThat(actual).isNotNull
        verify(exactly = 1) { creditRepository.findByCreditCode(any()) }
    }

    @Test
    fun shouldThrowBusinessExceptionWhenTryingToFindACreditByInvalidCreditCode() {
        //given
        every { creditRepository.findByCreditCode(any()) } returns null
        val fakeCreditCode: UUID = UUID.randomUUID()
        //when
        //then
        Assertions.assertThatExceptionOfType(BusinessException::class.java)
                .isThrownBy { creditService.findByCreditCode(1L, fakeCreditCode) }
                .withMessage("Credit Code $fakeCreditCode not found")
        verify(exactly = 1) { creditRepository.findByCreditCode(any()) }
    }

    @Test
    fun shouldThrowBusinessExceptionWhenTryingToFindACreditByInvalidCustomerId() {
        //given
        val fakeCreditCode: UUID = UUID.randomUUID()
        val fakeCredit: Credit = buildCredit(customer = buildCustomer())
        every { creditRepository.findByCreditCode(any()) } returns fakeCredit
        //when
        //then
        Assertions.assertThatExceptionOfType(BusinessException::class.java)
                .isThrownBy { creditService.findByCreditCode(5L, fakeCreditCode) }
                .withMessage("Contact admin")
        verify(exactly = 1) { creditRepository.findByCreditCode(any()) }
    }

    private fun buildCustomer(
            firstName: String = "Tete",
            lastName: String = "Feliz",
            cpf: String = "28475934625",
            email: String = "camila@gmail.com",
            password: String = "12345",
            zipCode: String = "12345",
            street: String = "Rua do Tete",
            income: BigDecimal = BigDecimal.valueOf(2000.0),
            id: Long = 1L
    ) = Customer(
            firstName = firstName,
            lastName = lastName,
            cpf = cpf,
            email = email,
            password = password,
            address = Address(
                    zipCode = zipCode,
                    street = street,
            ),
            income = income,
            id = id
    )

    private fun buildCredit(
            creditCode: UUID = UUID.randomUUID(),
            creditValue: BigDecimal = BigDecimal.ZERO,
            dayFirstInstallment: LocalDate = LocalDate.now(),
            numberOfInstallments: Int = 0,
            status: Status = Status.IN_PROGRESS,
            customer: Customer? = null,
            id: Long = 1L
    ) = Credit(
            creditCode = creditCode,
            creditValue = creditValue,
            dayFirstInstallment = dayFirstInstallment,
            numberOfInstallments = numberOfInstallments,
            status = status,
            customer = customer,
            id = id
    )


}