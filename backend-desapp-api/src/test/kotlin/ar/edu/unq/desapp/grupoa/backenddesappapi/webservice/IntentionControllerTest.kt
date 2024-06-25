package ar.edu.unq.desapp.grupoa.backenddesappapi.webservice

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.CryptoCurrencyEnum
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.OperationEnum
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.User
import ar.edu.unq.desapp.grupoa.backenddesappapi.service.CryptoService
import ar.edu.unq.desapp.grupoa.backenddesappapi.service.IntentionService
import ar.edu.unq.desapp.grupoa.backenddesappapi.service.UserService
import ar.edu.unq.desapp.grupoa.backenddesappapi.webservice.dtos.CreationIntentionDTO
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
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest
@AutoConfigureMockMvc
class IntentionControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var intentionService: IntentionService

    @MockBean
    private lateinit var cryptoService: CryptoService

    private var userId: Long? = null

    @BeforeEach
    fun setUp() {
        val userToCreate = User(
            "Satoshi",
            "Nakamoto",
            "satonaka@gmail.com",
            "Fake Street 123",
            "Security1234!",
            "0011223344556677889911",
            "01234567",
            5.0,
        )
        userId = userService.createUser(userToCreate).id!!
        Mockito.`when`(cryptoService.getCryptoQuote(CryptoCurrencyEnum.BTCUSDT)).thenReturn(1000F)

    }

    @Test
    @WithMockUser(username = "user", roles = ["USER"])
    fun intentionCorrectlySubmittedReturns201Created() {
        val intention = CreationIntentionDTO(
            CryptoCurrencyEnum.BTCUSDT, 1.5, 1000.0, userId!!, OperationEnum.BUY
        )
        val parsedIntentionData = ObjectMapper().writeValueAsString(intention)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/intentions/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(parsedIntentionData)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("$.userId").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("$.cryptoactive").value("BTCUSDT"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.amountOfCrypto").value(1.5))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastQuotation").value(1000.0))
            .andExpect(MockMvcResultMatchers.jsonPath("$.amountInPesos").value(1627500.0))
            .andExpect(MockMvcResultMatchers.jsonPath("$.userFirstName").value("Satoshi"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.userLastName").value("Nakamoto"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.userAmountOfTransactions").value(0))
            .andExpect(MockMvcResultMatchers.jsonPath("$.userReputation").value(0.0))
            .andExpect(MockMvcResultMatchers.jsonPath("$.dateCreated").isNotEmpty)
    }

    @Test
    @WithMockUser(username = "user", roles = ["USER"])
    fun intentionWithExceededPriceReturns400BadRequest() {
        val intention = CreationIntentionDTO(
            CryptoCurrencyEnum.BTCUSDT, 1.5, 2000.0, userId!!, OperationEnum.BUY
        )
        val parsedIntentionData = ObjectMapper().writeValueAsString(intention)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/intentions/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(parsedIntentionData)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(
                MockMvcResultMatchers.content()
                    .string("Failed to create intention: Price for the intention must be within five percent of the current price for the cryptocurrency.")
            )
    }

    @Test
    @WithMockUser(username = "user", roles = ["USER"])
    fun intentionWithLowPriceReturns400BadRequest() {
        val intention = CreationIntentionDTO(
            CryptoCurrencyEnum.BTCUSDT, 1.5, 500.0, userId!!, OperationEnum.BUY
        )
        val parsedIntentionData = ObjectMapper().writeValueAsString(intention)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/intentions/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(parsedIntentionData)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(
                MockMvcResultMatchers.content()
                    .string("Failed to create intention: Price for the intention must be within five percent of the current price for the cryptocurrency.")
            )
    }

    @Test
    @WithMockUser(username = "user", roles = ["USER"])
    fun intentionWithNonExistentUserIdReturns404NotFound() {
        val intention = CreationIntentionDTO(
            CryptoCurrencyEnum.BTCUSDT, 1.5, 1000.0, 999, OperationEnum.BUY
        )
        val parsedIntentionData = ObjectMapper().writeValueAsString(intention)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/intentions/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(parsedIntentionData)
        )
            .andExpect(MockMvcResultMatchers.status().isNotFound)
            .andExpect(
                MockMvcResultMatchers.content()
                    .string("Didn't found any user with id 999")
            )
    }

    @Test
    fun intentionActiveListReturns200WithEmptyList() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/intentions/all-active")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$").isArray)
            .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty)
    }

    @Test
    fun intentionActiveListReturns200() {
        intentionService.createIntention(
            CryptoCurrencyEnum.BTCUSDT,
            2.0,
            1000.0,
            userId!!,
            OperationEnum.BUY
        )

        mockMvc.perform(
            MockMvcRequestBuilders.get("/intentions/all-active")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$").isArray)
            .andExpect(MockMvcResultMatchers.jsonPath("$[0]").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].userId").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].cryptoactive").value("BTCUSDT"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].amountOfCrypto").value(2.0))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].lastQuotation").value(1000.0))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].amountInPesos").value(2170000.0))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].userFirstName").value("Satoshi"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].userLastName").value("Nakamoto"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].userAmountOfTransactions").value(0))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].userReputation").value(0.0))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].operation").value("BUY"))
    }

    @Test
    fun intentionActiveListReturns200WithUserWithoutOperations() {
        val anotherUser = userService.createUser(
            User(
                "NotSatoshi",
                "NotNakamoto",
                "notsatonaka@gmail.com",
                "Fake Street 123",
                "Security1234!",
                "0011223344556677889912",
                "01234568",
                0.0,
            )
        )

        intentionService.createIntention(
            CryptoCurrencyEnum.BTCUSDT,
            2.0,
            1000.0,
            anotherUser.id!!,
            OperationEnum.BUY
        )

        mockMvc.perform(
            MockMvcRequestBuilders.get("/intentions/all-active")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$").isArray)
            .andExpect(MockMvcResultMatchers.jsonPath("$[0]").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].userFirstName").value("NotSatoshi"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].userLastName").value("NotNakamoto"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].userReputation").value("No operations"))
    }

    @AfterEach
    fun cleanUp() {
        intentionService.deleteAll()
        userService.deleteAll()
    }
}