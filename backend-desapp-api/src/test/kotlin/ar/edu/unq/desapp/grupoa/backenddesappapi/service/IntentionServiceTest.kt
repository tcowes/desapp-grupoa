package ar.edu.unq.desapp.grupoa.backenddesappapi.service

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.CryptoCurrencyEnum
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.OperationEnum
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.User
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions.exceptionsIntention.OutOfRangePriceException
import ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions.exceptionsIntention.UsernameIntentException
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IntentionServiceTest {

    @Autowired
    private lateinit var intentionService: IntentionService

    @Autowired
    private lateinit var userService: UserService

    @MockBean
    private lateinit var cryptoService: CryptoService

    private lateinit var user: User

    @BeforeEach
    fun setUp() {
        Mockito.`when`(cryptoService.getCryptoQuote(CryptoCurrencyEnum.BTCUSDT)).thenReturn(null)
        Mockito.`when`(cryptoService.getCryptoQuote(CryptoCurrencyEnum.ETHUSDT)).thenReturn(1000F)

        user = userService.createUser(
            User(
                "Satoshi",
                "Nakamoto",
                "satonaka@gmail.com",
                "Fake Street 123",
                "Security1234!",
                "0011223344556677889911",
                "01234567",
                5.0,
            )
        )
    }

    @Test
    fun anIntentionIsCreatedSuccessfully() {
        val intention = intentionService.createIntention(
            CryptoCurrencyEnum.ETHUSDT,
            2.0,
            1000.0,
            user.id!!,
            OperationEnum.BUY
        )
        val intentionCreated = intentionService.getIntentionById(intention.id!!)
        assertEquals(intentionCreated.cryptoactive, intention.cryptoactive)
        assertEquals(intentionCreated.amountOfCrypto, intention.amountOfCrypto)
        assertEquals(intentionCreated.lastQuotation, intention.lastQuotation)
        assertEquals(intentionCreated.amountInPesos, intention.amountInPesos)
        assertEquals(intentionCreated.user.id, intention.user.id)
        assertEquals(intentionCreated.operation, intention.operation)

    }

    @Test
    fun anIntentExceedingPriceLimitThrowsError() {
        val error = org.junit.jupiter.api.assertThrows<OutOfRangePriceException> {
            intentionService.createIntention(
                CryptoCurrencyEnum.ETHUSDT,
                2.0,
                2000.0,
                user.id!!,
                OperationEnum.BUY
            )
        }

        assertEquals(
            "Price for the intention must be within five percent of the current price for the cryptocurrency.",
            error.message
        )

    }

    @Test
    fun anIntentWithPriceBelowLimitThrowsError() {
        val error = org.junit.jupiter.api.assertThrows<OutOfRangePriceException> {
            intentionService.createIntention(
                CryptoCurrencyEnum.ETHUSDT,
                2.0,
                900.0,
                user.id!!,
                OperationEnum.BUY
            )
        }

        assertEquals(
            "Price for the intention must be within five percent of the current price for the cryptocurrency.",
            error.message
        )

    }

    @Test
    fun anIntentWithUnkownPriceFromCryptocurrencyThrowsError() {
        val error = org.junit.jupiter.api.assertThrows<OutOfRangePriceException> {
            intentionService.createIntention(
                CryptoCurrencyEnum.ETHUSDT,
                2.0,
                900.0,
                user.id!!,
                OperationEnum.BUY
            )
        }

        assertEquals(
            "Price for the intention must be within five percent of the current price for the cryptocurrency.",
            error.message
        )

    }

    @Test
    fun anIntentIsCreatedUnsuccessfullyByAnUnRegisteredUser() {
        val unregisteredUserId = 123L

        val error = org.junit.jupiter.api.assertThrows<UsernameIntentException> {
            intentionService.createIntention(
                CryptoCurrencyEnum.BTCUSDT,
                2.0,
                1000.0,
                unregisteredUserId,
                OperationEnum.BUY
            )
        }

        assertEquals(
            "Error: user with id $unregisteredUserId is not registered to make a BUY/SELL Intent",
            error.message
        )

    }

    @Test
    fun listActiveIntentions() {
        val intention1 = intentionService.createIntention(
            CryptoCurrencyEnum.ETHUSDT,
            2.0,
            1000.0,
            user.id!!,
            OperationEnum.BUY
        )
        val intention2 = intentionService.createIntention(
            CryptoCurrencyEnum.ETHUSDT,
            1.0,
            1020.0,
            user.id!!,
            OperationEnum.SELL
        )
        val intention3 = intentionService.createIntention(
            CryptoCurrencyEnum.ETHUSDT,
            1.0,
            999.0,
            user.id!!,
            OperationEnum.SELL
        )
        val intentions = intentionService.listActiveIntentions()

        assertTrue(intentions.size == 3)
        assertTrue(intentions.any { it.id == intention1.id && it.user.id == user.id })
        assertTrue(intentions.any { it.id == intention2.id && it.user.id == user.id })
        assertTrue(intentions.any { it.id == intention3.id && it.user.id == user.id })
    }

    @Test
    fun listActiveIntentionsWontShowInactiveIntentios() {
        val intention1 = intentionService.createIntention(
            CryptoCurrencyEnum.ETHUSDT,
            2.0,
            1000.0,
            user.id!!,
            OperationEnum.BUY
        )
        val intention2 = intentionService.createIntention(
            CryptoCurrencyEnum.ETHUSDT,
            1.0,
            1020.0,
            user.id!!,
            OperationEnum.SELL
        )
        var intentions = intentionService.listActiveIntentions()

        assertTrue(intentions.size == 2)
        assertTrue(intentions.any { it.id == intention1.id && it.user.id == user.id })
        assertTrue(intentions.any { it.id == intention2.id && it.user.id == user.id })

        intention2.available = false
        intentionService.updateIntention(intention2)

        intentions = intentionService.listActiveIntentions()

        assertTrue(intentions.size == 1)
    }

    @Test
    fun listActiveIntentionsReturnsEmptyListIfNoIntentionsRegistered() {
        assertTrue(intentionService.listActiveIntentions().isEmpty())
    }

    @AfterEach
    fun cleanUp() {
        intentionService.deleteAll()
        userService.deleteAll()
    }
}