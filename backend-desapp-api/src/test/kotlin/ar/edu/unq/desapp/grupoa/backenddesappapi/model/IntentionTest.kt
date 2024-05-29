package ar.edu.unq.desapp.grupoa.backenddesappapi.model

import ar.edu.unq.desapp.grupoa.backenddesappapi.model.exceptions.exceptionsIntention.OutOfRangePriceException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.*
import java.time.LocalDateTime

class IntentionTest {

    private lateinit var user: User
    private lateinit var intention: Intention

    @BeforeEach
    fun setUp() {
        user = User(
            name = "John",
            surname = "Doe",
            email = "john.doe@gmail.com",
            address = "123 Main St",
            password = "Password1!",
            cvu = "1234567890123456789012",
            walletAddress = "12345678"
        )

        intention = Intention(
            cryptoactive = CryptoCurrencyEnum.AAVEUSDT,
            amountOfCrypto = 1.0,
            lastQuotation = 50000.0,
            amountInPesos = 50000.0,
            user = user,
            operation = OperationEnum.BUY,
            dateCreated = LocalDateTime.now(),
            available = true
        )
    }

    @Test
    fun validateIntentionDataShouldNotThrowExceptionForPriceWithinRange() {
        val priceForCrypto = 50000.0f
        assertDoesNotThrow { intention.validateIntentionData(priceForCrypto) }
    }

    @Test
    fun validateIntentionDataShouldThrowOutOfRangePriceExceptionForPriceBelowRange() {
        val priceForCrypto = 47000.0f
        assertThrows(OutOfRangePriceException::class.java) { intention.validateIntentionData(priceForCrypto) }
    }

    @Test
    fun validateIntentionDataShouldThrowOutOfRangePriceExceptionForPriceAboveRange() {
        val priceForCrypto = 53000.0f
        assertThrows(OutOfRangePriceException::class.java) { intention.validateIntentionData(priceForCrypto) }
    }

    @Test
    fun validateIntentionDataShouldThrowOutOfRangePriceExceptionForNullPrice() {
        assertThrows(OutOfRangePriceException::class.java) { intention.validateIntentionData(null) }
    }
}
