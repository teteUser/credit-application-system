package me.dio.credit.application.system.controller

import com.fasterxml.jackson.databind.ObjectMapper
import me.dio.credit.application.system.dto.CustomerDTO
import me.dio.credit.application.system.dto.CustomerUpdateDTO
import me.dio.credit.application.system.entity.Customer
import me.dio.credit.application.system.repository.CustomerRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.math.BigDecimal
import java.util.*

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration
class CustomerControllerTest {

    @Autowired
    private lateinit var customerRepository: CustomerRepository

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    companion object {
        const val URL: String = "/api/customers"
    }

    @BeforeEach
    fun setup() = customerRepository.deleteAll()

    @AfterEach
    fun tearDown() = customerRepository.deleteAll()

    @Test
    fun shouldCreateACustomerAndReturnStatus201() {
        //given
        val customerDTO: CustomerDTO = builderCustomerDTO()
        val valueAsString: String = objectMapper.writeValueAsString(customerDTO)
        //when
        //then
        mockMvc.perform(
                MockMvcRequestBuilders.post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(valueAsString)
        )
                .andExpect(MockMvcResultMatchers.status().isCreated)
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Tete"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Feliz"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value("28475934625"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("tete.feliz@email.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.income").value("1000.0"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.zipCode").value("000000"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.street").value("Avenida do Tete, 123"))
                .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun shouldNotSaveACustomerWithSameCPFAndShouldReturnStatus409() {
        //given
        customerRepository.save(builderCustomerDTO().toEntity())
        val customerDTO: CustomerDTO = builderCustomerDTO()
        val valueAsString: String = objectMapper.writeValueAsString(customerDTO)
        //when
        //then
        mockMvc.perform(
                MockMvcRequestBuilders.post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(valueAsString)
        )
                .andExpect(MockMvcResultMatchers.status().isConflict)
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Conflict! Consult the documentation"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(409))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.exception")
                                .value("class org.springframework.dao.DataIntegrityViolationException")
                )
                .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
                .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun shouldNotSaveACustomerWithEmptyFirstNameAndShouldReturnStatus400() {
        //given
        val customerDTO: CustomerDTO = builderCustomerDTO(firstName = "")
        val valueAsString: String = objectMapper.writeValueAsString(customerDTO)
        //when
        //then
        mockMvc.perform(
                MockMvcRequestBuilders.post(URL)
                        .content(valueAsString)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.exception")
                                .value("class org.springframework.web.bind.MethodArgumentNotValidException")
                )
                .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
                .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun shouldFindCustomerByIdAndReturnStatus200() {
        //given
        val customer: Customer = customerRepository.save(builderCustomerDTO().toEntity())
        //when
        //then
        mockMvc.perform(
                MockMvcRequestBuilders.get("$URL/${customer.id}")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Tete"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Feliz"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value("28475934625"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("tete.feliz@email.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.income").value("1000.0"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.zipCode").value("000000"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.street").value("Avenida do Tete, 123"))
                //.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun shouldNotFindCustomerWithInvalidIdAndShouldReturnStatus400() {
        //given
        val invalidId: Long = 2L
        //when
        //then
        mockMvc.perform(
                MockMvcRequestBuilders.get("$URL/$invalidId")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.exception")
                                .value("class me.dio.credit.application.system.exception.BusinessException")
                )
                .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
                .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun shouldDeleteCustomerByIdAndReturnStatus204() {
        //given
        val customer: Customer = customerRepository.save(builderCustomerDTO().toEntity())
        //when
        //then
        mockMvc.perform(
                MockMvcRequestBuilders.delete("$URL/${customer.id}")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(MockMvcResultMatchers.status().isNoContent)
                .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun shouldNotDeleteCustomerByIdAndReturnStatus400() {
        //given
        val invalidId: Long = Random().nextLong()
        //when
        //then
        mockMvc.perform(
                MockMvcRequestBuilders.delete("$URL/${invalidId}")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.exception")
                                .value("class me.dio.credit.application.system.exception.BusinessException")
                )
                .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
                .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun shouldUpdateACustomerAndShouldReturnStatus200() {
        //given
        val customer: Customer = customerRepository.save(builderCustomerDTO().toEntity())
        val customerUpdateDTO: CustomerUpdateDTO = builderCustomerUpdateDTO()
        val valueAsString: String = objectMapper.writeValueAsString(customerUpdateDTO)
        //when
        //then
        mockMvc.perform(
                MockMvcRequestBuilders.patch("$URL?customerId=${customer.id}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(valueAsString)
        )
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("TeteUpdate"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("FelizUpdate"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value("28475934625"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("tete.feliz@email.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.income").value("5000.0"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.zipCode").value("075075"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.street").value("Rua do Tete, 500"))
                .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun shouldNotUpdateACustomerWithInvalidIdAndReturnStatus400() {
        //given
        val invalidId: Long = Random().nextLong()
        val customerUpdateDTO: CustomerUpdateDTO = builderCustomerUpdateDTO()
        val valueAsString: String = objectMapper.writeValueAsString(customerUpdateDTO)
        //when
        //then
        mockMvc.perform(
                MockMvcRequestBuilders.patch("$URL?customerId=$invalidId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(valueAsString)
        )
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
                .andExpect(
                        MockMvcResultMatchers.jsonPath("$.exception")
                                .value("class me.dio.credit.application.system.exception.BusinessException")
                )
                .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
                .andDo(MockMvcResultHandlers.print())
    }


    private fun builderCustomerDTO(
            firstName: String = "Tete",
            lastName: String = "Feliz",
            cpf: String = "28475934625",
            email: String = "tete.feliz@email.com",
            income: BigDecimal = BigDecimal.valueOf(1000.0),
            password: String = "123456",
            zipCode: String = "000000",
            street: String = "Avenida do Tete, 123",
    ) = CustomerDTO(
            firstName = firstName,
            lastName = lastName,
            cpf = cpf,
            email = email,
            income = income,
            password = password,
            zipCode = zipCode,
            street = street
    )

    private fun builderCustomerUpdateDTO(
            firstName: String = "TeteUpdate",
            lastName: String = "FelizUpdate",
            income: BigDecimal = BigDecimal.valueOf(5000.0),
            zipCode: String = "075075",
            street: String = "Rua do Tete, 500"
    ): CustomerUpdateDTO = CustomerUpdateDTO(
            firstName = firstName,
            lastName = lastName,
            income = income,
            zipCode = zipCode,
            street = street
    )


}