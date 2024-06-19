package ar.edu.unq.desapp.grupoa.backenddesappapi.webservice

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.CryptoCurrencyEnum
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.OperationEnum
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.User
import ar.edu.unq.desapp.grupoa.backenddesappapi.service.CryptoService
import ar.edu.unq.desapp.grupoa.backenddesappapi.service.IntentionService
import ar.edu.unq.desapp.grupoa.backenddesappapi.service.TransactionService
import ar.edu.unq.desapp.grupoa.backenddesappapi.service.UserService
import ar.edu.unq.desapp.grupoa.backenddesappapi.webservice.dtos.UserDTO
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var intentionService: IntentionService

    @Autowired
    private lateinit var transactionService: TransactionService

    @MockBean
    private lateinit var cryptoService: CryptoService

    @BeforeEach
    fun setUp() {
        Mockito.`when`(cryptoService.getCryptoQuote(CryptoCurrencyEnum.BTCUSDT)).thenReturn(1000F)
    }

    @Test
    fun userRegistrationReturns201Created() {
        val userData = UserDTO(
            name = "Satoshi",
            surname = "Nakamoto",
            email = "satonaka@gmail.com",
            address = "Shibuya 123",
            password = "123456sD!",
            cvu = "0011223344556677889911",
            walletAddress = "12345678",
        )
        val parsedUserData = ObjectMapper().writeValueAsString(userData)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(parsedUserData)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Satoshi"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.surname").value("Nakamoto"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("satonaka@gmail.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.address").value("Shibuya 123"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.cvu").value("0011223344556677889911"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.walletAddress").value("12345678"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.password").doesNotExist())
    }

    @Test
    fun userRegistrationReturns400WithInvalidData() {
        val userData = UserDTO(
            name = "S",
            surname = "Nakamoto",
            email = "satonaka@gmail.com",
            address = "Shibuya 123",
            password = "123456sD!",
            cvu = "0011223344556677889911",
            walletAddress = "12345678",
        )
        val parsedUserData = ObjectMapper().writeValueAsString(userData)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(parsedUserData)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun userCreatesTransactionCorrectlyAndReturns201Created() {
        val user = userService.createUser(
            User(
                "NotSatoshi",
                "NotNakamoto",
                "notsatonaka@gmail.com",
                "Fake Street 123",
                "Security1234!",
                "0011223344556677889912",
                "01234567",
                0.0,
            )
        )
        val anotherUser = userService.createUser(
            User(
                "Itachi",
                "Uchiha",
                "longlivesasuke@gmail.com",
                "Konoha Barrio Uchiha",
                "Edotensei1234!=",
                "2222222222222222222222",
                "01234568",
            )
        )

        val intention = intentionService.createIntention(
            CryptoCurrencyEnum.BTCUSDT,
            2.0,
            1000.0,
            user.id!!,
            OperationEnum.BUY
        )

        mockMvc.perform(
            MockMvcRequestBuilders.post("/users/${anotherUser.id!!}/createTransaction/${intention.id!!}")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("$.userId").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("$.cryptoactive").value("BTCUSDT"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.amountOfCrypto").value(2.0))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastQuotation").value(1000.0))
            .andExpect(MockMvcResultMatchers.jsonPath("$.userFirstName").value(anotherUser.name))
            .andExpect(MockMvcResultMatchers.jsonPath("$.userLastName").value(anotherUser.surname))
            .andExpect(MockMvcResultMatchers.jsonPath("$.userAmountOfTransactions").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.userReputation").value("No operations"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.address").value(anotherUser.cvu))
            .andExpect(MockMvcResultMatchers.jsonPath("$.action").value("Please confirm reception"))
    }

    @Test
    fun userCreatesTransactionCorrectlyButItAutomaticallyCancels() {
        val user = userService.createUser(
            User(
                "NotSatoshi",
                "NotNakamoto",
                "notsatonaka@gmail.com",
                "Fake Street 123",
                "Security1234!",
                "0011223344556677889912",
                "01234567",
                0.0,
            )
        )
        val anotherUser = userService.createUser(
            User(
                "Itachi",
                "Uchiha",
                "longlivesasuke@gmail.com",
                "Konoha Barrio Uchiha",
                "Edotensei1234!=",
                "2222222222222222222222",
                "01234568",
            )
        )

        val intention = intentionService.createIntention(
            CryptoCurrencyEnum.BTCUSDT,
            2.0,
            1000.0,
            user.id!!,
            OperationEnum.BUY
        )

        Mockito.`when`(cryptoService.getCryptoQuote(CryptoCurrencyEnum.BTCUSDT)).thenReturn(1100F)
        mockMvc.perform(
            MockMvcRequestBuilders.post("/users/${anotherUser.id!!}/createTransaction/${intention.id!!}")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("$.userId").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("$.cryptoactive").value("BTCUSDT"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.amountOfCrypto").value(2.0))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastQuotation").value(1100.0))
            .andExpect(MockMvcResultMatchers.jsonPath("$.userFirstName").value(anotherUser.name))
            .andExpect(MockMvcResultMatchers.jsonPath("$.userLastName").value(anotherUser.surname))
            .andExpect(MockMvcResultMatchers.jsonPath("$.userAmountOfTransactions").value(0))
            .andExpect(MockMvcResultMatchers.jsonPath("$.userReputation").value("No operations"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.address").value(null))
            .andExpect(MockMvcResultMatchers.jsonPath("$.action").value("Transaction cancelled"))
    }

    @Test
    fun nonExistentUserCreatesTransactionAndReturns404() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/users/999/createTransaction/123465")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isNotFound)
            .andExpect(
                MockMvcResultMatchers.content().string("User not found with id: 999")
            )
    }

    @AfterEach
    fun cleanUp() {
        transactionService.deleteAll()
        intentionService.deleteAll()
        userService.deleteAll()
    }
}